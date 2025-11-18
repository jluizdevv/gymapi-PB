package br.com.gym.management.gymapi.dto;

import java.math.BigDecimal;

public record PlanoDTO(
        Long id,
        String nome,
        BigDecimal preco,
        Integer duracaoEmMeses
) {}