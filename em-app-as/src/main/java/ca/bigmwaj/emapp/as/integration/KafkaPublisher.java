package ca.bigmwaj.emapp.as.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, Object payload) {
        kafkaTemplate.send(topic, payload);
    }
}
