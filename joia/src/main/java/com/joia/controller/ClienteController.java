package com.joia.controller;

import com.joia.entity.Cliente;
import com.joia.service.ClienteService; // Certifique-se que o Service tem os métodos
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public String save(@Valid @RequestBody Cliente clienteEntity) {
        return clienteService.save(clienteEntity);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Cliente> findById(@PathVariable Long id) {
        Cliente clienteEntity = clienteService.findById(id);
        return ResponseEntity.ok(clienteEntity);
    }

    @GetMapping("/findByEmail")
    public ResponseEntity<Cliente> findByEmail(@RequestParam String email) {
        Cliente cliente = clienteService.findByEmail(email);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Cliente>> findAll() {
        List<Cliente> clienteEntities = clienteService.findAll();
        return ResponseEntity.ok(clienteEntities);
    }

    @GetMapping("/findByNomeContaining")
    public ResponseEntity<List<Cliente>> findByNomeContaining(@RequestParam String nome) {
        List<Cliente> resultado = clienteService.findByNomeContaining(nome);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findByPedidosIsNotEmpty")
    public ResponseEntity<List<Cliente>> findByPedidosIsNotEmpty() {
        List<Cliente> resultado = clienteService.findByPedidosIsNotEmpty();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findAllByOrderByNomeDesc")
    public ResponseEntity<List<Cliente>> findAllByOrderByNomeDesc() {
        List<Cliente> resultado = clienteService.findAllByOrderByNomeDesc();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findClientesSemPedidos")
    public ResponseEntity<List<Cliente>> findClientesSemPedidos() {
        List<Cliente> resultado = clienteService.findClientesSemPedidos();
        return ResponseEntity.ok(resultado);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody Cliente clienteEntity) {
        String mensagem = clienteService.update(clienteEntity, id);
        return ResponseEntity.ok(mensagem);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        String mensagem = clienteService.delete(id);
        return ResponseEntity.ok(mensagem);
    }
}