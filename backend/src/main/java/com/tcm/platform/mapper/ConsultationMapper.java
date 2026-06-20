package com.tcm.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcm.platform.entity.Consultation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConsultationMapper extends BaseMapper<Consultation> {

    @Update("""
            UPDATE consultations
            SET doctor_id = #{doctorId},
                status = #{status},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{consultationId}
            """)
    int updateAssignment(
            @Param("consultationId") Long consultationId,
            @Param("doctorId") Long doctorId,
            @Param("status") String status
    );

    @Update("""
            UPDATE consultations
            SET doctor_id = #{doctorId},
                status = '待接诊',
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{consultationId}
              AND doctor_id IS NULL
              AND status <> '已完成'
            """)
    int claimIfUnassigned(
            @Param("consultationId") Long consultationId,
            @Param("doctorId") Long doctorId
    );

    /** 按状态统计问诊数量 */
    @Select("SELECT status, COUNT(*) AS count FROM consultations GROUP BY status")
    List<Map<String, Object>> countByStatus();
    
    /** 按紧急度统计问诊数量 */
    @Select("SELECT urgency, COUNT(*) AS count FROM consultations GROUP BY urgency")
    List<Map<String, Object>> countByUrgency();
    
    /** 近 6 个月问诊趋势 */
    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS count " +
            "FROM consultations " +
            "WHERE created_at >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 5 MONTH), '%Y-%m-01') " +
            "GROUP BY month ORDER BY month")
    List<Map<String, Object>> trendLast6Months();

    /** 近 7 天问诊趋势 */
    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m-%d') AS period, COUNT(*) AS count " +
            "FROM consultations WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
            "GROUP BY period ORDER BY period")
    List<Map<String, Object>> trendLast7Days();

    /** 近 4 周问诊趋势，以每周周一作为周期标识 */
    @Select("SELECT DATE_FORMAT(DATE_SUB(DATE(created_at), INTERVAL WEEKDAY(created_at) DAY), '%Y-%m-%d') AS period, " +
            "COUNT(*) AS count FROM consultations " +
            "WHERE created_at >= DATE_SUB(" +
            "DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 3 WEEK) " +
            "GROUP BY period ORDER BY period")
    List<Map<String, Object>> trendLast4Weeks();

    /** 近 6 个月问诊趋势，统一使用 period 字段 */
    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m') AS period, COUNT(*) AS count " +
            "FROM consultations " +
            "WHERE created_at >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 5 MONTH), '%Y-%m-01') " +
            "GROUP BY period ORDER BY period")
    List<Map<String, Object>> trendLast6MonthsByPeriod();
}
