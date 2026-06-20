package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.dto.ConsultationUpdateRequest;
import com.tcm.platform.dto.ConsultationWorkspaceRecord;
import com.tcm.platform.entity.Account;
import com.tcm.platform.entity.Consultation;
import com.tcm.platform.entity.Department;
import com.tcm.platform.entity.User;
import com.tcm.platform.mapper.AccountMapper;
import com.tcm.platform.mapper.ConsultationMapper;
import com.tcm.platform.mapper.DepartmentMapper;
import com.tcm.platform.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConsultationWorkspaceService {

    private static final String PENDING_STATUS = "待接诊";
    private static final String COMPLETED_STATUS = "已完成";
    private static final String APPROVED_STATUS = "APPROVED";
    private static final String GENERAL_DEPARTMENT_CODE = "general";
    private static final Set<String> VALID_POOL_SCOPES = Set.of("all", "department", "general");
    private static final Set<String> VALID_STATUSES = Set.of("待接诊", "接诊中", "已完成");
    private static final Set<String> VALID_URGENCIES = Set.of("普通", "紧急", "非常紧急");

    private final ConsultationMapper consultationMapper;
    private final UserMapper userMapper;
    private final AccountMapper accountMapper;
    private final DepartmentMapper departmentMapper;

    public ConsultationWorkspaceService(
            ConsultationMapper consultationMapper,
            UserMapper userMapper,
            AccountMapper accountMapper,
            DepartmentMapper departmentMapper
    ) {
        this.consultationMapper = consultationMapper;
        this.userMapper = userMapper;
        this.accountMapper = accountMapper;
        this.departmentMapper = departmentMapper;
    }

    public Page<ConsultationWorkspaceRecord> listForAdmin(
            long current,
            long size,
            String status,
            String urgency,
            String keyword,
            Long doctorId,
            Boolean unassigned,
            Long departmentId
    ) {
        LambdaQueryWrapper<Consultation> query = baseQuery(current, size, status, urgency, keyword);
        query.eq(doctorId != null, Consultation::getDoctorId, doctorId)
                .isNull(Boolean.TRUE.equals(unassigned), Consultation::getDoctorId)
                .eq(departmentId != null, Consultation::getDepartmentId, departmentId);
        return loadRecords(current, size, query);
    }

    public Page<ConsultationWorkspaceRecord> listDepartmentPool(
            long current,
            long size,
            String urgency,
            String keyword,
            String scope,
            Long doctorId
    ) {
        User doctor = requireActiveDoctor(doctorId);
        Department generalDepartment = requireGeneralDepartment();
        String poolScope = hasText(scope) ? scope : "all";
        if (!VALID_POOL_SCOPES.contains(poolScope)) {
            throw new IllegalArgumentException("无效的问诊池范围");
        }

        LambdaQueryWrapper<Consultation> query =
                baseQuery(current, size, PENDING_STATUS, urgency, keyword);
        query.isNull(Consultation::getDoctorId);
        if ("department".equals(poolScope)) {
            query.eq(Consultation::getDepartmentId, doctor.getDepartmentId());
        } else if ("general".equals(poolScope)) {
            query.eq(Consultation::getDepartmentId, generalDepartment.getId());
        } else if (Objects.equals(doctor.getDepartmentId(), generalDepartment.getId())) {
            query.eq(Consultation::getDepartmentId, generalDepartment.getId());
        } else {
            query.and(wrapper -> wrapper
                    .eq(Consultation::getDepartmentId, doctor.getDepartmentId())
                    .or()
                    .eq(Consultation::getDepartmentId, generalDepartment.getId()));
        }
        return loadRecords(current, size, query);
    }

    public Page<ConsultationWorkspaceRecord> listMine(
            long current,
            long size,
            String status,
            String urgency,
            String keyword,
            Long doctorId
    ) {
        requireActiveDoctor(doctorId);
        LambdaQueryWrapper<Consultation> query = baseQuery(current, size, status, urgency, keyword);
        query.eq(Consultation::getDoctorId, doctorId);
        return loadRecords(current, size, query);
    }

    @Transactional
    public Consultation assign(Long consultationId, Long doctorId) {
        Consultation consultation = requireConsultation(consultationId);
        if (COMPLETED_STATUS.equals(consultation.getStatus())) {
            throw new IllegalArgumentException("已完成问诊不能重新分配");
        }
        if (doctorId != null) {
            requireEnabledDoctor(doctorId);
        }

        if (!Objects.equals(consultation.getDoctorId(), doctorId)) {
            if (consultationMapper.updateAssignment(consultationId, doctorId, PENDING_STATUS) != 1) {
                throw new IllegalStateException("问诊分配更新失败");
            }
            consultation.setDoctorId(doctorId);
            consultation.setStatus(PENDING_STATUS);
        }
        return consultation;
    }

    @Transactional
    public Consultation claim(Long consultationId, Long doctorId) {
        User doctor = requireActiveDoctor(doctorId);
        Department generalDepartment = requireGeneralDepartment();
        Consultation consultation = requireConsultation(consultationId);
        if (COMPLETED_STATUS.equals(consultation.getStatus())) {
            throw new IllegalArgumentException("已完成问诊不能认领");
        }
        if (consultation.getDoctorId() != null) {
            throw new IllegalArgumentException("该问诊单已被其他医生认领");
        }
        if (!Objects.equals(consultation.getDepartmentId(), doctor.getDepartmentId())
                && !Objects.equals(consultation.getDepartmentId(), generalDepartment.getId())) {
            throw new IllegalArgumentException("只能认领本科室或综合咨询问诊");
        }

        if (consultationMapper.claimIfUnassigned(consultationId, doctorId) == 1) {
            return requireConsultation(consultationId);
        }

        throw new IllegalArgumentException("该问诊单已被其他医生认领");
    }

    @Transactional
    public Consultation updateAsDoctor(
            Long consultationId,
            ConsultationUpdateRequest request,
            Long doctorId
    ) {
        Consultation consultation = requireConsultation(consultationId);
        if (!doctorId.equals(consultation.getDoctorId())) {
            throw new IllegalArgumentException("该问诊单未分配给当前医生");
        }
        validateOptionalStatus(request.getStatus());

        if (hasText(request.getStatus())) {
            consultation.setStatus(request.getStatus());
        }
        if (request.getDoctorNote() != null) {
            consultation.setDoctorNote(request.getDoctorNote());
        }
        if (request.getFollowUpAt() != null) {
            consultation.setFollowUpAt(request.getFollowUpAt());
        }
        update(consultation);
        return consultation;
    }

    @Transactional
    public Consultation updateDepartment(Long consultationId, Long departmentId) {
        Consultation consultation = requireConsultation(consultationId);
        if (COMPLETED_STATUS.equals(consultation.getStatus())) {
            throw new IllegalArgumentException("已完成问诊不能修改科室");
        }
        Department department = requireEnabledDepartment(departmentId);
        consultation.setDepartmentId(department.getId());
        consultation.setDepartmentName(department.getName());
        update(consultation);
        return consultation;
    }

    private Page<ConsultationWorkspaceRecord> loadRecords(
            long current,
            long size,
            LambdaQueryWrapper<Consultation> query
    ) {
        Page<Consultation> page = consultationMapper.selectPage(new Page<>(current, size), query);
        Set<Long> doctorIds = page.getRecords().stream()
                .map(Consultation::getDoctorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, User> doctors = doctorIds.isEmpty()
                ? Collections.emptyMap()
                : userMapper.selectBatchIds(doctorIds).stream()
                        .collect(Collectors.toMap(User::getId, Function.identity()));
        Set<Long> departmentIds = page.getRecords().stream()
                .map(Consultation::getDepartmentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Department> departments = departmentIds.isEmpty()
                ? Collections.emptyMap()
                : departmentMapper.selectBatchIds(departmentIds).stream()
                        .collect(Collectors.toMap(Department::getId, Function.identity()));

        List<ConsultationWorkspaceRecord> records = page.getRecords().stream()
                .map(item -> toRecord(
                        item,
                        doctors.get(item.getDoctorId()),
                        departments.get(item.getDepartmentId())
                ))
                .toList();
        Page<ConsultationWorkspaceRecord> result = new Page<>(current, size, page.getTotal());
        result.setRecords(records);
        return result;
    }

    private ConsultationWorkspaceRecord toRecord(
            Consultation consultation,
            User doctor,
            Department department
    ) {
        ConsultationWorkspaceRecord record = new ConsultationWorkspaceRecord();
        BeanUtils.copyProperties(consultation, record);
        if (department != null) {
            record.setDepartmentName(department.getName());
        }
        if (doctor != null) {
            record.setDoctorName(doctor.getDisplayName());
            record.setDoctorDepartment(doctor.getDepartment());
        }
        return record;
    }

    private LambdaQueryWrapper<Consultation> baseQuery(
            long current,
            long size,
            String status,
            String urgency,
            String keyword
    ) {
        validatePage(current, size);
        validateOptionalStatus(status);
        if (hasText(urgency) && !VALID_URGENCIES.contains(urgency)) {
            throw new IllegalArgumentException("无效的紧急度");
        }

        return new LambdaQueryWrapper<Consultation>()
                .eq(hasText(status), Consultation::getStatus, status)
                .eq(hasText(urgency), Consultation::getUrgency, urgency)
                .and(hasText(keyword), wrapper -> wrapper
                        .like(Consultation::getPatientName, keyword)
                        .or()
                        .like(Consultation::getSymptoms, keyword))
                .orderByDesc(Consultation::getCreatedAt);
    }

    private Consultation requireConsultation(Long id) {
        Consultation consultation = consultationMapper.selectById(id);
        if (consultation == null) {
            throw new IllegalArgumentException("问诊单不存在");
        }
        return consultation;
    }

    private void requireEnabledDoctor(Long doctorId) {
        User doctor = userMapper.selectById(doctorId);
        if (doctor == null || !"doctor".equals(doctor.getRole())) {
            throw new IllegalArgumentException("请选择有效的医生");
        }
        Account account = accountMapper.selectById(doctor.getAccountId());
        if (account == null || Boolean.FALSE.equals(account.getEnabled())) {
            throw new IllegalArgumentException("该医生账号已停用");
        }
    }

    private User requireActiveDoctor(Long doctorId) {
        User doctor = userMapper.selectById(doctorId);
        if (doctor == null || !"doctor".equals(doctor.getRole())) {
            throw new IllegalArgumentException("当前账号不是有效医生");
        }
        if (!APPROVED_STATUS.equals(doctor.getApprovalStatus())) {
            throw new IllegalArgumentException("医生账号尚未通过审核");
        }
        if (doctor.getDepartmentId() == null) {
            throw new IllegalArgumentException("医生尚未配置所属科室");
        }
        Account account = accountMapper.selectById(doctor.getAccountId());
        if (account == null || Boolean.FALSE.equals(account.getEnabled())) {
            throw new IllegalArgumentException("医生账号已停用");
        }
        return doctor;
    }

    private Department requireGeneralDepartment() {
        Department department = departmentMapper.selectOne(
                new LambdaQueryWrapper<Department>()
                        .eq(Department::getCode, GENERAL_DEPARTMENT_CODE)
                        .eq(Department::getEnabled, true)
        );
        if (department == null) {
            throw new IllegalStateException("综合咨询科室未配置");
        }
        return department;
    }

    private Department requireEnabledDepartment(Long departmentId) {
        if (departmentId == null) {
            throw new IllegalArgumentException("请选择问诊科室");
        }
        Department department = departmentMapper.selectById(departmentId);
        if (department == null || Boolean.FALSE.equals(department.getEnabled())) {
            throw new IllegalArgumentException("请选择有效科室");
        }
        return department;
    }

    private void update(Consultation consultation) {
        if (consultationMapper.updateById(consultation) != 1) {
            throw new IllegalStateException("问诊单更新失败");
        }
    }

    private void validatePage(long current, long size) {
        if (current < 1) {
            throw new IllegalArgumentException("页码必须大于 0");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("每页数量必须在 1 到 100 之间");
        }
    }

    private void validateOptionalStatus(String status) {
        if (hasText(status) && !VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("无效的问诊状态");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
