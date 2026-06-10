package com.tcm.platform.service;

import com.tcm.platform.dto.DashboardSummary;
import com.tcm.platform.mapper.ConsultationMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    @Test
    void getSummaryCombinesAllConsultationStatistics() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        List<Map<String, Object>> status = List.of(Map.of("status", "待接诊", "count", 2L));
        List<Map<String, Object>> urgency = List.of(Map.of("urgency", "紧急", "count", 1L));
        List<Map<String, Object>> trend = List.of(Map.of("month", "2026-06", "count", 3L));
        when(consultationMapper.countByStatus()).thenReturn(status);
        when(consultationMapper.countByUrgency()).thenReturn(urgency);
        when(consultationMapper.trendLast6Months()).thenReturn(trend);

        DashboardSummary summary = new DashboardService(consultationMapper).getSummary();

        assertThat(summary.statusDistribution()).isEqualTo(status);
        assertThat(summary.urgencyDistribution()).isEqualTo(urgency);
        assertThat(summary.trendLast6Months()).isEqualTo(trend);
    }
}
