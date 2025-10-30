package br.com.gym.management.gymapi.repository;

import br.com.gym.management.gymapi.domain.Membro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembroRepository extends JpaRepository<Membro, Long>,
        RevisionRepository<Membro, Long, Integer> {


    Optional<Membro> findByEmail(String email);

    Optional<Membro> findByCpf(String cpf);

    Optional<Membro> findByEmailAndIdNot(String email, Long id);


}