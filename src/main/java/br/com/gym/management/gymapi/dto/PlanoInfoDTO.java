package br.com.gym.management.gymapi.dto;

import java.time.LocalDate;


public record PlanoInfoDTO(
        Long planoId,
        LocalDate dataFimInscricao,
        Boolean inscricaoAtiva
) {}