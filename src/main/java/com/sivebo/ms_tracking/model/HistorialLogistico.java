package com.sivebo.ms_tracking.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HISTORIAL_LOGISTICO")
public class HistorialLogistico {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        @ManyToOne(optional = false)
        @JoinColumn(name = "codigo_tracking", referencedColumnName = "codigo_tracking")
        GuiaDespacho guia;

        @ManyToOne(optional = false)
        @JoinColumn(name = "nombre_estado", referencedColumnName = "nombre_estado")
        EstadoMaestro estado;

        @Column(name = "nombre_sucursal_actual")
        String nombreSucursalActual;

        @Column(name = "username")
        String username;

        @Column(name = "fecha_hora", nullable = false)
        LocalDateTime fechaHora;

        @Column(name = "comentario")
        String comentario;
}
