package com.tcm.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.dto.PersonnelRecord;
import com.tcm.platform.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    @Select("""
            <script>
            SELECT a.id,
                   a.username,
                   a.role,
                   p.display_name AS displayName,
                   p.phone,
                   NULL AS department,
                   a.enabled,
                   a.created_at AS createdAt
            FROM accounts a
            JOIN patient_accounts p ON p.account_id = a.id
            WHERE a.role = 'patient'
            <if test="keyword != null and keyword != ''">
              AND (
                a.username LIKE CONCAT('%', #{keyword}, '%')
                OR p.display_name LIKE CONCAT('%', #{keyword}, '%')
                OR p.phone LIKE CONCAT('%', #{keyword}, '%')
              )
            </if>
            ORDER BY a.created_at DESC, a.id DESC
            </script>
            """)
    IPage<PersonnelRecord> selectPatientPersonnel(
            Page<PersonnelRecord> page,
            @Param("keyword") String keyword
    );

    @Select("""
            <script>
            SELECT a.id,
                   a.username,
                   a.role,
                   u.display_name AS displayName,
                   NULL AS phone,
                   u.department,
                   a.enabled,
                   a.created_at AS createdAt
            FROM accounts a
            JOIN users u ON u.account_id = a.id
            WHERE a.role = 'doctor'
            <if test="keyword != null and keyword != ''">
              AND (
                a.username LIKE CONCAT('%', #{keyword}, '%')
                OR u.display_name LIKE CONCAT('%', #{keyword}, '%')
                OR u.department LIKE CONCAT('%', #{keyword}, '%')
              )
            </if>
            ORDER BY a.created_at DESC, a.id DESC
            </script>
            """)
    IPage<PersonnelRecord> selectDoctorPersonnel(
            Page<PersonnelRecord> page,
            @Param("keyword") String keyword
    );
}
