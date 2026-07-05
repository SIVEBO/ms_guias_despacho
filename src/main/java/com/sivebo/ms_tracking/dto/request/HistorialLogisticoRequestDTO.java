package com.sivebo.ms_tracking.dto.request;

import java.time.LocalDateTime;

import com.sivebo.ms_tracking.model.TipoEstado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialLogisticoRequestDTO {

        @NotBlank(message = "El código de tracking de la guía es obligatorio")
        String codigoTracking;

        @NotNull(message = "El estado es obligatorio")
        TipoEstado nombreEstado;

        String nombreSucursalActual;

        String username;

        @NotNull(message = "La fecha y hora son obligatorias")
        LocalDateTime fechaHora;

        String comentario;
}
