package com.joia.service;

import com.joia.entity.Categoria;
import com.joia.repository.CategoriaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; // Import para verificar strings

import java.util.Collections; // Import para lista vazia
import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public String save(@Valid Categoria categoriaEntity) {
        if (!StringUtils.hasText(categoriaEntity.getNome())) {
            throw new IllegalArgumentException("O nome da categoria não pode ser vazio.");
        }
        categoriaRepository.save(categoriaEntity);
        return "Categoria salva com sucesso!";
    }


    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    public String update(@Valid Categoria categoriaEntity, Long id) {
        if (!StringUtils.hasText(categoriaEntity.getNome())) {
            throw new IllegalArgumentException("O nome da categoria não pode ser vazio.");
        }
        Categoria categoriaEntityExistente = findById(id);
        categoriaEntityExistente.setNome(categoriaEntity.getNome());
        categoriaRepository.save(categoriaEntityExistente);
        return "Categoria ID " + id + " atualizada com sucesso!";
    }

    public String delete(Long id) {
        Categoria categoria = findById(id);

        if (categoria.getJoias() != null && !categoria.getJoias().isEmpty()) {
            throw new RuntimeException("Não é possível excluir a categoria ID " + id + " pois ela está associada a " + categoria.getJoias().size() + " joia(s).");
        }
        categoriaRepository.deleteById(id);
        return "Categoria ID " + id + " deletada com sucesso!";
    }

    public Categoria findById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria ID " + id + " não encontrada!"));
    }
    
    public List<Categoria> findByNomeContaining(String nome) {
        if (!StringUtils.hasText(nome)) {
            return Collections.emptyList();
        }
        return categoriaRepository.findByNomeContainingIgnoreCase(nome);
    }


    public List<Categoria> findAllByOrderByNomeAsc() {
        return categoriaRepository.findAllByOrderByNomeAsc();
    }

}