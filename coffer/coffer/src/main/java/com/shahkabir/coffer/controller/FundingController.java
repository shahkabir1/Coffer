package com.shahkabir.coffer.controller;


import com.shahkabir.coffer.dto.DepositRequest;
import com.shahkabir.coffer.dto.FundingResponse;
import com.shahkabir.coffer.dto.WithdrawalRequest;
import com.shahkabir.coffer.model.enums.FundingType;
import com.shahkabir.coffer.model.LedgerTransaction;
import com.shahkabir.coffer.service.FundingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/funding")
public class FundingController {

    private final FundingService fundingService;

    public FundingController(FundingService fundingService) {
        this.fundingService = fundingService;
    }


    @PostMapping("/deposits")
    public FundingResponse deposit(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody DepositRequest request){
        LedgerTransaction transaction = fundingService
                .deposit(request.accountId(), request.amount(), idempotencyKey);

        return new FundingResponse(
                transaction.getId(),
                transaction.getStatus(),
                FundingType.DEPOSIT,
                transaction.getToAccount().getId(),
                transaction.getDestinationAmount(),
                transaction.getDestinationCurrency()
        );
    }

    @PostMapping("/withdrawals")
    public FundingResponse withdraw(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody WithdrawalRequest request){
        LedgerTransaction transaction = fundingService
                .withdraw(request.accountId(), request.amount(), idempotencyKey);

        return new FundingResponse(
                transaction.getId(),
                transaction.getStatus(),
                FundingType.WITHDRAWAL,
                transaction.getToAccount().getId(),
                transaction.getDestinationAmount(),
                transaction.getDestinationCurrency()
        );
    }
}
