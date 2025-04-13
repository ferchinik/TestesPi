package com.joia.controller;

import com.joia.entity.Categoria;
import com.joia.service.CategoriaService; // Certifique-se que o Service tem os métodos
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public String save(@Valid @RequestBody Categoria categoriaEntity) {
        return categoriaService.save(categoriaEntity);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Categoria> findById(@PathVariable Long id) {
        Categoria categoriaEntity = categoriaService.findById(id);
        return ResponseEntity.ok(categoriaEntity);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Categoria>> findAll() {
        List<Categoria> categoriaEntities = categoriaService.findAll();
        return ResponseEntity.ok(categoriaEntities);
    }

    @GetMapping("/findByNomeContaining")
    public ResponseEntity<List<Categoria>> findByNomeContaining(@RequestParam String nome) {

        List<Categoria> resultado = categoriaService.findByNomeContaining(nome);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findAllByOrderByNomeAsc")
    public ResponseEntity<List<Categoria>> findAllByOrderByNomeAsc() {

        List<Categoria> resultado = categoriaService.findAllByOrderByNomeAsc();
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody Categoria categoriaEntity) {
        String mensagem = categoriaService.update(categoriaEntity, id);
        return ResponseEntity.ok(mensagem);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        String mensagem = categoriaService.delete(id);
        return ResponseEntity.ok(mensagem);
    }
}