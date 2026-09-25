package com.shahkabir.coffer.controller;

import com.shahkabir.coffer.dto.AccountResponse;
import com.shahkabir.coffer.dto.CreateAccountRequest;
import com.shahkabir.coffer.dto.UpdateAccountRequest;
import com.shahkabir.coffer.model.Account;
import com.shahkabir.coffer.service.AccountService;
import com.shahkabir.coffer.util.AccountNumberMasker;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public AccountResponse getAccountById(@PathVariable UUID id) {
        Account account = accountService.getAccount(id);

        return new AccountResponse(
                account.getId(),
                AccountNumberMasker.mask(account.getAccountNumber()),
                account.getAccountType(),
                account.getCurrency(),
                account.getAccountStatus(),
                account.getCustomer().getId(),
                account.getCreationTime()
        );
    }

    @PatchMapping("/{id}")
    public AccountResponse updateAccount(@PathVariable UUID id,
                                         @RequestBody @Valid UpdateAccountRequest request){
        Account account = accountService.updateAccount(id, request);

        return new AccountResponse(
                account.getId(),
                AccountNumberMasker.mask(account.getAccountNumber()),
                account.getAccountType(),
                account.getCurrency(),
                account.getAccountStatus(),
                account.getCustomer().getId(),
                account.getCreationTime()
        );

    }

    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts()
                .stream()
                .map(account -> new AccountResponse(
                        account.getId(),
                        AccountNumberMasker.mask(account.getAccountNumber()),
                        account.getAccountType(),
                        account.getCurrency(),
                        account.getAccountStatus(),
                        account.getCustomer().getId(),
                        account.getCreationTime()
                ))
                .toList();
    }

    @PostMapping
    public AccountResponse createAccount(
            @RequestBody @Valid CreateAccountRequest request) {
        Account account = accountService.createAccount(
                request.customerId(),
                request.type()
        );

        return new AccountResponse(
                account.getId(),
                AccountNumberMasker.mask(account.getAccountNumber()),
                account.getAccountType(),
                account.getCurrency(),
                account.getAccountStatus(),
                account.getCustomer().getId(),
                account.getCreationTime()
        );
    }

}
