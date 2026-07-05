package com.sivebo.ms_tracking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sivebo.ms_tracking.model.GuiaDespacho;

public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {

        Optional<GuiaDespacho> findByCodigoTracking(String codigoTracking);

        Optional<GuiaDespacho> findByCodigoAdmision(String codigoAdmision);
}
