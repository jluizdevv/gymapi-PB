package br.com.gym.management.gymapi.repository;

import br.com.gym.management.gymapi.domain.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {
    Optional<Inscricao> findByMembroIdAndAtivaTrue(Long membroId);

}