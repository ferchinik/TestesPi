package com.joia.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class FaturamentoDTO {
    private BigDecimal valorTotal;

    public FaturamentoDTO(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public FaturamentoDTO() {
    }
}