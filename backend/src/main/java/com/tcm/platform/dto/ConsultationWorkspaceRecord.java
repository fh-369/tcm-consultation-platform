package com.tcm.platform.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultationWorkspaceRecord {

    private Long id;
    private Long patientAccountId;
    private Long departmentId;
    private String departmentName;
    private String patientName;
    private Integer age;
    private String gender;
    private String phone;
    private String symptoms;
    private String duration;
    private String allergyHistory;
    private String urgency;
    private String patientNote;
    private String reminderLevel;
    private String reminderText;
    private String status;
    private String doctorNote;
    private Long doctorId;
    private String doctorName;
    private String doctorDepartment;
    private LocalDateTime followUpAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
