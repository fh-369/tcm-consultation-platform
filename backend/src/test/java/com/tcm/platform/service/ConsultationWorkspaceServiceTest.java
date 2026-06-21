package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.dto.ConsultationUpdateRequest;
import com.tcm.platform.entity.Account;
import com.tcm.platform.entity.Consultation;
import com.tcm.platform.entity.Department;
import com.tcm.platform.entity.User;
import com.tcm.platform.mapper.AccountMapper;
import com.tcm.platform.mapper.ConsultationMapper;
import com.tcm.platform.mapper.DepartmentMapper;
import com.tcm.platform.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsultationWorkspaceServiceTest {

    @BeforeAll
    static void initializeTableInfo() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), "workspace-test"),
                Consultation.class
        );
    }

    @Test
    void departmentPoolOnlyIncludesPendingUnassignedConsultationsFromOwnOrGeneralDepartment() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        DepartmentMapper departmentMapper = mock(DepartmentMapper.class);
        User doctor = doctor(6L, 16L);
        doctor.setDepartmentId(2L);
        doctor.setApprovalStatus("APPROVED");
        Account account = new Account();
        account.setId(16L);
        account.setEnabled(true);
        Department general = new Department();
        general.setId(1L);
        general.setCode("general");
        general.setEnabled(true);
        when(userMapper.selectById(6L)).thenReturn(doctor);
        when(accountMapper.selectById(16L)).thenReturn(account);
        when(departmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(general);
        when(consultationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(new Page<>());
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(
                        consultationMapper, userMapper, accountMapper, departmentMapper
                );

        service.listDepartmentPool(1, 10, null, null, "all", 6L);

        ArgumentCaptor<LambdaQueryWrapper<Consultation>> queryCaptor =
                ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(consultationMapper).selectPage(any(IPage.class), queryCaptor.capture());
        assertThat(queryCaptor.getValue().getCustomSqlSegment())
                .contains("doctor_id IS NULL", "status", "department_id", "OR");
    }

    @Test
    void myConsultationsOnlyIncludesRecordsAssignedToCurrentDoctor() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        DepartmentMapper departmentMapper = mock(DepartmentMapper.class);
        User doctor = doctor(6L, 16L);
        Account account = new Account();
        account.setId(16L);
        account.setEnabled(true);
        when(userMapper.selectById(6L)).thenReturn(doctor);
        when(accountMapper.selectById(16L)).thenReturn(account);
        when(consultationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(new Page<>());
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(
                        consultationMapper, userMapper, accountMapper, departmentMapper
                );

        service.listMine(1, 10, "接诊中", null, null, 6L);

        ArgumentCaptor<LambdaQueryWrapper<Consultation>> queryCaptor =
                ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(consultationMapper).selectPage(any(IPage.class), queryCaptor.capture());
        assertThat(queryCaptor.getValue().getCustomSqlSegment())
                .contains("doctor_id", "status")
                .doesNotContain("doctor_id IS NULL");
    }

    @Test
    void administratorAssignsEnabledDoctorAndResetsActiveConsultationToPending() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        DepartmentMapper departmentMapper = mock(DepartmentMapper.class);
        Consultation consultation = consultation(9L, 3L, "接诊中");
        consultation.setDepartmentId(2L);
        User doctor = doctor(6L, 16L);
        Department general = department(1L, "general", "综合咨询");
        Account account = new Account();
        account.setId(16L);
        account.setEnabled(true);
        when(consultationMapper.selectById(9L)).thenReturn(consultation);
        when(userMapper.selectById(6L)).thenReturn(doctor);
        when(accountMapper.selectById(16L)).thenReturn(account);
        when(departmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(general);
        when(consultationMapper.updateAssignment(9L, 6L, "待接诊")).thenReturn(1);
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(
                        consultationMapper, userMapper, accountMapper, departmentMapper
                );

        Consultation assigned = service.assign(9L, 6L);

        assertThat(assigned.getDoctorId()).isEqualTo(6L);
        assertThat(assigned.getStatus()).isEqualTo("待接诊");
        assertThat(assigned.getAssignedAt()).isNotNull();
        verify(consultationMapper).updateAssignment(9L, 6L, "待接诊");
    }

    @Test
    void administratorRejectsCrossDepartmentDoctorButAllowsAnyApprovedDoctorForGeneralConsultation() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        DepartmentMapper departmentMapper = mock(DepartmentMapper.class);
        User gynecologyDoctor = doctor(7L, 17L);
        gynecologyDoctor.setDepartmentId(3L);
        gynecologyDoctor.setDepartment("中医妇科");
        Account account = new Account();
        account.setId(17L);
        account.setEnabled(true);
        Department general = department(1L, "general", "综合咨询");
        Consultation internalMedicine = consultation(9L, null, "待接诊");
        internalMedicine.setDepartmentId(2L);
        Consultation generalConsultation = consultation(10L, null, "待接诊");
        generalConsultation.setDepartmentId(1L);
        when(userMapper.selectById(7L)).thenReturn(gynecologyDoctor);
        when(accountMapper.selectById(17L)).thenReturn(account);
        when(departmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(general);
        when(consultationMapper.selectById(9L)).thenReturn(internalMedicine);
        when(consultationMapper.selectById(10L)).thenReturn(generalConsultation);
        when(consultationMapper.updateAssignment(10L, 7L, "待接诊")).thenReturn(1);
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(
                        consultationMapper, userMapper, accountMapper, departmentMapper
                );

        assertThatThrownBy(() -> service.assign(9L, 7L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("只能分配给当前问诊科室的医生");

        Consultation assigned = service.assign(10L, 7L);

        assertThat(assigned.getDoctorId()).isEqualTo(7L);
        verify(consultationMapper).updateAssignment(10L, 7L, "待接诊");
    }

    @Test
    void administratorCannotAssignDoctorWithoutApproval() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        DepartmentMapper departmentMapper = mock(DepartmentMapper.class);
        Consultation consultation = consultation(9L, null, "待接诊");
        consultation.setDepartmentId(2L);
        User pendingDoctor = doctor(6L, 16L);
        pendingDoctor.setApprovalStatus("PENDING");
        when(consultationMapper.selectById(9L)).thenReturn(consultation);
        when(userMapper.selectById(6L)).thenReturn(pendingDoctor);
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(
                        consultationMapper, userMapper, accountMapper, departmentMapper
                );

        assertThatThrownBy(() -> service.assign(9L, 6L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("医生账号尚未通过审核");

        verify(consultationMapper, never()).updateAssignment(any(), any(), any());
    }

    @Test
    void administratorCanClearAssignmentWithExplicitNullUpdate() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        DepartmentMapper departmentMapper = mock(DepartmentMapper.class);
        Consultation consultation = consultation(9L, 6L, "接诊中");
        consultation.setAssignedAt(LocalDateTime.now().minusDays(1));
        when(consultationMapper.selectById(9L)).thenReturn(consultation);
        when(consultationMapper.updateAssignment(9L, null, "待接诊")).thenReturn(1);
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(
                        consultationMapper, userMapper, accountMapper, departmentMapper
                );

        Consultation unassigned = service.assign(9L, null);

        assertThat(unassigned.getDoctorId()).isNull();
        assertThat(unassigned.getStatus()).isEqualTo("待接诊");
        assertThat(unassigned.getAssignedAt()).isNull();
        verify(consultationMapper).updateAssignment(9L, null, "待接诊");
    }

    @Test
    void doctorClaimsOnlyUnassignedConsultationAndCannotProcessAnotherDoctorsRecord() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        DepartmentMapper departmentMapper = mock(DepartmentMapper.class);
        Consultation unassignedRecord = consultation(9L, null, "待接诊");
        unassignedRecord.setDepartmentId(2L);
        Consultation claimedRecord = consultation(9L, 6L, "待接诊");
        claimedRecord.setDepartmentId(2L);
        User doctor = doctor(6L, 16L);
        Account account = new Account();
        account.setId(16L);
        account.setEnabled(true);
        Department general = new Department();
        general.setId(1L);
        general.setCode("general");
        general.setEnabled(true);
        when(userMapper.selectById(6L)).thenReturn(doctor);
        when(accountMapper.selectById(16L)).thenReturn(account);
        when(departmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(general);
        when(consultationMapper.claimIfUnassigned(9L, 6L)).thenReturn(1);
        when(consultationMapper.selectById(9L)).thenReturn(unassignedRecord, claimedRecord);
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(
                        consultationMapper, userMapper, accountMapper, departmentMapper
                );

        Consultation claimed = service.claim(9L, 6L);

        assertThat(claimed.getDoctorId()).isEqualTo(6L);
        assertThat(claimed.getStatus()).isEqualTo("待接诊");
        assertThat(claimed.getAssignedAt()).isNotNull();

        Consultation otherDoctors = consultation(10L, 7L, "接诊中");
        when(consultationMapper.selectById(10L)).thenReturn(otherDoctors);
        ConsultationUpdateRequest request = new ConsultationUpdateRequest();
        request.setStatus("已完成");

        assertThatThrownBy(() -> service.updateAsDoctor(10L, request, 6L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("该问诊单未分配给当前医生");
        verify(consultationMapper, never()).deleteById(any(Long.class));
    }

    @Test
    void doctorCannotClaimConsultationOutsideOwnOrGeneralDepartment() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        DepartmentMapper departmentMapper = mock(DepartmentMapper.class);
        User doctor = doctor(6L, 16L);
        doctor.setDepartmentId(2L);
        doctor.setApprovalStatus("APPROVED");
        Account account = new Account();
        account.setId(16L);
        account.setEnabled(true);
        Department general = new Department();
        general.setId(1L);
        general.setCode("general");
        general.setEnabled(true);
        Consultation otherDepartment = consultation(9L, null, "待接诊");
        otherDepartment.setDepartmentId(3L);
        when(userMapper.selectById(6L)).thenReturn(doctor);
        when(accountMapper.selectById(16L)).thenReturn(account);
        when(departmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(general);
        when(consultationMapper.selectById(9L)).thenReturn(otherDepartment);
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(
                        consultationMapper, userMapper, accountMapper, departmentMapper
                );

        assertThatThrownBy(() -> service.claim(9L, 6L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("只能认领本科室或综合咨询问诊");

        verify(consultationMapper, never()).claimIfUnassigned(any(), any());
    }

    @Test
    void completedConsultationCannotBeReassignedOrClaimed() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        DepartmentMapper departmentMapper = mock(DepartmentMapper.class);
        Consultation completed = consultation(9L, 6L, "已完成");
        User doctor = doctor(7L, 17L);
        Account account = new Account();
        account.setId(17L);
        account.setEnabled(true);
        Department general = new Department();
        general.setId(1L);
        general.setCode("general");
        general.setEnabled(true);
        when(userMapper.selectById(7L)).thenReturn(doctor);
        when(accountMapper.selectById(17L)).thenReturn(account);
        when(departmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(general);
        when(consultationMapper.claimIfUnassigned(9L, 7L)).thenReturn(0);
        when(consultationMapper.selectById(9L)).thenReturn(completed);
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(
                        consultationMapper, userMapper, accountMapper, departmentMapper
                );

        assertThatThrownBy(() -> service.assign(9L, 7L))
                .hasMessage("已完成问诊不能重新分配");
        assertThatThrownBy(() -> service.claim(9L, 7L))
                .hasMessage("已完成问诊不能认领");
    }

    @Test
    void administratorFiltersByDepartmentAndTransferClearsAssignmentAndResetsStatus() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        DepartmentMapper departmentMapper = mock(DepartmentMapper.class);
        when(consultationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(new Page<>());
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(
                        consultationMapper, userMapper, accountMapper, departmentMapper
                );

        service.listForAdmin(1, 10, null, null, null, null, null, 2L);

        ArgumentCaptor<LambdaQueryWrapper<Consultation>> queryCaptor =
                ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(consultationMapper).selectPage(any(IPage.class), queryCaptor.capture());
        assertThat(queryCaptor.getValue().getCustomSqlSegment()).contains("department_id");

        Consultation consultation = consultation(9L, 6L, "接诊中");
        consultation.setDepartmentId(1L);
        consultation.setAssignedAt(LocalDateTime.now().minusDays(1));
        Department department = department(3L, "gynecology", "中医妇科");
        when(consultationMapper.selectById(9L)).thenReturn(consultation);
        when(departmentMapper.selectById(3L)).thenReturn(department);
        when(consultationMapper.updateDepartmentAndClearAssignment(9L, 3L, "待接诊"))
                .thenReturn(1);

        Consultation updated = service.updateDepartment(9L, 3L);

        assertThat(updated.getDepartmentId()).isEqualTo(3L);
        assertThat(updated.getDoctorId()).isNull();
        assertThat(updated.getStatus()).isEqualTo("待接诊");
        assertThat(updated.getAssignedAt()).isNull();
        verify(consultationMapper).updateDepartmentAndClearAssignment(9L, 3L, "待接诊");
    }

    @Test
    void completedConsultationCannotChangeDepartment() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        DepartmentMapper departmentMapper = mock(DepartmentMapper.class);
        Consultation completed = consultation(9L, 6L, "已完成");
        completed.setDepartmentId(1L);
        when(consultationMapper.selectById(9L)).thenReturn(completed);
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(
                        consultationMapper, userMapper, accountMapper, departmentMapper
                );

        assertThatThrownBy(() -> service.updateDepartment(9L, 3L))
                .hasMessage("已完成问诊不能修改科室");

        verify(departmentMapper, never()).selectById(any());
        verify(consultationMapper, never()).updateById(any());
    }

    private Consultation consultation(Long id, Long doctorId, String status) {
        Consultation consultation = new Consultation();
        consultation.setId(id);
        consultation.setDoctorId(doctorId);
        consultation.setStatus(status);
        return consultation;
    }

    private User doctor(Long id, Long accountId) {
        User doctor = new User();
        doctor.setId(id);
        doctor.setAccountId(accountId);
        doctor.setRole("doctor");
        doctor.setDisplayName("张医生");
        doctor.setDepartment("中医内科");
        doctor.setDepartmentId(2L);
        doctor.setApprovalStatus("APPROVED");
        return doctor;
    }

    private Department department(Long id, String code, String name) {
        Department department = new Department();
        department.setId(id);
        department.setCode(code);
        department.setName(name);
        department.setEnabled(true);
        return department;
    }
}
