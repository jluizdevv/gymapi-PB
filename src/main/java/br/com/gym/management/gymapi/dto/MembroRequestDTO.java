package br.com.gym.management.gymapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record MembroRequestDTO(
        @NotBlank(message = "Nome não pode ser nulo ou vazio")
        String nome,

        @NotBlank(message = "Email não pode ser nulo ou vazio")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "CPF não pode ser nulo ou vazio")
        @CPF(message = "Formato de CPF inválido")
        String cpf,

        @NotNull(message = "Data de nascimento não pode ser nula")
        @Past(message = "Data de nascimento deve ser no passado")
        LocalDate dataNascimento,

        @NotNull(message = "ID do Plano não pode ser nulo")
        Long planoId
) {
}