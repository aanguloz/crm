package com.prototype.crm.controller;

import com.prototype.crm.model.dto.request.ClientsReqDTO;
import com.prototype.crm.model.dto.response.ClientsRespDTO;
import com.prototype.crm.service.ClientsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientsController {

    private final ClientsService clientsService;

    @PostMapping ({"", "/"})
    public ResponseEntity<ClientsRespDTO> createClient(
            @RequestBody ClientsReqDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientsService.addClients(dto));
    }

    @GetMapping ({"", "/"})
    public ResponseEntity<List<ClientsRespDTO>> getClients(
            @RequestParam(required = false) String filter
    ) {
        return ResponseEntity.ok(clientsService.searchClients(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientsRespDTO> getClientById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(clientsService.getClientById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientsRespDTO> updateClient(
            @PathVariable UUID id, @RequestBody ClientsReqDTO dto
    ) {
        return ResponseEntity.ok(clientsService.updateClient(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateClient(
            @PathVariable UUID id
    ) {
        clientsService.deactivateClient(id);
        return ResponseEntity.noContent().build();
    }
}
