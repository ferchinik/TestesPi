package com.joia.controller;

import com.joia.entity.Cliente;
import com.joia.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String save(@Valid @RequestBody Cliente clienteEntity) {
        return clienteService.save(clienteEntity);
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Cliente> findById(@PathVariable Long id) {
        Cliente clienteEntity = clienteService.findById(id);
        return ResponseEntity.ok(clienteEntity);
    }

    @GetMapping("/findByEmail")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Cliente> findByEmail(@RequestParam String email) {
        Cliente cliente = clienteService.findByEmail(email);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Cliente>> findAll() {
        List<Cliente> clienteEntities = clienteService.findAll();
        return ResponseEntity.ok(clienteEntities);
    }

    @GetMapping("/findByNomeContaining")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Cliente>> findByNomeContaining(@RequestParam String nome) {
        List<Cliente> resultado = clienteService.findByNomeContaining(nome);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findByPedidosIsNotEmpty")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Cliente>> findByPedidosIsNotEmpty() {
        List<Cliente> resultado = clienteService.findByPedidosIsNotEmpty();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findAllByOrderByNomeDesc")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Cliente>> findAllByOrderByNomeDesc() {
        List<Cliente> resultado = clienteService.findAllByOrderByNomeDesc();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findClientesSemPedidos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Cliente>> findClientesSemPedidos() {
        List<Cliente> resultado = clienteService.findClientesSemPedidos();
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody Cliente clienteEntity) {
        String mensagem = clienteService.update(clienteEntity, id);
        return ResponseEntity.ok(mensagem);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        String mensagem = clienteService.delete(id);
        return ResponseEntity.ok(mensagem);
    }
}
