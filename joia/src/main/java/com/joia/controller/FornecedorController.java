package com.joia.controller;

import com.joia.entity.Fornecedor;
import com.joia.service.FornecedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importar esta anotação
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
@CrossOrigin(origins = "*")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')") // Somente ADMIN pode criar novos fornecedores
    public String save(@Valid @RequestBody Fornecedor fornecedorEntity) {
        return fornecedorService.save(fornecedorEntity);
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Fornecedor> findById(@PathVariable Long id) {
        Fornecedor fornecedorEntity = fornecedorService.findById(id);
        return ResponseEntity.ok(fornecedorEntity);
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Fornecedor>> findAll() {
        List<Fornecedor> fornecedores = fornecedorService.findAll();
        return ResponseEntity.ok(fornecedores);
    }

    @GetMapping("/findByNomeContaining")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Fornecedor>> findByNomeContaining(@RequestParam String nome) {
        List<Fornecedor> resultado = fornecedorService.findByNomeContaining(nome);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findByJoiasIsNotEmpty")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Fornecedor>> findByJoiasIsNotEmpty() {
        List<Fornecedor> resultado = fornecedorService.findByJoiasIsNotEmpty();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findAllByOrderByNomeAsc")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Fornecedor>> findAllByOrderByNomeAsc() {
        List<Fornecedor> resultado = fornecedorService.findAllByOrderByNomeAsc();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findByJoiasIsEmpty")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Fornecedor>> findByJoiasIsEmpty() {
        List<Fornecedor> resultado = fornecedorService.findByJoiasIsEmpty();
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody Fornecedor fornecedorEntity) {
        String mensagem = fornecedorService.update(fornecedorEntity, id);
        return ResponseEntity.ok(mensagem);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        String mensagem = fornecedorService.delete(id);
        return ResponseEntity.ok(mensagem);
    }
}
