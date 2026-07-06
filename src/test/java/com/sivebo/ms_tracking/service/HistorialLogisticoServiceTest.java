package com.sivebo.ms_tracking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sivebo.ms_tracking.dto.request.HistorialLogisticoRequestDTO;
import com.sivebo.ms_tracking.dto.response.HistorialLogisticoResponseDTO;
import com.sivebo.ms_tracking.exception.MicroserviceValidationException;
import com.sivebo.ms_tracking.model.EstadoMaestro;
import com.sivebo.ms_tracking.model.GuiaDespacho;
import com.sivebo.ms_tracking.model.HistorialLogistico;
import com.sivebo.ms_tracking.model.TipoEstado;
import com.sivebo.ms_tracking.repository.EstadoMaestroRepository;
import com.sivebo.ms_tracking.repository.GuiaDespachoRepository;
import com.sivebo.ms_tracking.repository.HistorialLogisticoRepository;

@ExtendWith(MockitoExtension.class)
class HistorialLogisticoServiceTest {

        @Mock HistorialLogisticoRepository historialRepository;
        @Mock GuiaDespachoRepository guiaDespachoRepository;
        @Mock EstadoMaestroRepository estadoMaestroRepository;

        @InjectMocks HistorialLogisticoService service;

        private static final GuiaDespacho GUIA =
                new GuiaDespacho(1L, "ABC123456789", "ADM123456789", LocalDateTime.of(2026, 1, 1, 0, 0));

        private static final EstadoMaestro RECIBIDO =
                new EstadoMaestro(1L, TipoEstado.RECIBIDO, 1);

        private static final LocalDateTime FECHA = LocalDateTime.of(2026, 1, 1, 12, 0);

        private static final HistorialLogistico HISTORIAL =
                new HistorialLogistico(1L, GUIA, RECIBIDO, null, null, FECHA, null);

        @Test
        void getAllReturnsAllMapped() {
                when(historialRepository.findAll()).thenReturn(List.of(HISTORIAL));

                List<HistorialLogisticoResponseDTO> result = service.getAll();

                assertEquals(1, result.size());
                assertEquals("RECIBIDO", result.get(0).getNombreEstado());
                assertEquals("ABC123456789", result.get(0).getCodigoTracking());
        }

        @Test
        void getByGuiaIdReturnsChronologicalList() {
                when(historialRepository.findByGuiaCodigoTrackingOrderByFechaHoraAsc("ABC123456789"))
                        .thenReturn(List.of(HISTORIAL));

                List<HistorialLogisticoResponseDTO> result = service.getByGuiaId("ABC123456789");

                assertEquals(1, result.size());
                assertEquals("RECIBIDO", result.get(0).getNombreEstado());
        }

        @Test
        void getEstadoActualFoundReturnsDTO() {
                when(historialRepository.findTopByGuiaCodigoTrackingOrderByFechaHoraDesc("ABC123456789"))
                        .thenReturn(Optional.of(HISTORIAL));

                Optional<HistorialLogisticoResponseDTO> result = service.getEstadoActual("ABC123456789");

                assertTrue(result.isPresent());
                assertEquals("RECIBIDO", result.get().getNombreEstado());
                assertEquals(FECHA, result.get().getFechaHora());
        }

        @Test
        void getEstadoActualNoHistoryReturnsEmpty() {
                when(historialRepository.findTopByGuiaCodigoTrackingOrderByFechaHoraDesc("ZZZ999999999"))
                        .thenReturn(Optional.empty());

                assertTrue(service.getEstadoActual("ZZZ999999999").isEmpty());
        }

        @Test
        void createValidRequestSavesAndReturnsDTO() {
                HistorialLogisticoRequestDTO dto =
                        new HistorialLogisticoRequestDTO("ABC123456789", TipoEstado.RECIBIDO, null, null, FECHA, "ok");
                HistorialLogistico saved =
                        new HistorialLogistico(2L, GUIA, RECIBIDO, null, null, FECHA, "ok");

                when(guiaDespachoRepository.findByCodigoTracking("ABC123456789")).thenReturn(Optional.of(GUIA));
                when(estadoMaestroRepository.findByTipoEstado(TipoEstado.RECIBIDO)).thenReturn(Optional.of(RECIBIDO));
                when(historialRepository.save(any())).thenReturn(saved);

                HistorialLogisticoResponseDTO result = service.create(dto);

                assertEquals("RECIBIDO", result.getNombreEstado());
                assertEquals("ok", result.getComentario());
                verify(historialRepository).save(any(HistorialLogistico.class));
        }

        @Test
        void createGuiaNotFoundThrowsMicroserviceValidationException() {
                HistorialLogisticoRequestDTO dto =
                        new HistorialLogisticoRequestDTO("ZZZ999999999", TipoEstado.RECIBIDO, null, null, FECHA, null);
                when(guiaDespachoRepository.findByCodigoTracking("ZZZ999999999")).thenReturn(Optional.empty());

                assertThrows(MicroserviceValidationException.class, () -> service.create(dto));
        }

        @Test
        void createEstadoNotFoundThrowsMicroserviceValidationException() {
                HistorialLogisticoRequestDTO dto =
                        new HistorialLogisticoRequestDTO("ABC123456789", TipoEstado.EN_TRANSITO, null, null, FECHA, null);
                when(guiaDespachoRepository.findByCodigoTracking("ABC123456789")).thenReturn(Optional.of(GUIA));
                when(estadoMaestroRepository.findByTipoEstado(TipoEstado.EN_TRANSITO)).thenReturn(Optional.empty());

                assertThrows(MicroserviceValidationException.class, () -> service.create(dto));
        }

        @Test
        void deleteDeletesAndReturnsTrue() {
                doNothing().when(historialRepository).deleteById(1L);
                when(historialRepository.existsById(1L)).thenReturn(false);

                assertTrue(service.delete(1L));
        }
}
