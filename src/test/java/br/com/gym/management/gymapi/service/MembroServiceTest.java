package br.com.gym.management.gymapi.service;

import br.com.gym.management.gymapi.client.PlanoClient;
import br.com.gym.management.gymapi.domain.Membro;
import br.com.gym.management.gymapi.dto.MembroRequestDTO;
import br.com.gym.management.gymapi.events.InscricaoCriadaEvent;
import br.com.gym.management.gymapi.exception.RegraDeNegocioException;
import br.com.gym.management.gymapi.repository.MembroRepository;
import br.com.gym.management.gymapi.messaging.InscricaoPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembroServiceTest {

    @Mock
    private MembroRepository membroRepository;

    @Mock
    private PlanoClient planoClient;


    @Mock
    private InscricaoPublisher inscricaoPublisher;

    @InjectMocks
    private MembroService membroService;


    private final MembroRequestDTO requestDTO = new MembroRequestDTO(
            "Membro Teste", "mock@test.com", "99988877766", LocalDate.now().minusYears(20), 1L);


    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar e-mail duplicado")
    void quandoEmailDuplicado_deveLancarRegraDeNegocioException() {
        
        when(membroRepository.findByEmail(anyString())).thenReturn(Optional.of(new Membro()));


        assertThrows(RegraDeNegocioException.class, () -> {
            membroService.cadastrarMembro(requestDTO);
        });


        verify(membroRepository, never()).save(any(Membro.class));
        verify(inscricaoPublisher, never()).publicarEventoInscricaoCriada(any(InscricaoCriadaEvent.class));
    }


    @Test
    @DisplayName("Deve salvar Membro e Publicar o Evento para processamento assíncrono")
    void quandoDadosValidos_deveSalvarMembroEPublicarEvento() {
        Membro membroSalvo = new Membro(1L, requestDTO.nome(), requestDTO.email(), requestDTO.cpf(), requestDTO.dataNascimento());

        when(membroRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(membroRepository.findByCpf(anyString())).thenReturn(Optional.empty());

        when(membroRepository.save(any(Membro.class))).thenReturn(membroSalvo);

        membroService.cadastrarMembro(requestDTO);

        verify(membroRepository, times(1)).save(any(Membro.class));

        verify(inscricaoPublisher, times(1)).publicarEventoInscricaoCriada(any(InscricaoCriadaEvent.class));

        verify(planoClient, never()).buscarPlanoPorId(anyLong());

    }

}