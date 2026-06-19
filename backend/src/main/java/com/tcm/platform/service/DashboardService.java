package com.tcm.platform.service;

import com.tcm.platform.dto.DashboardSummary;
import com.tcm.platform.mapper.ConsultationMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public List<Map<String, Object>> getTrend(String period) {
        return switch (period) {
            case "day" -> consultationMapper.trendLast7Days();
            case "week" -> consultationMapper.trendLast4Weeks();
            case "month" -> consultationMapper.trendLast6MonthsByPeriod();
            default -> throw new IllegalArgumentException("不支持的趋势周期");
        };
    }
}
