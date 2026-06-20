package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.dto.ConsultationUpdateRequest;
import com.tcm.platform.entity.Account;
import com.tcm.platform.entity.Consultation;
import com.tcm.platform.entity.User;
import com.tcm.platform.mapper.AccountMapper;
import com.tcm.platform.mapper.ConsultationMapper;
import com.tcm.platform.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsultationWorkspaceServiceTest {

    @BeforeAll
    static void initializeTableInfo() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), "workspace-test"),
                Consultation.class
        );
    }

    @Test
    void doctorWorkspaceOnlyIncludesUnassignedAndOwnConsultations() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        when(consultationMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(new Page<>());
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(consultationMapper, userMapper, accountMapper);

        service.listForDoctor(1, 10, null, null, null, 6L);

        ArgumentCaptor<LambdaQueryWrapper<Consultation>> queryCaptor =
                ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(consultationMapper).selectPage(any(IPage.class), queryCaptor.capture());
        assertThat(queryCaptor.getValue().getCustomSqlSegment())
                .contains("doctor_id IS NULL", "doctor_id", "OR");
    }

    @Test
    void administratorAssignsEnabledDoctorAndResetsActiveConsultationToPending() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        Consultation consultation = consultation(9L, 3L, "接诊中");
        User doctor = doctor(6L, 16L);
        Account account = new Account();
        account.setId(16L);
        account.setEnabled(true);
        when(consultationMapper.selectById(9L)).thenReturn(consultation);
        when(userMapper.selectById(6L)).thenReturn(doctor);
        when(accountMapper.selectById(16L)).thenReturn(account);
        when(consultationMapper.updateAssignment(9L, 6L, "待接诊")).thenReturn(1);
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(consultationMapper, userMapper, accountMapper);

        Consultation assigned = service.assign(9L, 6L);

        assertThat(assigned.getDoctorId()).isEqualTo(6L);
        assertThat(assigned.getStatus()).isEqualTo("待接诊");
        verify(consultationMapper).updateAssignment(9L, 6L, "待接诊");
    }

    @Test
    void administratorCanClearAssignmentWithExplicitNullUpdate() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        Consultation consultation = consultation(9L, 6L, "接诊中");
        when(consultationMapper.selectById(9L)).thenReturn(consultation);
        when(consultationMapper.updateAssignment(9L, null, "待接诊")).thenReturn(1);
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(consultationMapper, userMapper, accountMapper);

        Consultation unassigned = service.assign(9L, null);

        assertThat(unassigned.getDoctorId()).isNull();
        assertThat(unassigned.getStatus()).isEqualTo("待接诊");
        verify(consultationMapper).updateAssignment(9L, null, "待接诊");
    }

    @Test
    void doctorClaimsOnlyUnassignedConsultationAndCannotProcessAnotherDoctorsRecord() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        Consultation claimedRecord = consultation(9L, 6L, "待接诊");
        when(consultationMapper.claimIfUnassigned(9L, 6L)).thenReturn(1);
        when(consultationMapper.selectById(9L)).thenReturn(claimedRecord);
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(consultationMapper, userMapper, accountMapper);

        Consultation claimed = service.claim(9L, 6L);

        assertThat(claimed.getDoctorId()).isEqualTo(6L);
        assertThat(claimed.getStatus()).isEqualTo("待接诊");

        Consultation otherDoctors = consultation(10L, 7L, "接诊中");
        when(consultationMapper.selectById(10L)).thenReturn(otherDoctors);
        ConsultationUpdateRequest request = new ConsultationUpdateRequest();
        request.setStatus("已完成");

        assertThatThrownBy(() -> service.updateAsDoctor(10L, request, 6L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("该问诊单未分配给当前医生");
        verify(consultationMapper, never()).deleteById(any(Long.class));
    }

    @Test
    void completedConsultationCannotBeReassignedOrClaimed() {
        ConsultationMapper consultationMapper = mock(ConsultationMapper.class);
        UserMapper userMapper = mock(UserMapper.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        Consultation completed = consultation(9L, 6L, "已完成");
        when(consultationMapper.claimIfUnassigned(9L, 7L)).thenReturn(0);
        when(consultationMapper.selectById(9L)).thenReturn(completed);
        ConsultationWorkspaceService service =
                new ConsultationWorkspaceService(consultationMapper, userMapper, accountMapper);

        assertThatThrownBy(() -> service.assign(9L, 7L))
                .hasMessage("已完成问诊不能重新分配");
        assertThatThrownBy(() -> service.claim(9L, 7L))
                .hasMessage("已完成问诊不能认领");
    }

    private Consultation consultation(Long id, Long doctorId, String status) {
        Consultation consultation = new Consultation();
        consultation.setId(id);
        consultation.setDoctorId(doctorId);
        consultation.setStatus(status);
        return consultation;
    }

    private User doctor(Long id, Long accountId) {
        User doctor = new User();
        doctor.setId(id);
        doctor.setAccountId(accountId);
        doctor.setRole("doctor");
        doctor.setDisplayName("张医生");
        return doctor;
    }
}
