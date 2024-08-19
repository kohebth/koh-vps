package koh.service.remote.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface MessageHandler {
    void handle(ConsumerRecord<String, String> rawMessage);
}
