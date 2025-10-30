package br.com.gym.management.gymapi.dto;

import java.time.LocalDateTime;

public record RevisaoDTO(
        Integer idRevisao,
        LocalDateTime dataHoraRevisao,
        String tipoRevisao,
        MembroAuditoriaDTO dadosDoMembro
) {
}