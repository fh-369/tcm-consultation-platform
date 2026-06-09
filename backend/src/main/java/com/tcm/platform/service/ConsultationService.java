package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.dto.ConsultationRequest;
import com.tcm.platform.dto.ConsultationUpdateRequest;
import com.tcm.platform.entity.Consultation;
import com.tcm.platform.mapper.ConsultationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 问诊单创建、查询、更新和统计业务。
 */
@Service
public class ConsultationService {

    private static final String DEFAULT_URGENCY = "普通";
    private static final String INITIAL_STATUS = "待接诊";
    private static final Set<String> VALID_URGENCIES = Set.of("普通", "紧急", "非常紧急");
    private static final Set<String> VALID_STATUSES = Set.of("待接诊", "接诊中", "已完成");

    private final ConsultationMapper consultationMapper;
    private final ReminderService reminderService;

    public ConsultationService(ConsultationMapper consultationMapper, ReminderService reminderService) {
        this.consultationMapper = consultationMapper;
        this.reminderService = reminderService;
    }

    @Transactional
    public Consultation createConsultation(ConsultationRequest request) {
        String urgency = defaultUrgency(request.getUrgency());
        validateUrgency(urgency);

        Consultation consultation = new Consultation();
        consultation.setPatientAccountId(request.getPatientAccountId());
        consultation.setPatientName(request.getPatientName());
        consultation.setAge(request.getAge());
        consultation.setGender(request.getGender());
        consultation.setPhone(request.getPhone());
        consultation.setSymptoms(request.getSymptoms());
        consultation.setDuration(request.getDuration());
        consultation.setAllergyHistory(request.getAllergyHistory());
        consultation.setUrgency(urgency);
        consultation.setPatientNote(request.getPatientNote());
        consultation.setStatus(INITIAL_STATUS);
        reminderService.applyReminder(consultation);

        if (consultationMapper.insert(consultation) != 1) {
            throw new IllegalStateException("创建问诊单失败");
        }
        return consultation;
    }

    public Page<Consultation> listConsultations(
            long current,
            long size,
            String status,
            String urgency,
            Long patientAccountId
    ) {
        validatePage(current, size);
        validateOptionalStatus(status);
        validateOptionalUrgency(urgency);

        LambdaQueryWrapper<Consultation> query = new LambdaQueryWrapper<>();
        query.eq(hasText(status), Consultation::getStatus, status)
                .eq(hasText(urgency), Consultation::getUrgency, urgency)
                .eq(patientAccountId != null, Consultation::getPatientAccountId, patientAccountId)
                .orderByDesc(Consultation::getCreatedAt);

        return consultationMapper.selectPage(new Page<>(current, size), query);
    }

    public Consultation getConsultationById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("问诊单 ID 不能为空");
        }

        Consultation consultation = consultationMapper.selectById(id);
        if (consultation == null) {
            throw new IllegalArgumentException("问诊单不存在");
        }
        return consultation;
    }

    @Transactional
    public Consultation updateConsultation(Long id, ConsultationUpdateRequest request) {
        Consultation consultation = getConsultationById(id);
        validateOptionalStatus(request.getStatus());

        if (hasText(request.getStatus())) {
            consultation.setStatus(request.getStatus());
        }
        if (request.getDoctorNote() != null) {
            consultation.setDoctorNote(request.getDoctorNote());
        }
        if (request.getDoctorId() != null) {
            consultation.setDoctorId(request.getDoctorId());
        }
        if (request.getFollowUpAt() != null) {
            consultation.setFollowUpAt(request.getFollowUpAt());
        }

        if (consultationMapper.updateById(consultation) != 1) {
            throw new IllegalStateException("更新问诊单失败");
        }
        return consultation;
    }

    public List<Map<String, Object>> getStatusDistribution() {
        return consultationMapper.countByStatus();
    }

    public List<Map<String, Object>> getUrgencyDistribution() {
        return consultationMapper.countByUrgency();
    }

    public List<Map<String, Object>> getTrendLast6Months() {
        return consultationMapper.trendLast6Months();
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

    private void validateOptionalUrgency(String urgency) {
        if (hasText(urgency)) {
            validateUrgency(urgency);
        }
    }

    private void validateUrgency(String urgency) {
        if (!VALID_URGENCIES.contains(urgency)) {
            throw new IllegalArgumentException("无效的紧急度");
        }
    }

    private String defaultUrgency(String urgency) {
        return hasText(urgency) ? urgency : DEFAULT_URGENCY;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
