package br.com.gym.management.gymapi.service;

import br.com.gym.management.gymapi.client.PlanoClient;
import br.com.gym.management.gymapi.domain.Membro;
import br.com.gym.management.gymapi.dto.InscricaoDTO;
import br.com.gym.management.gymapi.dto.MembroAuditoriaDTO;
import br.com.gym.management.gymapi.dto.MembroRequestDTO;
import br.com.gym.management.gymapi.dto.MembroResponseDTO;
import br.com.gym.management.gymapi.dto.MembroUpdateRequestDTO;
import br.com.gym.management.gymapi.dto.PlanoInfoDTO;
import br.com.gym.management.gymapi.dto.RevisaoDTO;
import br.com.gym.management.gymapi.events.InscricaoCriadaEvent;
import br.com.gym.management.gymapi.exception.RecursoNaoEncontradoException;
import br.com.gym.management.gymapi.exception.RegraDeNegocioException;
import br.com.gym.management.gymapi.messaging.InscricaoPublisher;
import br.com.gym.management.gymapi.repository.MembroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MembroService {

    private final MembroRepository membroRepository;
    private final InscricaoPublisher inscricaoPublisher;
    private final PlanoClient planoClient;

    public MembroService(MembroRepository membroRepository,
                         InscricaoPublisher inscricaoPublisher,
                         PlanoClient planoClient) {
        this.membroRepository = membroRepository;
        this.inscricaoPublisher = inscricaoPublisher;
        this.planoClient = planoClient;
    }

    @Transactional
    public MembroResponseDTO cadastrarMembro(MembroRequestDTO requestDTO) {

        validarDuplicidade(requestDTO.email(), requestDTO.cpf());

        Membro novoMembro = new Membro();
        novoMembro.setNome(requestDTO.nome());
        novoMembro.setEmail(requestDTO.email());
        novoMembro.setCpf(requestDTO.cpf());
        novoMembro.setDataNascimento(requestDTO.dataNascimento());

        Membro membroSalvo = membroRepository.save(novoMembro);

        InscricaoCriadaEvent evento = new InscricaoCriadaEvent(
                membroSalvo.getId(),
                requestDTO.planoId(),
                membroSalvo.getDataNascimento(),
                membroSalvo.getEmail(),
                LocalDate.now(),
                membroSalvo.getNome()
        );

        inscricaoPublisher.publicarEventoInscricaoCriada(evento);

        return paraResponseDTO(membroSalvo, null);
    }

    @Transactional(readOnly = true)
    public MembroResponseDTO buscarPorId(Long id) {
        Membro membro = membroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Membro não encontrado com ID: " + id));

        Optional<InscricaoDTO> inscricaoDTOOptional = Optional.empty();
        try {
            inscricaoDTOOptional = planoClient.buscarInscricaoAtivaPorMembroId(id);
        } catch (Exception e) {
            System.err.println("Erro ao buscar inscrição ativa no plan-api: " + e.getMessage());
        }

        InscricaoDTO inscricaoDTO = inscricaoDTOOptional.orElse(null);

        return paraResponseDTO(membro, inscricaoDTO);
    }

    @Transactional
    public MembroResponseDTO atualizarMembro(Long id, MembroUpdateRequestDTO updateDTO) {
        Membro membro = membroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Membro não encontrado com ID: " + id));

        if (updateDTO.email() != null && !updateDTO.email().equalsIgnoreCase(membro.getEmail())) {
            if (membroRepository.findByEmailAndIdNot(updateDTO.email(), id).isPresent()) {
                throw new RegraDeNegocioException("E-mail já cadastrado no sistema por outro usuário.");
            }
            membro.setEmail(updateDTO.email());
        }

        if (updateDTO.nome() != null) {
            membro.setNome(updateDTO.nome());
        }
        if (updateDTO.dataNascimento() != null) {
            membro.setDataNascimento(updateDTO.dataNascimento());
        }

        Membro membroAtualizado = membroRepository.save(membro);

        return paraResponseDTO(membroAtualizado, null);
    }

    @Transactional(readOnly = true)
    public Page<MembroResponseDTO> findAll(Pageable pageable) {
        Page<Membro> pageMembros = membroRepository.findAll(pageable);

        return pageMembros.map(membro -> paraResponseDTO(membro, null));
    }

    @Transactional
    public void deleteMembro(Long id) {
        if (!membroRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Membro não encontrado com ID: " + id);
        }

        membroRepository.deleteById(id);
    }

    private void validarDuplicidade(String email, String cpf) {
        if (membroRepository.findByEmail(email).isPresent()) {
            throw new RegraDeNegocioException("E-mail já cadastrado no sistema.");
        }
        if (membroRepository.findByCpf(cpf).isPresent()) {
            throw new RegraDeNegocioException("CPF já cadastrado no sistema.");
        }
    }

    private MembroResponseDTO paraResponseDTO(Membro membro, InscricaoDTO inscricaoDTO) {
        PlanoInfoDTO planoInfo = null;

        if (inscricaoDTO != null && inscricaoDTO.plano() != null) {
            planoInfo = new PlanoInfoDTO(
                    inscricaoDTO.plano().id(),
                    inscricaoDTO.dataFim(),
                    inscricaoDTO.ativa()
            );
        }

        return new MembroResponseDTO(
                membro.getId(),
                membro.getNome(),
                membro.getEmail(),
                membro.getDataNascimento(),
                planoInfo
        );
    }

    public List<RevisaoDTO> buscarHistoricoPorId(Long id) {
        Revisions<Integer, Membro> revisoes = membroRepository.findRevisions(id);

        return revisoes.getContent().stream()
                .map(this::paraRevisaoDTO)
                .collect(Collectors.toList());
    }

    private RevisaoDTO paraRevisaoDTO(Revision<Integer, Membro> revisao) {
        Integer idRevisao = revisao.getRevisionNumber()
                .orElseThrow(() -> new RegraDeNegocioException("Revisão sem ID."));

        Instant instant = revisao.getRevisionInstant()
                .orElse(Instant.EPOCH);
        LocalDateTime dataHora = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        String tipoRevisao = revisao.getMetadata().getRevisionType().name();

        Membro membroNaRevisao = revisao.getEntity();
        MembroAuditoriaDTO dadosDoMembro = new MembroAuditoriaDTO(
                membroNaRevisao.getId(),
                membroNaRevisao.getNome(),
                membroNaRevisao.getEmail(),
                membroNaRevisao.getCpf(),
                membroNaRevisao.getDataNascimento()
        );

        return new RevisaoDTO(idRevisao, dataHora, tipoRevisao, dadosDoMembro);
    }
}