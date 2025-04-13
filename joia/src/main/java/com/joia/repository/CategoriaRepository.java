package com.joia.repository;

import com.joia.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByNomeContainingIgnoreCase(String nome);

    List<Categoria> findAllByOrderByNomeAsc();
}
