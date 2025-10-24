package br.com.gym.management.gymapi.controller;

import br.com.gym.management.gymapi.dto.MembroRequestDTO;
import br.com.gym.management.gymapi.dto.MembroResponseDTO;
import br.com.gym.management.gymapi.service.MembroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/membros")
public class MembroController {

    private final MembroService membroService;

    public MembroController(MembroService membroService) {
        this.membroService = membroService;
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


    @GetMapping("/{id}") //
    public ResponseEntity<MembroResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        MembroResponseDTO membro = membroService.buscarPorId(id);
        return ResponseEntity.ok(membro);
    }
}