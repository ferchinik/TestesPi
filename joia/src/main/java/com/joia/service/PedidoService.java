package com.joia.service;

import com.joia.dto.FaturamentoDTO;
import com.joia.entity.Joia;
import com.joia.entity.Pedido;
import com.joia.repository.PedidoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils; // Para checar coleções vazias
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date; // Import correto para Date
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public String save(@Valid Pedido pedidoEntity) {
        validarPedido(pedidoEntity);
        pedidoRepository.save(pedidoEntity);
        return "Pedido salvo com sucesso!";
    }

    public Pedido findById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido ID " + id + " não encontrado!"));
    }

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public String update(@Valid Pedido pedidoEntity, Long id) {
        Pedido pedidoExistente = findById(id); // Reusa findById

        validarPedido(pedidoEntity);

        pedidoExistente.setDataPedido(pedidoEntity.getDataPedido());
        pedidoExistente.setCliente(pedidoEntity.getCliente());
        pedidoExistente.getJoias().clear();
        if (pedidoEntity.getJoias() != null) {
            pedidoExistente.getJoias().addAll(pedidoEntity.getJoias());
        }

        pedidoRepository.save(pedidoExistente);
        return "Pedido ID " + id + " atualizado com sucesso!";
    }

    public String delete(Long id) {
        findById(id); // Garante que existe antes de deletar
        pedidoRepository.deleteById(id);
        return "Pedido ID " + id + " deletado com sucesso!";
    }

    public List<Pedido> findByDataPedido(Date dataPedido) {
        if (dataPedido == null) {
            throw new IllegalArgumentException("Data para busca não pode ser nula.");
        }
        return pedidoRepository.findByDataPedido(dataPedido);
    }

    public List<Pedido> findByDataPedidoBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Datas de início e fim são obrigatórias para busca por período.");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim.");
        }
        return pedidoRepository.findByDataPedidoBetween(startDate, endDate);
    }

    public List<Pedido> findByClienteNomeContaining(String nomeCliente) {
        if (!StringUtils.hasText(nomeCliente)) {
            return Collections.emptyList();
        }
        return pedidoRepository.findByClienteNomeContainingIgnoreCase(nomeCliente);
    }

    private void validarPedido(Pedido pedidoEntity) {
        if (pedidoEntity.getDataPedido() == null) {
            throw new IllegalArgumentException("A data do pedido não pode ser nula!");
        }
        if (pedidoEntity.getCliente() == null || pedidoEntity.getCliente().getId() == null) { // Verifica ID
            throw new IllegalArgumentException("O cliente (com ID) do pedido não pode ser nulo!");
        }
        if (CollectionUtils.isEmpty(pedidoEntity.getJoias())) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos uma joia!");
        }
    }

    public FaturamentoDTO calcularFaturamentoTotal() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        BigDecimal faturamentoTotal = BigDecimal.ZERO;
        for (Pedido pedido : pedidos) {
            if (pedido.getJoias() != null) {
                for (Joia joia : pedido.getJoias()) {
                    if (joia.getPreco() != null) {
                        faturamentoTotal = faturamentoTotal.add(joia.getPreco());
                    }
                }
            }
        }
        return new FaturamentoDTO(faturamentoTotal);
    }
}