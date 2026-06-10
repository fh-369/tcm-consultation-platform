package com.tcm.platform.service;

import com.tcm.platform.dto.DashboardSummary;
import com.tcm.platform.mapper.ConsultationMapper;
import org.springframework.stereotype.Service;

/**
 * 聚合后台 Dashboard 所需的问诊统计。
 */
@Service
public class DashboardService {

    private final ConsultationMapper consultationMapper;

    public DashboardService(ConsultationMapper consultationMapper) {
        this.consultationMapper = consultationMapper;
    }

    public DashboardSummary getSummary() {
        return new DashboardSummary(
                consultationMapper.countByStatus(),
                consultationMapper.countByUrgency(),
                consultationMapper.trendLast6Months()
        );
    }
}
