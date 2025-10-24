package br.com.gym.management.gymapi.repository;

import br.com.gym.management.gymapi.domain.Membro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // 1.

@Repository
public interface MembroRepository extends JpaRepository<Membro, Long> {

    // 2. Mágica do Spring Data JPA:
    // Ao declarar um método que começa com 'findBy' e o nome de um atributo
    // da entidade (ex: 'Email'), o Spring cria a consulta SQL automaticamente.
    Optional<Membro> findByEmail(String email);

    // O mesmo vale aqui para 'Cpf'.
    Optional<Membro> findByCpf(String cpf);
}