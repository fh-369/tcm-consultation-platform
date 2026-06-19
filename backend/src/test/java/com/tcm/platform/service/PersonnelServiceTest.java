package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.dto.PersonnelRecord;
import com.tcm.platform.entity.Account;
import com.tcm.platform.mapper.AccountMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonnelServiceTest {

    @Test
    void listsPatientsAndDoctorsWithTheirOwnQueries() {
        AccountMapper accountMapper = mock(AccountMapper.class);
        Page<PersonnelRecord> patientPage = new Page<>(1, 10);
        Page<PersonnelRecord> doctorPage = new Page<>(1, 10);
        when(accountMapper.selectPatientPersonnel(any(Page.class), eq("张")))
                .thenReturn(patientPage);
        when(accountMapper.selectDoctorPersonnel(any(Page.class), eq("内科")))
                .thenReturn(doctorPage);
        PersonnelService service = new PersonnelService(accountMapper);

        IPage<PersonnelRecord> patients = service.listPatients(1, 10, "张");
        IPage<PersonnelRecord> doctors = service.listDoctors(1, 10, "内科");

        assertThat(patients).isSameAs(patientPage);
        assertThat(doctors).isSameAs(doctorPage);
    }

    @Test
    void updatesAnotherAccountStatusButRejectsDisablingCurrentAdministrator() {
        AccountMapper accountMapper = mock(AccountMapper.class);
        Account doctor = account(8L, "doctor1", true);
        Account admin = account(1L, "admin", true);
        when(accountMapper.selectById(8L)).thenReturn(doctor);
        when(accountMapper.selectById(1L)).thenReturn(admin);
        when(accountMapper.updateById(any(Account.class))).thenReturn(1);
        PersonnelService service = new PersonnelService(accountMapper);

        Account updated = service.updateEnabled(8L, false, "admin");

        assertThat(updated.getEnabled()).isFalse();
        verify(accountMapper).updateById(doctor);

        assertThatThrownBy(() -> service.updateEnabled(1L, false, "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("不能停用当前登录账号");
    }

    private Account account(Long id, String username, boolean enabled) {
        Account account = new Account();
        account.setId(id);
        account.setUsername(username);
        account.setEnabled(enabled);
        return account;
    }
}
