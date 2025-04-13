package com.joia.repository;

import com.joia.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    List<Fornecedor> findByNomeContainingIgnoreCase(String nome);

    List<Fornecedor> findByJoiasIsNotEmpty();

    List<Fornecedor> findAllByOrderByNomeAsc();

    List<Fornecedor> findByJoiasIsEmpty();
}
