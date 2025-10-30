package br.com.gym.management.gymapi.dto;

import java.time.LocalDate;



public record MembroResponseDTO(
        Long id,
        String nome,
        String email,
        LocalDate dataNascimento,
        PlanoInfoDTO plano
) {

    public MembroResponseDTO(Long id, String nome, String email, LocalDate dataNascimento) {
        this(id, nome, email, dataNascimento, null);
    }
}