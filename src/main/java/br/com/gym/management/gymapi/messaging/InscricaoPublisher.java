package br.com.gym.management.gymapi.messaging;

import br.com.gym.management.gymapi.events.InscricaoCriadaEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class InscricaoPublisher {

    private final RabbitTemplate rabbitTemplate;
    private static final String INSCRICAO_QUEUE = "inscricao.criar";

    public InscricaoPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarEventoInscricaoCriada(InscricaoCriadaEvent event) {
        rabbitTemplate.convertAndSend(INSCRICAO_QUEUE, event);
    }
}