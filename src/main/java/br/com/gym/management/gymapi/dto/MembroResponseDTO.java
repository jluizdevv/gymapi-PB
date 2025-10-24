package br.com.gym.management.gymapi.dto;

import java.time.LocalDate;



public record MembroResponseDTO(
        Long id,
        String nome,
        String email,
        LocalDate dataNascimento,
        PlanoInfoDTO plano // 3. Este campo continua aqui
) {
    // Construtor auxiliar (continua igual)
    public MembroResponseDTO(Long id, String nome, String email, LocalDate dataNascimento) {
        this(id, nome, email, dataNascimento, null);
    }
}