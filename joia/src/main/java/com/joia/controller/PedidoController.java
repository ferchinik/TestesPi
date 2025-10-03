package com.joia.controller;

import com.joia.entity.Pedido;
import com.joia.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String save(@Valid @RequestBody Pedido pedidoEntity) {
        return pedidoService.save(pedidoEntity);
    }

    @GetMapping("/findById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Pedido> findById(@PathVariable Long id) {
        Pedido pedidoEntity = pedidoService.findById(id);
        return ResponseEntity.ok(pedidoEntity);
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Pedido>> findAll() {
        List<Pedido> pedidoEntities = pedidoService.findAll();
        return ResponseEntity.ok(pedidoEntities);
    }

    @GetMapping("/findByClienteNomeContaining")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Pedido>> findByClienteNomeContaining(@RequestParam String nomeCliente) {
        List<Pedido> resultado = pedidoService.findByClienteNomeContaining(nomeCliente);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findByDataPedido")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Pedido>> findByDataPedido(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dataPedido) {
        List<Pedido> resultado = pedidoService.findByDataPedido(dataPedido);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findByDataPedidoBetween")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<Pedido>> findByDataPedidoBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        List<Pedido> resultado = pedidoService.findByDataPedidoBetween(startDate, endDate);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody Pedido pedidoEntity) {
        String mensagem = pedidoService.update(pedidoEntity, id);
        return ResponseEntity.ok(mensagem);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        String mensagem = pedidoService.delete(id);
        return ResponseEntity.ok(mensagem);
    }
}
