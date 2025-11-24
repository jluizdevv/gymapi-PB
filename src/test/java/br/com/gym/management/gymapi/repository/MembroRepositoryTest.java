package br.com.gym.management.gymapi.repository;

import br.com.gym.management.gymapi.client.PlanoClient;
import br.com.gym.management.gymapi.domain.Membro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MembroRepositoryTest {

    @MockitoBean
    private PlanoClient planoClient;


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MembroRepository membroRepository;

    @Test
    @DisplayName("Deve retornar um Membro quando buscar por um email existente")
    void quandoFindByEmail_deveRetornarMembro() {
        Membro membro = new Membro(null, "Membro Teste", "teste@email.com", "12345678901", LocalDate.now());
        entityManager.persistAndFlush(membro);

        Optional<Membro> membroEncontrado = membroRepository.findByEmail("teste@email.com");

        assertThat(membroEncontrado).isPresent();
        assertThat(membroEncontrado.get().getNome()).isEqualTo(membro.getNome());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando buscar por um CPF inexistente")
    void quandoFindByCpfInexistente_deveRetornarOptionalVazio() {
        Optional<Membro> membroEncontrado = membroRepository.findByCpf("00000000000");

        assertThat(membroEncontrado).isNotPresent();
    }
}