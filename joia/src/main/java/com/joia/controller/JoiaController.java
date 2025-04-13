package com.joia.controller;

import com.joia.entity.Joia;
import com.joia.service.JoiaService; // Certifique-se que o Service tem os métodos
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal; // Import necessário para o filtro de preço
import java.util.List;

@RestController
@RequestMapping("/api/joias")
@CrossOrigin(origins = "*")
public class JoiaController {

    @Autowired
    private JoiaService joiaService;

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public String save(@Valid @RequestBody Joia joiaEntity) {
        return joiaService.save(joiaEntity);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Joia> findById(@PathVariable Long id) {
        Joia joiaEntity = joiaService.findById(id);
        return ResponseEntity.ok(joiaEntity);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Joia>> findAll() {
        List<Joia> joiaEntities = joiaService.findAll();
        return ResponseEntity.ok(joiaEntities);
    }

    @GetMapping("/findAllByOrderByNomeAsc")
    public ResponseEntity<List<Joia>> findAllByOrderByNomeAsc() {
        List<Joia> resultado = joiaService.findAllByOrderByNomeAsc(); // << IMPLEMENTADO
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<Joia>> filtrarJoias(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) BigDecimal precoMin
    ) {
        List<Joia> resultado = joiaService.filtrar(nome, categoriaId, fornecedorId, precoMin);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody Joia joiaEntity) {
        String mensagem = joiaService.update(joiaEntity, id);
        return ResponseEntity.ok(mensagem);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        String mensagem = joiaService.delete(id);
        return ResponseEntity.ok(mensagem);
    }
}