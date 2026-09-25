package com.shahkabir.coffer.service;

import com.shahkabir.coffer.exception.AccountNotFoundException;
import com.shahkabir.coffer.model.Account;
import com.shahkabir.coffer.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void getAccount_returnsAccountWhenItExists() {
        UUID accountId = UUID.randomUUID();
        Account account = mock(Account.class);

        when (accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        Account result = accountService.getAccount(accountId);

        assertSame(account, result);
        verify(accountRepository).findById(accountId);
    }

    @Test
    void getAccount_throwsWhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccount(accountId)
        );

        verify(accountRepository).findById(accountId);
    }
}
