package br.com.gym.management.gymapi.dto;

import java.time.LocalDate;

public record MembroAuditoriaDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        LocalDate dataNascimento
) { }