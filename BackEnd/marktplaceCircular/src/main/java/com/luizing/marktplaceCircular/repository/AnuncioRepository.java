package com.luizing.marktplaceCircular.repository;

import com.luizing.marktplaceCircular.model.anuncio.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
}
