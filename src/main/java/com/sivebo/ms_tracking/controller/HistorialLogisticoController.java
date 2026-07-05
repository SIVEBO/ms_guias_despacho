package com.sivebo.ms_tracking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sivebo.ms_tracking.dto.request.HistorialLogisticoRequestDTO;
import com.sivebo.ms_tracking.dto.response.HistorialLogisticoResponseDTO;
import com.sivebo.ms_tracking.service.HistorialLogisticoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1/historial")
@RequiredArgsConstructor
@Tag(name = "Historial Logístico", description = "Registro y consulta del historial de estados de guías")
public class HistorialLogisticoController {

        private final HistorialLogisticoService historialLogisticoService;

        @Operation(summary = "Listar todo el historial", description = "Retorna todos los registros de historial logístico")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = HistorialLogisticoResponseDTO.class)))
        })
        @GetMapping
        public List<HistorialLogisticoResponseDTO> getAll() {
                return historialLogisticoService.getAll();
        }

        @Operation(
                summary = "Obtener estado actual de una guía",
                description = "Retorna el último estado registrado para la guía indicada. " +
                        "El campo 'nombreEstado' es consumido por ms_portal"
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Estado actual obtenido",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = HistorialLogisticoResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Sin historial para la guía indicada",
                        content = @Content(mediaType = "application/json"))
        })
        @GetMapping("/estado-actual/{codigoTracking}")
        public ResponseEntity<HistorialLogisticoResponseDTO> getEstadoActual(@PathVariable String codigoTracking) {
                return historialLogisticoService.getEstadoActual(codigoTracking)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        }

        @Operation(
                summary = "Historial cronológico de una guía",
                description = "Retorna todos los cambios de estado de una guía ordenados por fecha ascendente"
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = HistorialLogisticoResponseDTO.class)))
        })
        @GetMapping("/{codigoTracking}")
        public List<HistorialLogisticoResponseDTO> getByGuiaId(@PathVariable String codigoTracking) {
                return historialLogisticoService.getByGuiaId(codigoTracking);
        }

        @Operation(
                summary = "Registrar cambio de estado",
                description = "Agrega una nueva entrada al historial logístico de una guía"
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Estado registrado exitosamente",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = HistorialLogisticoResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Datos inválidos",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(responseCode = "404", description = "Guía o estado no encontrado",
                        content = @Content(mediaType = "application/json"))
        })
        @PostMapping
        public ResponseEntity<HistorialLogisticoResponseDTO> create(
                @Valid @RequestBody HistorialLogisticoRequestDTO dto) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(historialLogisticoService.create(dto));
        }

        @Operation(summary = "Eliminar entrada de historial", description = "Elimina un registro del historial por ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Registro eliminado exitosamente",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(responseCode = "404", description = "Registro no encontrado",
                        content = @Content(mediaType = "application/json"))
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<String> delete(@PathVariable Long id) {
                if (historialLogisticoService.delete(id)) {
                        return ResponseEntity.ok("Registro eliminado");
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registro no encontrado");
                }
        }
}
