package br.com.gym.management.gymapi.service;

import br.com.gym.management.gymapi.client.PlanoClient;
import br.com.gym.management.gymapi.domain.Inscricao;
import br.com.gym.management.gymapi.domain.Membro;
import br.com.gym.management.gymapi.dto.MembroRequestDTO;
import br.com.gym.management.gymapi.dto.PlanoDTO;
import br.com.gym.management.gymapi.exception.RecursoNaoEncontradoException;
import br.com.gym.management.gymapi.repository.InscricaoRepository;
import br.com.gym.management.gymapi.repository.MembroRepository;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembroServiceTest {

    @Mock
    private MembroRepository membroRepository;
    @Mock
    private InscricaoRepository inscricaoRepository;
    @Mock
    private PlanoClient planoClient;

    @InjectMocks
    private MembroService membroService;


    private final PlanoDTO planoTeste = new PlanoDTO(1L, "Plano Mock", new BigDecimal("100"), 3);
    private final MembroRequestDTO requestDTO = new MembroRequestDTO(
            "Membro Teste", "mock@test.com", "99988877766", LocalDate.now().minusYears(20), 1L);

    @Test
    @DisplayName("Deve lançar exceção se o Plano não for encontrado no microsserviço")
    void quandoPlanoNaoExiste_deveLancarRecursoNaoEncontrado() {

        when(planoClient.buscarPlanoPorId(anyLong())).thenThrow(FeignException.NotFound.class);

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            membroService.cadastrarMembro(requestDTO);
        });

        verify(membroRepository, never()).save(any(Membro.class));
    }

    @Test
    @DisplayName("Deve cadastrar Membro e Inscrição quando o Plano for encontrado")
    void quandoPlanoExiste_deveSalvarMembroEInscricao() {

        Membro membroMock = new Membro();
        when(membroRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(membroRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(planoClient.buscarPlanoPorId(anyLong())).thenReturn(planoTeste);
        when(membroRepository.save(any(Membro.class))).thenReturn(membroMock);


        membroService.cadastrarMembro(requestDTO);

        verify(membroRepository, times(1)).save(any(Membro.class));

        verify(inscricaoRepository, times(1)).save(any(Inscricao.class));
    }
}