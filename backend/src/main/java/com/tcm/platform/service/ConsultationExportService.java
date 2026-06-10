package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.opencsv.CSVWriter;
import com.tcm.platform.entity.Consultation;
import com.tcm.platform.mapper.ConsultationMapper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 将问诊数据导出为适合表格软件打开的 UTF-8 CSV。
 */
@Service
public class ConsultationExportService {

    private static final byte[] UTF_8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] HEADER = {
            "问诊ID", "患者姓名", "年龄", "性别", "联系电话", "症状描述", "持续时间",
            "过敏史", "紧急度", "状态", "医生备注", "创建时间", "更新时间"
    };

    private final ConsultationMapper consultationMapper;

    public ConsultationExportService(ConsultationMapper consultationMapper) {
        this.consultationMapper = consultationMapper;
    }

    public byte[] exportCsv() {
        List<Consultation> consultations = consultationMapper.selectList(
                new LambdaQueryWrapper<Consultation>().orderByDesc(Consultation::getCreatedAt)
        );

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            output.write(UTF_8_BOM);
            try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8))) {
                writer.writeNext(HEADER);
                for (Consultation consultation : consultations) {
                    writer.writeNext(toRow(consultation));
                }
            }
            return output.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("导出问诊 CSV 失败", ex);
        }
    }

    private String[] toRow(Consultation consultation) {
        return new String[]{
                value(consultation.getId()),
                value(consultation.getPatientName()),
                value(consultation.getAge()),
                value(consultation.getGender()),
                value(consultation.getPhone()),
                value(consultation.getSymptoms()),
                value(consultation.getDuration()),
                value(consultation.getAllergyHistory()),
                value(consultation.getUrgency()),
                value(consultation.getStatus()),
                value(consultation.getDoctorNote()),
                formatDateTime(consultation.getCreatedAt()),
                formatDateTime(consultation.getUpdatedAt())
        };
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "" : DATE_TIME_FORMATTER.format(value);
    }

    private String value(Object value) {
        return value == null ? "" : value.toString();
    }
}
