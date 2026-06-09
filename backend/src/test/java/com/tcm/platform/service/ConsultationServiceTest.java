package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.entity.Consultation;
import com.tcm.platform.mapper.ConsultationMapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
}
