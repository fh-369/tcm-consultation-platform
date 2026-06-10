package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.tcm.platform.entity.Consultation;
import com.tcm.platform.mapper.ConsultationMapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsultationExportServiceTest {

    @BeforeAll
    static void initializeTableInfo() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), "test"),
                Consultation.class
        );
    }

    @Test
    void exportCsvWritesUtf8BomChineseContentAndEscapesCommas() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        Consultation consultation = new Consultation();
        consultation.setId(7L);
        consultation.setPatientName("李女士");
        consultation.setAge(31);
        consultation.setGender("女");
        consultation.setPhone("13800000000");
        consultation.setSymptoms("胃痛,反酸");
        consultation.setUrgency("紧急");
        consultation.setStatus("待接诊");
        consultation.setCreatedAt(LocalDateTime.of(2026, 6, 10, 8, 30));
        when(consultationMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(consultation));

        byte[] csv = new ConsultationExportService(consultationMapper).exportCsv();

        assertThat(csv).startsWith((byte) 0xEF, (byte) 0xBB, (byte) 0xBF);
        String content = new String(csv, StandardCharsets.UTF_8);
        assertThat(content)
                .contains("问诊ID", "患者姓名", "李女士", "\"胃痛,反酸\"", "2026-06-10 08:30:00");

        ArgumentCaptor<LambdaQueryWrapper<Consultation>> queryCaptor =
                ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(consultationMapper).selectList(queryCaptor.capture());
        assertThat(queryCaptor.getValue().getCustomSqlSegment()).contains("created_at", "DESC");
    }
}
