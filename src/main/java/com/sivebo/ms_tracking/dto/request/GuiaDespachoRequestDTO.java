package com.sivebo.ms_tracking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuiaDespachoRequestDTO {

        @Size(min = 12, max = 12, message = "El código de tracking debe tener exactamente 12 caracteres")
        @NotBlank(message = "El código de tracking es obligatorio")
        String codigoTracking;

        @NotBlank(message = "El código de admisión es obligatorio")
        String codigoAdmision;
}
