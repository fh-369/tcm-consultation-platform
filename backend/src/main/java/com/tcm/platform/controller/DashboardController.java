package com.tcm.platform.controller;

import com.tcm.platform.common.Result;
import com.tcm.platform.dto.DashboardSummary;
import com.tcm.platform.service.ConsultationExportService;
import com.tcm.platform.service.DashboardService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台 Dashboard 统计与问诊导出接口。
 */
@RestController
@RequestMapping("/api/admin")
public class DashboardController {

    private static final MediaType CSV_MEDIA_TYPE = MediaType.parseMediaType("text/csv;charset=UTF-8");

    private final DashboardService dashboardService;
    private final ConsultationExportService consultationExportService;

    public DashboardController(
            DashboardService dashboardService,
            ConsultationExportService consultationExportService
    ) {
        this.dashboardService = dashboardService;
        this.consultationExportService = consultationExportService;
    }

    @GetMapping("/dashboard")
    public Result<DashboardSummary> getDashboard() {
        return Result.success(dashboardService.getSummary());
    }

    @GetMapping("/export/consultations")
    public ResponseEntity<byte[]> exportConsultations() {
        return ResponseEntity.ok()
                .contentType(CSV_MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"consultations.csv\"")
                .body(consultationExportService.exportCsv());
    }
}
