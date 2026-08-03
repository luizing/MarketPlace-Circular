package com.luizing.marktplaceCircular.repository;

import com.luizing.marktplaceCircular.model.anuncio.Anuncio;
import com.luizing.marktplaceCircular.model.anuncio.CategoriaAnuncio;
import com.luizing.marktplaceCircular.model.anuncio.StatusAnuncio;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {

    long countByUsuarioId(Long usuarioId);

    long countByStatus(StatusAnuncio status);

    Page<Anuncio> findByStatusOrderByIdDesc(StatusAnuncio status, Pageable pageable);

    Page<Anuncio> findByStatusAndTituloContainingIgnoreCaseOrderByIdDesc(
            StatusAnuncio status, String titulo, Pageable pageable);

    Page<Anuncio> findByStatusAndCategoriaInOrderByIdDesc(
            StatusAnuncio status, List<CategoriaAnuncio> categorias, Pageable pageable);

    Page<Anuncio> findByStatusAndTituloContainingIgnoreCaseAndCategoriaInOrderByIdDesc(
            StatusAnuncio status,
            String titulo,
            List<CategoriaAnuncio> categorias,
            Pageable pageable
    );

    Page<Anuncio> findAllByOrderByIdDesc(Pageable pageable);

    Page<Anuncio> findByTituloContainingIgnoreCaseOrderByIdDesc(String titulo, Pageable pageable);

    Page<Anuncio> findByCategoriaInOrderByIdDesc(List<CategoriaAnuncio> categorias, Pageable pageable);

    Page<Anuncio> findByTituloContainingIgnoreCaseAndCategoriaInOrderByIdDesc(
            String titulo,
            List<CategoriaAnuncio> categorias,
            Pageable pageable
    );

    Page<Anuncio> findByUsuarioIdOrderByIdDesc(Long usuarioId, Pageable pageable);

    Page<Anuncio> findByUsuarioIdAndTituloContainingIgnoreCaseOrderByIdDesc(
            Long usuarioId,
            String titulo,
            Pageable pageable
    );

    Page<Anuncio> findByUsuarioIdAndCategoriaInOrderByIdDesc(
            Long usuarioId,
            List<CategoriaAnuncio> categorias,
            Pageable pageable
    );

    Page<Anuncio> findByUsuarioIdAndTituloContainingIgnoreCaseAndCategoriaInOrderByIdDesc(
            Long usuarioId,
            String titulo,
            List<CategoriaAnuncio> categorias,
            Pageable pageable
    );

    @Query("""
            SELECT anuncio FROM Anuncio anuncio
            JOIN anuncio.interessados interessado
            WHERE interessado.id = :usuarioId
            ORDER BY anuncio.id DESC
            """)
    Page<Anuncio> findInteressantesByUsuarioId(
            @Param("usuarioId") Long usuarioId,
            Pageable pageable
    );

    @Query("""
            SELECT anuncio FROM Anuncio anuncio
            JOIN anuncio.interessados interessado
            WHERE interessado.id = :usuarioId
            AND LOWER(anuncio.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))
            ORDER BY anuncio.id DESC
            """)
    Page<Anuncio> findInteressantesByUsuarioIdAndTitulo(
            @Param("usuarioId") Long usuarioId,
            @Param("titulo") String titulo,
            Pageable pageable
    );

    @Query("""
            SELECT anuncio FROM Anuncio anuncio
            JOIN anuncio.interessados interessado
            WHERE interessado.id = :usuarioId
            AND anuncio.categoria IN :categorias
            ORDER BY anuncio.id DESC
            """)
    Page<Anuncio> findInteressantesByUsuarioIdAndCategorias(
            @Param("usuarioId") Long usuarioId,
            @Param("categorias") List<CategoriaAnuncio> categorias,
            Pageable pageable
    );

    @Query("""
            SELECT anuncio FROM Anuncio anuncio
            JOIN anuncio.interessados interessado
            WHERE interessado.id = :usuarioId
            AND LOWER(anuncio.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))
            AND anuncio.categoria IN :categorias
            ORDER BY anuncio.id DESC
            """)
    Page<Anuncio> findInteressantesByUsuarioIdAndTituloAndCategorias(
            @Param("usuarioId") Long usuarioId,
            @Param("titulo") String titulo,
            @Param("categorias") List<CategoriaAnuncio> categorias,
            Pageable pageable
    );
}
