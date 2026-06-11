package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.entity.Consultation;
import com.tcm.platform.dto.ConsultationRequest;
import com.tcm.platform.dto.ConsultationUpdateRequest;
import com.tcm.platform.mapper.ConsultationMapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {

    @BeforeAll
    static void initializeTableInfo() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), "test"),
                Consultation.class
        );
    }

    @Mock
    private ConsultationMapper consultationMapper;

    @Mock
    private ReminderService reminderService;

    @Test
    void listConsultationsSearchesPatientNameOrSymptomsByKeyword() {
        ConsultationService service = new ConsultationService(consultationMapper, reminderService);
        when(consultationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(new Page<>());

        service.listConsultations(1, 10, null, null, null, "胃痛");

        ArgumentCaptor<LambdaQueryWrapper<Consultation>> queryCaptor =
                ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(consultationMapper).selectPage(any(IPage.class), queryCaptor.capture());

        String sql = queryCaptor.getValue().getCustomSqlSegment();
        assertThat(sql).contains("patient_name", "symptoms", "OR");
    }

    @Test
    void createConsultationAppliesDefaultsAndReminderBeforeInsert() {
        ConsultationService service = new ConsultationService(consultationMapper, reminderService);
        when(consultationMapper.insert(any(Consultation.class))).thenReturn(1);
        ConsultationRequest request = consultationRequest(null);

        Consultation created = service.createConsultation(request);

        assertThat(created.getUrgency()).isEqualTo("普通");
        assertThat(created.getStatus()).isEqualTo("待接诊");
        assertThat(created.getPatientName()).isEqualTo("李女士");
        verify(reminderService).applyReminder(created);
        verify(consultationMapper).insert(created);
    }

    @Test
    void createConsultationRejectsInvalidUrgencyBeforeInsert() {
        ConsultationService service = new ConsultationService(consultationMapper, reminderService);

        assertThatThrownBy(() -> service.createConsultation(consultationRequest("最高优先")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("无效的紧急度");

        verify(consultationMapper, never()).insert(any());
        verify(reminderService, never()).applyReminder(any());
    }

    @Test
    void listConsultationsRejectsInvalidPaginationAndFilters() {
        ConsultationService service = new ConsultationService(consultationMapper, reminderService);

        assertThatThrownBy(() -> service.listConsultations(0, 10, null, null, null, null))
                .hasMessage("页码必须大于 0");
        assertThatThrownBy(() -> service.listConsultations(1, 101, null, null, null, null))
                .hasMessage("每页数量必须在 1 到 100 之间");
        assertThatThrownBy(() -> service.listConsultations(1, 10, "已取消", null, null, null))
                .hasMessage("无效的问诊状态");
        assertThatThrownBy(() -> service.listConsultations(1, 10, null, "未知", null, null))
                .hasMessage("无效的紧急度");

        verify(consultationMapper, never()).selectPage(any(), any());
    }

    @Test
    void updateConsultationChangesProvidedFieldsAndKeepsOtherValues() {
        ConsultationService service = new ConsultationService(consultationMapper, reminderService);
        Consultation existing = new Consultation();
        existing.setId(7L);
        existing.setStatus("待接诊");
        existing.setDoctorNote("旧备注");
        when(consultationMapper.selectById(7L)).thenReturn(existing);
        when(consultationMapper.updateById(existing)).thenReturn(1);
        ConsultationUpdateRequest request = new ConsultationUpdateRequest();
        request.setStatus("接诊中");
        request.setDoctorNote("已查看");
        request.setDoctorId(3L);

        Consultation updated = service.updateConsultation(7L, request);

        assertThat(updated.getStatus()).isEqualTo("接诊中");
        assertThat(updated.getDoctorNote()).isEqualTo("已查看");
        assertThat(updated.getDoctorId()).isEqualTo(3L);
        verify(consultationMapper).updateById(existing);
    }

    @Test
    void updateConsultationRejectsMissingRecordAndInvalidStatus() {
        ConsultationService service = new ConsultationService(consultationMapper, reminderService);
        when(consultationMapper.selectById(99L)).thenReturn(null);

        assertThatThrownBy(() -> service.updateConsultation(99L, new ConsultationUpdateRequest()))
                .hasMessage("问诊单不存在");

        Consultation existing = new Consultation();
        existing.setId(7L);
        when(consultationMapper.selectById(7L)).thenReturn(existing);
        ConsultationUpdateRequest invalid = new ConsultationUpdateRequest();
        invalid.setStatus("已取消");

        assertThatThrownBy(() -> service.updateConsultation(7L, invalid))
                .hasMessage("无效的问诊状态");
        verify(consultationMapper, never()).updateById(any());
    }

    private ConsultationRequest consultationRequest(String urgency) {
        ConsultationRequest request = new ConsultationRequest();
        request.setPatientAccountId(8L);
        request.setPatientName("李女士");
        request.setSymptoms("容易疲倦");
        request.setUrgency(urgency);
        return request;
    }
}
