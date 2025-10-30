package br.com.gym.management.gymapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record MembroUpdateRequestDTO(
        String nome,

        @Email(message = "Formato de e-mail inválido")
        String email,

        @Past(message = "Data de nascimento deve ser no passado")
        LocalDate dataNascimento
) {
}