package com.example.transferwatchbackend.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransferController {

    private final TransferService transferService;

    public TransferController(
            TransferService transferService
    ) {
        this.transferService =
                transferService;
    }

    @GetMapping("/teams/{teamId}/transfers")
    public List<Transfer> getTransfersForTeam(
            @PathVariable int teamId
    ) {
        if (teamId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Team ID must be positive"
            );
        }

        return transferService.getTransfers(teamId);
    }



}