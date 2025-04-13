package com.joia.repository;

import com.joia.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByDataPedido(Date dataPedido);

    List<Pedido> findByDataPedidoBetween(Date startDate, Date endDate);
    
    @Query("SELECT p FROM pedidos p JOIN p.cliente c WHERE lower(c.nome) LIKE lower(concat('%', :nomeCliente, '%'))")
    List<Pedido> findByClienteNomeContainingIgnoreCase(@Param("nomeCliente") String nomeCliente);
}
