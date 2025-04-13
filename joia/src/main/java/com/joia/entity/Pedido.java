package com.joia.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data do pedido não pode ser nula.")
    @Column(nullable = false)
    private Date dataPedido;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties("pedidos")
    private Cliente cliente;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "pedido_joia",
            joinColumns = @JoinColumn(name = "pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "joia_id")
    )
    @JsonIgnoreProperties("pedidos")
    private List<Joia> joias;
}
