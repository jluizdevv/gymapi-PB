package br.com.gym.management.gymapi.service;

import br.com.gym.management.gymapi.domain.Inscricao;
import br.com.gym.management.gymapi.domain.Membro;
import br.com.gym.management.gymapi.domain.Plano;
import br.com.gym.management.gymapi.dto.MembroRequestDTO;
import br.com.gym.management.gymapi.dto.MembroResponseDTO;
import br.com.gym.management.gymapi.dto.PlanoInfoDTO;
import br.com.gym.management.gymapi.exception.RecursoNaoEncontradoException;
import br.com.gym.management.gymapi.exception.RegraDeNegocioException;
import br.com.gym.management.gymapi.repository.InscricaoRepository;
import br.com.gym.management.gymapi.repository.MembroRepository;
import br.com.gym.management.gymapi.repository.PlanoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class MembroService {

    private final MembroRepository membroRepository;
    private final PlanoRepository planoRepository;
    private final InscricaoRepository inscricaoRepository;

    public MembroService(MembroRepository membroRepository,
                         PlanoRepository planoRepository,
                         InscricaoRepository inscricaoRepository) {
        this.membroRepository = membroRepository;
        this.planoRepository = planoRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    @Transactional
    public MembroResponseDTO cadastrarMembro(MembroRequestDTO requestDTO) {


        validarDuplicidade(requestDTO.email(), requestDTO.cpf());

        Plano plano = planoRepository.findById(requestDTO.planoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano não encontrado com ID: " + requestDTO.planoId()));

        Membro novoMembro = new Membro();
        novoMembro.setNome(requestDTO.nome());
        novoMembro.setEmail(requestDTO.email());
        novoMembro.setCpf(requestDTO.cpf());
        novoMembro.setDataNascimento(requestDTO.dataNascimento());

        Membro membroSalvo = membroRepository.save(novoMembro);

        Inscricao novaInscricao = new Inscricao();
        novaInscricao.setMembro(membroSalvo);
        novaInscricao.setPlano(plano);
        novaInscricao.setDataInicio(LocalDate.now());

        LocalDate dataFim = LocalDate.now().plusMonths(plano.getDuracaoEmMeses());
        novaInscricao.setDataFim(dataFim);
        novaInscricao.setAtiva(true);

        Inscricao inscricaoSalva = inscricaoRepository.save(novaInscricao);

        return paraResponseDTO(membroSalvo, inscricaoSalva);
    }

    @Transactional(readOnly = true)
    public MembroResponseDTO buscarPorId(Long id) {
        Membro membro = membroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Membro não encontrado com ID: " + id));

        Inscricao inscricao = inscricaoRepository.findByMembroIdAndAtivaTrue(id).orElse(null);

        return paraResponseDTO(membro, inscricao);
    }

    private void validarDuplicidade(String email, String cpf) {
        if (membroRepository.findByEmail(email).isPresent()) {
            throw new RegraDeNegocioException("E-mail já cadastrado no sistema.");
        }
        if (membroRepository.findByCpf(cpf).isPresent()) {
            throw new RegraDeNegocioException("CPF já cadastrado no sistema.");
        }
    }

    private MembroResponseDTO paraResponseDTO(Membro membro, Inscricao inscricao) {
        PlanoInfoDTO planoInfo = null;
        if (inscricao != null) {
            planoInfo = new PlanoInfoDTO(
                    inscricao.getPlano().getNome(),
                    inscricao.getDataFim(),
                    inscricao.getAtiva()
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
}