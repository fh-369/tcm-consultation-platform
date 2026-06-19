package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.dto.PersonnelRecord;
import com.tcm.platform.entity.Account;
import com.tcm.platform.mapper.AccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonnelService {

    private final AccountMapper accountMapper;

    public PersonnelService(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public IPage<PersonnelRecord> listPatients(long current, long size, String keyword) {
        return accountMapper.selectPatientPersonnel(new Page<>(current, size), normalizeKeyword(keyword));
    }

    public IPage<PersonnelRecord> listDoctors(long current, long size, String keyword) {
        return accountMapper.selectDoctorPersonnel(new Page<>(current, size), normalizeKeyword(keyword));
    }

    @Transactional
    public Account updateEnabled(Long accountId, boolean enabled, String currentUsername) {
        Account account = accountMapper.selectById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("账号不存在");
        }
        if (!enabled && account.getUsername().equals(currentUsername)) {
            throw new IllegalArgumentException("不能停用当前登录账号");
        }

        account.setEnabled(enabled);
        if (accountMapper.updateById(account) != 1) {
            throw new IllegalStateException("账号状态更新失败");
        }
        return account;
    }

    private String normalizeKeyword(String keyword) {
        return keyword == null ? null : keyword.trim();
    }
}
