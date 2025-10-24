package br.com.gym.management.gymapi.dto;

import java.time.LocalDate;


public record PlanoInfoDTO(
        String nome,
        LocalDate dataFimInscricao,
        Boolean inscricaoAtiva
) {}