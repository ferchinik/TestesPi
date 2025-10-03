package com.joia.service;

import com.joia.entity.Joia;
import com.joia.repository.JoiaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class JoiaService {

    @Autowired
    private JoiaRepository joiaRepository;

    public String save(@Valid Joia joiaEntity) {
        validarJoia(joiaEntity);
        joiaRepository.save(joiaEntity);
        return "Joia salva com sucesso!";
    }

    public Joia findById(Long id) {
        return joiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Joia ID " + id + " não encontrada!"));
    }

    public List<Joia> findAll() {
        return joiaRepository.findAll();
    }

    public String update(@Valid Joia joiaEntity, Long id) {
        Joia joiaExistente = findById(id);

        validarJoia(joiaEntity);

        joiaExistente.setNome(joiaEntity.getNome());
        joiaExistente.setPreco(joiaEntity.getPreco());
        joiaExistente.setCategoria(joiaEntity.getCategoria());
        joiaExistente.setFornecedor(joiaEntity.getFornecedor());


        joiaRepository.save(joiaExistente);
        return "Joia ID " + id + " atualizada com sucesso!";
    }

    public String delete(Long id) {
        Joia joia = findById(id);

        if (joia.getPedidos() != null && !joia.getPedidos().isEmpty()) {
            throw new RuntimeException("Não é possível excluir a joia ID " + id + " pois ela está associada a " + joia.getPedidos().size() + " pedido(s).");
        }
        joiaRepository.deleteById(id);
        return "Joia ID " + id + " deletada com sucesso!";
    }

    public List<Joia> findAllByOrderByNomeAsc() {
        return joiaRepository.findAllByOrderByNomeAsc();
    }

    public List<Joia> filtrar(String nome, Long categoriaId, Long fornecedorId, BigDecimal precoMin) {
        // Validações básicas opcionais (ou deixar a query tratar nulls)
        String nomeParam = StringUtils.hasText(nome) ? nome : null;
        Long catIdParam = (categoriaId != null && categoriaId > 0) ? categoriaId : null;
        Long fornIdParam = (fornecedorId != null && fornecedorId > 0) ? fornecedorId : null;
        BigDecimal precoParam = (precoMin != null && precoMin.compareTo(BigDecimal.ZERO) >= 0) ? precoMin : null;

        // Chama diretamente o método da query personalizada
        return joiaRepository.filtrar(nomeParam, catIdParam, fornIdParam, precoParam);
    }

    private void validarJoia(Joia joiaEntity) {
        if (!StringUtils.hasText(joiaEntity.getNome())) {
            throw new IllegalArgumentException("O nome da joia não pode ser vazio!");
        }
        if (joiaEntity.getPreco() == null || joiaEntity.getPreco().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço da joia não pode ser nulo ou negativo!");
        }
        if (joiaEntity.getCategoria() == null || joiaEntity.getCategoria().getId() == null) { // Verifica ID
            throw new IllegalArgumentException("A categoria da joia (com ID) não pode ser nula!");
        }
        if (joiaEntity.getFornecedor() == null || joiaEntity.getFornecedor().getId() == null) { // Verifica ID
            throw new IllegalArgumentException("O fornecedor da joia (com ID) não pode ser nulo!");
        }
    }
}