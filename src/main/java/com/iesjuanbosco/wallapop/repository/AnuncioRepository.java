package com.iesjuanbosco.wallapop.repository;

import com.iesjuanbosco.wallapop.entity.Anuncio;
import com.iesjuanbosco.wallapop.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {

    List<Anuncio> findAllByOrderByFechaCreacionDesc(); // Listar anuncios por fecha descendente

    List<Anuncio> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario); // Listar anuncios de un usuario
}
