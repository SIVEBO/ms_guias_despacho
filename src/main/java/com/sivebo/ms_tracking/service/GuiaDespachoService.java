package com.sivebo.ms_tracking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.sivebo.ms_tracking.dto.request.GuiaDespachoRequestDTO;
import com.sivebo.ms_tracking.dto.response.GuiaDespachoResponseDTO;
import com.sivebo.ms_tracking.exception.MicroserviceValidationException;
import com.sivebo.ms_tracking.model.EstadoMaestro;
import com.sivebo.ms_tracking.model.GuiaDespacho;
import com.sivebo.ms_tracking.model.HistorialLogistico;
import com.sivebo.ms_tracking.model.TipoEstado;
import com.sivebo.ms_tracking.repository.EstadoMaestroRepository;
import com.sivebo.ms_tracking.repository.GuiaDespachoRepository;
import com.sivebo.ms_tracking.repository.HistorialLogisticoRepository;
import com.sivebo.ms_tracking.utils.WebClientUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuiaDespachoService {

        private final GuiaDespachoRepository guiaDespachoRepository;
        private final EstadoMaestroRepository estadoMaestroRepository;
        private final HistorialLogisticoRepository historialRepository;
        private final WebClientUtil webClientUtil;

        @Qualifier("admisionWebClient")
        private final WebClient admisionWebClient;

        private GuiaDespachoResponseDTO mapToDTO(GuiaDespacho guia) {
                return new GuiaDespachoResponseDTO(
                        guia.getId(),
                        guia.getCodigoTracking(),
                        guia.getCodigoAdmision(),
                        guia.getFechaCreacion()
                );
        }

        @Transactional(readOnly = true)
        public List<GuiaDespachoResponseDTO> getAll() {
                return guiaDespachoRepository.findAll()
                        .stream().map(this::mapToDTO).toList();
        }

        @Transactional(readOnly = true)
        public Optional<GuiaDespachoResponseDTO> getById(Long id) {
                return guiaDespachoRepository.findById(id).map(this::mapToDTO);
        }

        @Transactional(readOnly = true)
        public Optional<GuiaDespachoResponseDTO> getByCodigoTracking(String codigoTracking) {
                return guiaDespachoRepository.findByCodigoTracking(codigoTracking).map(this::mapToDTO);
        }

        @Transactional(readOnly = true)
        public Optional<GuiaDespachoResponseDTO> getByCodigoAdmision(String codigoAdmision) {
                return guiaDespachoRepository.findByCodigoAdmision(codigoAdmision).map(this::mapToDTO);
        }

        @Transactional
        public GuiaDespachoResponseDTO create(GuiaDespachoRequestDTO dto) {
                webClientUtil.validateMicroServiceByQuery("admisiones", "codigoAdmision", dto.getCodigoAdmision(), admisionWebClient);
                GuiaDespacho saved = guiaDespachoRepository.save(
                        new GuiaDespacho(null, dto.getCodigoTracking(), dto.getCodigoAdmision(), LocalDateTime.now())
                );
                EstadoMaestro recibido = estadoMaestroRepository.findByTipoEstado(TipoEstado.RECIBIDO)
                        .orElseThrow(() -> new MicroserviceValidationException(
                                "Estado RECIBIDO no encontrado en ESTADO_MAESTRO"));
                historialRepository.save(
                        new HistorialLogistico(null, saved, recibido, null, null, LocalDateTime.now(), null)
                );
                return mapToDTO(saved);
        }

        @Transactional
        public Boolean delete(Long id) {
                historialRepository.deleteAll(historialRepository.findByGuiaId(id));
                guiaDespachoRepository.deleteById(id);
                return !guiaDespachoRepository.existsById(id);
        }
}
