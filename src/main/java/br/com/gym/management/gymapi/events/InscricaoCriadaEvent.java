package br.com.gym.management.gymapi.events;

import java.time.LocalDate;

public record InscricaoCriadaEvent(
        Long membroId,
        Long planoId,
        LocalDate dataNascimento,
        String email,
        LocalDate dataInicio,
        String nomeCompleto
) {}