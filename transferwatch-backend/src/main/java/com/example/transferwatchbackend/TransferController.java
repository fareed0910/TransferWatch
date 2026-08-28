package com.example.transferwatchbackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;
    private static final int DEFAULT_TEAM_ID = 33;

    public TransferController(
            TransferService transferService
    ) {
        this.transferService =
                transferService;
    }

    @GetMapping
    public List<Transfer> getTransfers() {

        return transferService.getTransfers(DEFAULT_TEAM_ID);
    }
}