package br.com.gym.management.gymapi.controller;

import br.com.gym.management.gymapi.dto.MembroRequestDTO;
import br.com.gym.management.gymapi.dto.MembroResponseDTO;
import br.com.gym.management.gymapi.dto.MembroUpdateRequestDTO;
import br.com.gym.management.gymapi.dto.RevisaoDTO;
import br.com.gym.management.gymapi.service.MembroService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/membros")
public class MembroController {

    private final MembroService membroService;

    public MembroController(MembroService membroService) {
        this.membroService = membroService;
    }

    @GetMapping
    public ResponseEntity<Page<MembroResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<MembroResponseDTO> page = membroService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    public ResponseEntity<MembroResponseDTO> cadastrar(
            @Valid @RequestBody MembroRequestDTO requestDTO
    ) {
        MembroResponseDTO membroSalvo = membroService.cadastrarMembro(requestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(membroSalvo.id())
                .toUri();

        return ResponseEntity.created(location).body(membroSalvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembroResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        MembroResponseDTO membro = membroService.buscarPorId(id);
        return ResponseEntity.ok(membro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MembroResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MembroUpdateRequestDTO updateDTO) {

        MembroResponseDTO membroAtualizado = membroService.atualizarMembro(id, updateDTO);
        return ResponseEntity.ok(membroAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        membroService.deleteMembro(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<List<RevisaoDTO>> buscarHistorico(@PathVariable Long id) {
        List<RevisaoDTO> historico = membroService.buscarHistoricoPorId(id);
        return ResponseEntity.ok(historico);
    }
}