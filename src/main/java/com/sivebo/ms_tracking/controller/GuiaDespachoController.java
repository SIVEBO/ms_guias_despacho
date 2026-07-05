package com.sivebo.ms_tracking.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sivebo.ms_tracking.dto.request.GuiaDespachoRequestDTO;
import com.sivebo.ms_tracking.dto.response.GuiaDespachoResponseDTO;
import com.sivebo.ms_tracking.service.GuiaDespachoService;

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
@RequestMapping("api/v1/guias")
@RequiredArgsConstructor
@Tag(name = "Guías de Despacho", description = "Gestión de guías de despacho y códigos de tracking")
public class GuiaDespachoController {

        private final GuiaDespachoService guiaDespachoService;

        @Operation(summary = "Listar todas las guías", description = "Retorna todas las guías de despacho registradas")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Guías obtenidas exitosamente",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = GuiaDespachoResponseDTO.class)))
        })
        @GetMapping
        public List<GuiaDespachoResponseDTO> getAll() {
                return guiaDespachoService.getAll();
        }

        @Operation(summary = "Obtener guía por ID", description = "Retorna una guía de despacho por su ID interno")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Guía encontrada",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = GuiaDespachoResponseDTO.class))),
                @ApiResponse(responseCode = "404", description = "Guía no encontrada",
                        content = @Content(mediaType = "application/json"))
        })
        @GetMapping("/{id}")
        public ResponseEntity<GuiaDespachoResponseDTO> getById(@PathVariable Long id) {
                return guiaDespachoService.getById(id)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        }

        @Operation(
                summary = "Buscar guía por atributo",
                description = "Busca por 'buscar?codigoTracking=*' o 'buscar?codigoAdmision=*'"
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Guía encontrada",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = GuiaDespachoResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Parámetro inválido",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(responseCode = "404", description = "Guía no encontrada",
                        content = @Content(mediaType = "application/json"))
        })
        @GetMapping("/buscar")
        public ResponseEntity<?> buscar(
                @RequestParam(required = false) String codigoTracking,
                @RequestParam(required = false) String codigoAdmision) {

                long provided = Stream.of(codigoTracking, codigoAdmision)
                        .filter(Objects::nonNull).count();

                if (provided == 0) {
                        return ResponseEntity.badRequest().body("Debe proporcionar un atributo de búsqueda válido");
                } else if (provided > 1) {
                        return ResponseEntity.badRequest().body("Solo se permite un atributo de búsqueda a la vez");
                } else if (codigoTracking != null) {
                        log.info(">>> Buscando guía por código de tracking: {}", codigoTracking);
                        return guiaDespachoService.getByCodigoTracking(codigoTracking)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
                } else {
                        log.info(">>> Buscando guía por código de admisión: {}", codigoAdmision);
                        return guiaDespachoService.getByCodigoAdmision(codigoAdmision)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
                }
        }

        @Operation(
                summary = "Registrar una guía de despacho",
                description = "Crea una nueva guía y registra automáticamente el estado inicial RECIBIDO"
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Guía creada exitosamente",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = GuiaDespachoResponseDTO.class))),
                @ApiResponse(responseCode = "400", description = "Datos inválidos",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(responseCode = "404", description = "Admisión no encontrada en ms_admision",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(responseCode = "503", description = "ms_admision no disponible",
                        content = @Content(mediaType = "application/json"))
        })
        @PostMapping
        public ResponseEntity<GuiaDespachoResponseDTO> create(@Valid @RequestBody GuiaDespachoRequestDTO dto) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(guiaDespachoService.create(dto));
        }

        @Operation(summary = "Eliminar una guía", description = "Elimina una guía de despacho por su ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Guía eliminada exitosamente",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(responseCode = "404", description = "Guía no encontrada",
                        content = @Content(mediaType = "application/json"))
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<String> delete(@PathVariable Long id) {
                if (guiaDespachoService.delete(id)) {
                        return ResponseEntity.ok("Guía eliminada");
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Guía no encontrada");
                }
        }
}
