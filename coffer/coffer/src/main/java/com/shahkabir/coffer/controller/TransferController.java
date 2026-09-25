package com.shahkabir.coffer.controller;


import com.shahkabir.coffer.dto.CreateTransferRequest;
import com.shahkabir.coffer.dto.TransferResponse;
import com.shahkabir.coffer.model.LedgerTransaction;
import com.shahkabir.coffer.service.LedgerService;
import com.shahkabir.coffer.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {
    private final LedgerService ledgerService;
    private final TransferService transferService;

    public TransferController(LedgerService ledgerService,
                              TransferService transferService) {
        this.ledgerService = ledgerService;
        this.transferService = transferService;
    }


    @GetMapping("/{id}")
    public TransferResponse getTransferById(@PathVariable UUID id) {
        LedgerTransaction transaction = ledgerService.getTransaction(id);

        return new TransferResponse(
                transaction.getId(),
                transaction.getStatus(),
                transaction.getFromAccount().getId(),
                transaction.getToAccount().getId(),
                transaction.getSourceAmount(),
                transaction.getSourceCurrency(),
                transaction.getDestinationAmount(),
                transaction.getDestinationCurrency(),
                transaction.getRate()
        );
    }

    @GetMapping
    public List<TransferResponse> getAllTransfers() {
        return ledgerService.getAllTransactions()
                .stream()
                .map(transaction -> new TransferResponse(
                        transaction.getId(),
                        transaction.getStatus(),
                        transaction.getFromAccount().getId(),
                        transaction.getToAccount().getId(),
                        transaction.getSourceAmount(),
                        transaction.getSourceCurrency(),
                        transaction.getDestinationAmount(),
                        transaction.getDestinationCurrency(),
                        transaction.getRate()
                ))
                .toList();
    }


    @PostMapping
    public TransferResponse createTransfer(
            @RequestBody @Valid CreateTransferRequest request) {
        LedgerTransaction transaction = transferService.transfer(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount()
        );

        return new TransferResponse(
                transaction.getId(),
                transaction.getStatus(),
                transaction.getFromAccount().getId(),
                transaction.getToAccount().getId(),
                transaction.getSourceAmount(),
                transaction.getSourceCurrency(),
                transaction.getDestinationAmount(),
                transaction.getDestinationCurrency(),
                transaction.getRate()
        );
    }
}
