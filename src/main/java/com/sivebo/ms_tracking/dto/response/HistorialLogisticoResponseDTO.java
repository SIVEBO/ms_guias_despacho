package com.sivebo.ms_tracking.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialLogisticoResponseDTO {

        Long id;
        String codigoTracking;
        String nombreEstado;
        String nombreSucursalActual;
        String username;
        LocalDateTime fechaHora;
        String comentario;
}
