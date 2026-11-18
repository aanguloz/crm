package com.prototype.crm.controller;

import com.prototype.crm.model.dto.request.ActivityReqDTO;
import com.prototype.crm.model.dto.response.ActivityRespDTO;
import com.prototype.crm.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    // 1. Agendar nueva actividad
    @PostMapping("/")
    ResponseEntity<ActivityRespDTO> createActivity(@RequestBody ActivityReqDTO dto) {
        ActivityRespDTO created = activityService.createActivity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 2. Listar todas las actividades (o filtradas por fecha/usuario)
    @GetMapping("/")
    ResponseEntity<List<ActivityRespDTO>> getAllActivities(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // Si se pasa fecha, filtra por ese día. Si no, trae todas las futuras/pendientes.
        return ResponseEntity.ok(activityService.getActivities(date));
    }

    // 3. Obtener actividades de un cliente específico (Para ver el historial en el detalle del cliente)
    @GetMapping("/client/{clientId}")
    ResponseEntity<List<ActivityRespDTO>> getActivitiesByClient(@PathVariable UUID clientId) {
        return ResponseEntity.ok(activityService.getActivitiesByClient(clientId));
    }

    // 4. Actualizar actividad (ej. cambiar fecha o marcar como completada)
    @PutMapping("/{id}")
    ResponseEntity<ActivityRespDTO> updateActivity(
            @PathVariable UUID id,
            @RequestBody ActivityReqDTO dto) {
        return ResponseEntity.ok(activityService.updateActivity(id, dto));
    }

    // 5. Eliminar actividad
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteActivity(@PathVariable UUID id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

}
