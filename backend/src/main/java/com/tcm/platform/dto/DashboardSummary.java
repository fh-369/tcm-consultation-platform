package com.tcm.platform.dto;

import java.util.List;
import java.util.Map;

/**
 * 后台 Dashboard 问诊统计汇总。
 */
public record DashboardSummary(
        List<Map<String, Object>> statusDistribution,
        List<Map<String, Object>> urgencyDistribution,
        List<Map<String, Object>> trendLast6Months
) {
}
