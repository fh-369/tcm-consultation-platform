package com.tcm.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcm.platform.entity.Consultation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConsultationMapper extends BaseMapper<Consultation> {
    
    // TODO: 添加以下 3 个自定义查询方法
    
    /** 按状态统计问诊数量 */
    // @Select("SELECT status, COUNT(*) as count FROM consultations GROUP BY status")
    // List<Map<String, Object>> countByStatus();
    
    /** 按紧急度统计问诊数量 */
    // @Select("SELECT urgency, COUNT(*) as count FROM consultations GROUP BY urgency")
    // List<Map<String, Object>> countByUrgency();
    
    /** 近 6 个月问诊趋势 */
    // @Select("SELECT DATE_FORMAT(created_at, '%Y-%m') as month, COUNT(*) as count " +
    //        "FROM consultations WHERE created_at >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
    //        "GROUP BY month ORDER BY month")
    // List<Map<String, Object>> trendLast6Months();
}
