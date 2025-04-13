package com.joia.repository;

import com.joia.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    Optional<Cliente> findByEmail(String email);

    List<Cliente> findByPedidosIsNotEmpty();

    List<Cliente> findAllByOrderByNomeDesc();

    @Query("SELECT c FROM clientes c WHERE SIZE(c.pedidos) = 0")
    List<Cliente> findClientesSemPedidos();
}
