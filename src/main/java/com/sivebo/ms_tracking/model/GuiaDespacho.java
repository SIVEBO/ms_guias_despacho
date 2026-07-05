package com.sivebo.ms_tracking.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GUIA_DESPACHO")
public class GuiaDespacho {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        @Column(name = "codigo_tracking", nullable = false, unique = true, length = 12)
        String codigoTracking;

        @Column(name = "codigo_admision", nullable = false, length = 20)
        String codigoAdmision;

        @Column(name = "fecha_creacion", nullable = false)
        LocalDateTime fechaCreacion;
}
