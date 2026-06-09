package com.tcm.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcm.platform.entity.Consultation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConsultationMapper extends BaseMapper<Consultation> {

    /** 按状态统计问诊数量 */
    @Select("SELECT status, COUNT(*) AS count FROM consultations GROUP BY status")
    List<Map<String, Object>> countByStatus();
    
    /** 按紧急度统计问诊数量 */
    @Select("SELECT urgency, COUNT(*) AS count FROM consultations GROUP BY urgency")
    List<Map<String, Object>> countByUrgency();
    
    /** 近 6 个月问诊趋势 */
    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS count " +
            "FROM consultations WHERE created_at >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
            "GROUP BY month ORDER BY month")
    List<Map<String, Object>> trendLast6Months();
}
