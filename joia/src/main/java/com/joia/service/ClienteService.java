package com.joia.service;

import com.joia.entity.Cliente;
import com.joia.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; // Import para verificar strings

import java.util.Collections; // Import para lista vazia
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public String save(@Valid Cliente clienteEntity) {
        if (!StringUtils.hasText(clienteEntity.getNome())) {
            throw new IllegalArgumentException("O nome do cliente não pode ser vazio.");
        }
        if (!StringUtils.hasText(clienteEntity.getEmail())) {
            throw new IllegalArgumentException("O email do cliente não pode ser vazio.");
        }

        Optional<Cliente> clienteExistenteComEmail = clienteRepository.findByEmail(clienteEntity.getEmail());
        if (clienteExistenteComEmail.isPresent()) {
            throw new RuntimeException("Email '" + clienteEntity.getEmail() + "' já cadastrado!");
        }

        clienteRepository.save(clienteEntity);
        return "Cliente salvo com sucesso!";
    }

    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente ID " + id + " não encontrado!"));
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public String update(@Valid Cliente clienteEntity, Long id) {
        if (!StringUtils.hasText(clienteEntity.getNome())) {
            throw new IllegalArgumentException("O nome do cliente não pode ser vazio.");
        }
        if (!StringUtils.hasText(clienteEntity.getEmail())) {
            throw new IllegalArgumentException("O email do cliente não pode ser vazio.");
        }

        Cliente clienteExistente = findById(id);

        Optional<Cliente> clienteComNovoEmail = clienteRepository.findByEmail(clienteEntity.getEmail());
        if (!clienteExistente.getEmail().equalsIgnoreCase(clienteEntity.getEmail()) &&
                clienteComNovoEmail.isPresent() &&
                !clienteComNovoEmail.get().getId().equals(id)) {
            throw new RuntimeException("Email '" + clienteEntity.getEmail() + "' já cadastrado para outro cliente!");
        }

        clienteExistente.setNome(clienteEntity.getNome());
        clienteExistente.setEmail(clienteEntity.getEmail());
        clienteRepository.save(clienteExistente);
        return "Cliente ID " + id + " atualizado com sucesso!";
    }

    public String delete(Long id) {
        Cliente cliente = findById(id);

        if (cliente.getPedidos() != null && !cliente.getPedidos().isEmpty()) {
            throw new RuntimeException("Não é possível excluir o cliente ID " + id + " pois ele possui " + cliente.getPedidos().size() + " pedido(s) associado(s).");
        }
        clienteRepository.deleteById(id);
        return "Cliente ID " + id + " deletado com sucesso!";
    }


    public Cliente findByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email para busca não pode ser vazio.");
        }
        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente com email '" + email + "' não encontrado!"));
    }

    public List<Cliente> findByNomeContaining(String nome) {
        if (!StringUtils.hasText(nome)) {
            return Collections.emptyList();
        }
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Cliente> findByPedidosIsNotEmpty() {
        return clienteRepository.findByPedidosIsNotEmpty();
    }

    public List<Cliente> findAllByOrderByNomeDesc() {
        return clienteRepository.findAllByOrderByNomeDesc();
    }

    public List<Cliente> findClientesSemPedidos() {
        return clienteRepository.findClientesSemPedidos();
    }
}