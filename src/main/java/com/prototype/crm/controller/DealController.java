package com.prototype.crm.controller;

import com.prototype.crm.model.dto.emuns.DealStage;
import com.prototype.crm.model.dto.request.DealReqDTO;
import com.prototype.crm.model.dto.response.DealRespDTO;
import com.prototype.crm.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deal")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @PostMapping ({"", "/"})
    public ResponseEntity<DealRespDTO> createDeal(
            @RequestBody DealReqDTO dto
    ) {
        DealRespDTO createdDeal = dealService.createDeal(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDeal);
    }

    @GetMapping ({"", "/"})
    public ResponseEntity<List<DealRespDTO>> searchDeals(
            @RequestParam(required = false) String filter
    ) {
        return ResponseEntity.ok(dealService.searchDeal(filter));
    }

    // 4. Mover oportunidad de etapa (PATCH - Actualización parcial)
    // Frontend: PATCH /api/v1/deals/{id}/stage?newStage=NEGOTIATION
    @PatchMapping("/{id}/stage")
    public ResponseEntity<DealRespDTO> moveStage(
            @PathVariable UUID id,
            @RequestParam DealStage newStage) {
        DealRespDTO updatedDeal = dealService.moveDealStage(id, newStage);
        return ResponseEntity.ok(updatedDeal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DealRespDTO> updateDeal(
            @PathVariable UUID id,
            @RequestBody DealReqDTO dto) {
        return ResponseEntity.ok(dealService.updateDeal(id, dto));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteDeal(@PathVariable UUID id) {
        dealService.deleteDeal(id);
        return ResponseEntity.noContent().build();
    }

}
