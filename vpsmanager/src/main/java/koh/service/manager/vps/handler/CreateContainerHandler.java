package koh.service.manager.vps.handler;

import koh.db.hub.repository.ContainerRepository;
import koh.db.hub.vps_management.tables.records.DockerContainerRecord;
import koh.service.manager.vps.kafka.KafkaProducerWorker;
import koh.service.manager.vps.message.CreateContainerMessage;
import koh.service.manager.vps.message.StatusMessage;
import koh.service.manager.vps.tools.JsonTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import static koh.service.manager.vps.kafka.KafkaRespTopic.TOPIC_VPS_CREATE_CONTAINER_RESPONSE;

@Slf4j
public class CreateContainerHandler extends AbstractRemoteHandler {
    ContainerRepository containerRepository = new ContainerRepository();

    public CreateContainerHandler(KafkaProducerWorker bus) {
        super(bus);
    }

    @Override
    public void handle(ConsumerRecord<String, String> rawMessage)
            throws Exception {

        CreateContainerMessage message = JsonTools.fromJson(rawMessage.value());

        DockerContainerRecord container = containerRepository.createContainer(message.getUserId(), message.getName(),
                message.getMemory());

        if (container.insert() == 1) {
            bus.respond(TOPIC_VPS_CREATE_CONTAINER_RESPONSE, rawMessage.key(), new StatusMessage(container.toString()));
        } else {
            bus.respond(TOPIC_VPS_CREATE_CONTAINER_RESPONSE, rawMessage.key(), new StatusMessage("Failure"));
        }
    }
}
