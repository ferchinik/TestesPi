package com.joia.repository;

import com.joia.entity.Joia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface JoiaRepository extends JpaRepository<Joia, Long> {

    List<Joia> findAllByOrderByNomeAsc();

    @Query("SELECT j FROM joias j " + // "joias" é o nome da entidade definido em @Entity(name="joias")
            "WHERE (:nome IS NULL OR lower(j.nome) LIKE lower(concat('%', :nome, '%'))) " +
            "AND (:categoriaId IS NULL OR j.categoria.id = :categoriaId) " +
            "AND (:fornecedorId IS NULL OR j.fornecedor.id = :fornecedorId) " +
            "AND (:precoMin IS NULL OR j.preco >= :precoMin) " +
            "ORDER BY j.nome ASC") // Adiciona ordenação padrão se desejar
    List<Joia> filtrar(
            @Param("nome") String nome,
            @Param("categoriaId") Long categoriaId,
            @Param("fornecedorId") Long fornecedorId,
            @Param("precoMin") BigDecimal precoMin
    );
}
