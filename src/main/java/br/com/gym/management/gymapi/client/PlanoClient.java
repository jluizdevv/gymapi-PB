package br.com.gym.management.gymapi.client;

import br.com.gym.management.gymapi.dto.InscricaoDTO;
import br.com.gym.management.gymapi.dto.PlanoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient("plan-api")
public interface PlanoClient {

    @GetMapping("/api/planos/{id}")
    PlanoDTO buscarPlanoPorId(@PathVariable("id") Long id);

    @GetMapping("/api/planos/inscricoes/membro/{membroId}")
    Optional<InscricaoDTO> buscarInscricaoAtivaPorMembroId(@PathVariable("membroId") Long membroId);
}