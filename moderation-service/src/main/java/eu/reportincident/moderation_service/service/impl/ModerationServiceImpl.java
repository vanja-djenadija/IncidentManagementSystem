package eu.reportincident.moderation_service.service.impl;

import eu.reportincident.moderation_service.event.IncidentStatusUpdateEvent;
import eu.reportincident.moderation_service.event.RabbitMQProducer;
import eu.reportincident.moderation_service.model.dto.IncidentModeration;
import eu.reportincident.moderation_service.model.entity.IncidentModerationEntity;
import eu.reportincident.moderation_service.model.entity.IncidentStatusHistoryEntity;
import eu.reportincident.moderation_service.model.enums.IncidentStatus;
import eu.reportincident.moderation_service.model.request.IncidentStatusUpdateRequest;
import eu.reportincident.moderation_service.repository.IncidentModerationRepository;
import eu.reportincident.moderation_service.repository.IncidentStatusHistoryRepository;
import eu.reportincident.moderation_service.service.ModerationService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import eu.reportincident.moderation_service.exception.NotFoundException;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
public class ModerationServiceImpl implements ModerationService {

    private final IncidentModerationRepository incidentModerationRepository;
    private final IncidentStatusHistoryRepository incidentStatusHistoryRepository;
    private final ModelMapper modelMapper;
    private final RabbitMQProducer rabbitMQProducer;

    public ModerationServiceImpl(IncidentModerationRepository incidentModerationRepository, IncidentStatusHistoryRepository incidentStatusHistoryRepository, ModelMapper modelMapper, RabbitMQProducer rabbitMQProducer) {
        this.incidentModerationRepository = incidentModerationRepository;
        this.incidentStatusHistoryRepository = incidentStatusHistoryRepository;
        this.modelMapper = modelMapper;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @Override
    public IncidentStatus getIncidentStatus(Long incidentId) {
        return incidentModerationRepository.findByIncidentId(incidentId).orElseThrow(NotFoundException::new).getStatus();
    }

    @Override
    @Transactional
    public IncidentModeration updateIncidentStatus(Long incidentId, IncidentStatus status, Long moderatorId) {

        IncidentModerationEntity incidentModerationEntity = incidentModerationRepository.findById(incidentId)
                .orElseThrow(() -> new NotFoundException("Incident not found with ID: " + incidentId));

        incidentModerationEntity.setStatus(status);
        incidentModerationEntity.setModeratorId(moderatorId);
        incidentModerationEntity.setStatusChangeTime(LocalDateTime.now());
        incidentModerationEntity = incidentModerationRepository.save(incidentModerationEntity);


        IncidentStatusHistoryEntity statusHistory = IncidentStatusHistoryEntity.builder()
                .incidentId(incidentId)
                .status(status)
                .statusChangeTime(LocalDateTime.now())
                .moderatorId(moderatorId)
                .build();
        incidentStatusHistoryRepository.save(statusHistory);

        // Send event to RabbitMQ about incident status update
        IncidentStatusUpdateEvent statusEvent = new IncidentStatusUpdateEvent(
                incidentId,
                status,
                LocalDateTime.now()
        );
        rabbitMQProducer.sendIncidentStatusUpdatedEvent(statusEvent);

        return modelMapper.map(incidentModerationEntity, IncidentModeration.class);
    }


}
