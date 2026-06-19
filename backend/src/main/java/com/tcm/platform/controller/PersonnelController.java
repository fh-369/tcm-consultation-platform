package com.tcm.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcm.platform.common.Result;
import com.tcm.platform.dto.AccountStatusUpdateRequest;
import com.tcm.platform.dto.AccountStatusResponse;
import com.tcm.platform.dto.PersonnelRecord;
import com.tcm.platform.service.PersonnelService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/personnel")
public class PersonnelController {

    private final PersonnelService personnelService;

    public PersonnelController(PersonnelService personnelService) {
        this.personnelService = personnelService;
    }

    @GetMapping("/users")
    public Result<IPage<PersonnelRecord>> listUsers(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        return Result.success(personnelService.listPatients(current, size, keyword));
    }

    @GetMapping("/doctors")
    public Result<IPage<PersonnelRecord>> listDoctors(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword
    ) {
        return Result.success(personnelService.listDoctors(current, size, keyword));
    }

    @PutMapping("/accounts/{id}/status")
    public Result<AccountStatusResponse> updateAccountStatus(
            @PathVariable Long id,
            @Valid @RequestBody AccountStatusUpdateRequest request,
            Authentication authentication
    ) {
        return Result.success("账号状态已更新", AccountStatusResponse.from(
                personnelService.updateEnabled(id, request.getEnabled(), authentication.getName())
        ));
    }
}
