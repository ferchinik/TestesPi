package com.joia.service;

import com.joia.entity.Fornecedor;
import com.joia.repository.FornecedorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; // Import para verificar strings

import java.util.Collections; // Import para lista vazia
import java.util.List;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    public String save(@Valid Fornecedor fornecedorEntity) {
        if (!StringUtils.hasText(fornecedorEntity.getNome())) {
            throw new IllegalArgumentException("O nome do fornecedor não pode ser vazio.");
        }
        fornecedorRepository.save(fornecedorEntity);
        return "Fornecedor salvo com sucesso!";
    }

    public Fornecedor findById(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor ID " + id + " não encontrado!"));
    }

    public List<Fornecedor> findAll() {
        return fornecedorRepository.findAll();
    }

    public String update(@Valid Fornecedor fornecedorEntity, Long id) {
        if (!StringUtils.hasText(fornecedorEntity.getNome())) {
            throw new IllegalArgumentException("O nome do fornecedor não pode ser vazio.");
        }
        Fornecedor fornecedorExistente = findById(id);

        fornecedorExistente.setNome(fornecedorEntity.getNome());
        fornecedorRepository.save(fornecedorExistente);
        return "Fornecedor ID " + id + " atualizado com sucesso!";
    }

    public String delete(Long id) {
        Fornecedor fornecedor = findById(id);

        if (fornecedor.getJoias() != null && !fornecedor.getJoias().isEmpty()) {
            throw new RuntimeException("Não é possível excluir o fornecedor ID " + id + " pois ele está associado a " + fornecedor.getJoias().size() + " joia(s).");
        }
        fornecedorRepository.deleteById(id);
        return "Fornecedor ID " + id + " deletado com sucesso!";
    }

    public List<Fornecedor> findByNomeContaining(String nome) {
        if (!StringUtils.hasText(nome)) {
            return Collections.emptyList();
        }
        return fornecedorRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Fornecedor> findByJoiasIsNotEmpty() {
        return fornecedorRepository.findByJoiasIsNotEmpty();
    }

    public List<Fornecedor> findAllByOrderByNomeAsc() {
        return fornecedorRepository.findAllByOrderByNomeAsc();
    }

    public List<Fornecedor> findByJoiasIsEmpty() {
        return fornecedorRepository.findByJoiasIsEmpty();
    }
}