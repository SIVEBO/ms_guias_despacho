package com.sivebo.ms_tracking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sivebo.ms_tracking.dto.request.HistorialLogisticoRequestDTO;
import com.sivebo.ms_tracking.dto.response.HistorialLogisticoResponseDTO;
import com.sivebo.ms_tracking.exception.MicroserviceValidationException;
import com.sivebo.ms_tracking.model.EstadoMaestro;
import com.sivebo.ms_tracking.model.GuiaDespacho;
import com.sivebo.ms_tracking.model.HistorialLogistico;
import com.sivebo.ms_tracking.repository.EstadoMaestroRepository;
import com.sivebo.ms_tracking.repository.GuiaDespachoRepository;
import com.sivebo.ms_tracking.repository.HistorialLogisticoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistorialLogisticoService {

        private final HistorialLogisticoRepository historialRepository;
        private final GuiaDespachoRepository guiaDespachoRepository;
        private final EstadoMaestroRepository estadoMaestroRepository;

        private HistorialLogisticoResponseDTO mapToDTO(HistorialLogistico h) {
                return new HistorialLogisticoResponseDTO(
                        h.getId(),
                        h.getGuia().getCodigoTracking(),
                        h.getEstado().getTipoEstado().name(),
                        h.getNombreSucursalActual(),
                        h.getUsername(),
                        h.getFechaHora(),
                        h.getComentario()
                );
        }

        @Transactional(readOnly = true)
        public List<HistorialLogisticoResponseDTO> getAll() {
                return historialRepository.findAll()
                        .stream().map(this::mapToDTO).toList();
        }

        @Transactional(readOnly = true)
        public List<HistorialLogisticoResponseDTO> getByGuiaId(String codigoTracking) {
                return historialRepository.findByGuiaCodigoTrackingOrderByFechaHoraAsc(codigoTracking)
                        .stream().map(this::mapToDTO).toList();
        }

        @Transactional(readOnly = true)
        public Optional<HistorialLogisticoResponseDTO> getEstadoActual(String codigoTracking) {
                return historialRepository.findTopByGuiaCodigoTrackingOrderByFechaHoraDesc(codigoTracking)
                        .map(this::mapToDTO);
        }

        @Transactional
        public HistorialLogisticoResponseDTO create(HistorialLogisticoRequestDTO dto) {
                GuiaDespacho guia = guiaDespachoRepository.findByCodigoTracking(dto.getCodigoTracking())
                        .orElseThrow(() -> new MicroserviceValidationException(
                                "Guía con código de tracking " + dto.getCodigoTracking() + " no encontrada"));
                EstadoMaestro estado = estadoMaestroRepository.findByTipoEstado(dto.getNombreEstado())
                        .orElseThrow(() -> new MicroserviceValidationException(
                                "Estado " + dto.getNombreEstado() + " no encontrado"));
                return mapToDTO(historialRepository.save(
                        new HistorialLogistico(
                                null, guia, estado,
                                dto.getNombreSucursalActual(), dto.getUsername(),
                                dto.getFechaHora(), dto.getComentario()
                        )
                ));
        }

        @Transactional
        public Boolean delete(Long id) {
                historialRepository.deleteById(id);
                return !historialRepository.existsById(id);
        }
}
