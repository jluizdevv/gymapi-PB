package br.com.gym.management.gymapi.dto;

import java.time.LocalDate;


public record InscricaoDTO(
        Long id,
        Long membroId,
        PlanoDTO plano,
        LocalDate dataInicio,
        LocalDate dataFim,
        Boolean ativa
) {
}