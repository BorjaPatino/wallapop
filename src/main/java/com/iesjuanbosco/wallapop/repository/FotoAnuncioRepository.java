package com.iesjuanbosco.wallapop.repository;

import com.iesjuanbosco.wallapop.entity.FotoAnuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoAnuncioRepository extends JpaRepository<FotoAnuncio, Long> {
}
