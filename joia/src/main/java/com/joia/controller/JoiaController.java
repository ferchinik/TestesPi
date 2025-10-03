package com.joia.controller;

import com.joia.entity.Joia;
import com.joia.service.JoiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importar
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/joias")
@CrossOrigin(origins = "*")
public class JoiaController {

    @Autowired
    private JoiaService joiaService;

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String save(@Valid @RequestBody Joia joiaEntity) {
        return joiaService.save(joiaEntity);
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Joia> findById(@PathVariable Long id) {
        Joia joiaEntity = joiaService.findById(id);
        return ResponseEntity.ok(joiaEntity);
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Joia>> findAll() {
        List<Joia> joiaEntities = joiaService.findAll();
        return ResponseEntity.ok(joiaEntities);
    }

    @GetMapping("/findAllByOrderByNomeAsc")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Joia>> findAllByOrderByNomeAsc() {
        List<Joia> resultado = joiaService.findAllByOrderByNomeAsc();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/filtrar")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
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
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody Joia joiaEntity) {
        String mensagem = joiaService.update(joiaEntity, id);
        return ResponseEntity.ok(mensagem);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        String mensagem = joiaService.delete(id);
        return ResponseEntity.ok(mensagem);
    }
}
