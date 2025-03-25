package eu.reportincident.incident_service.service;

import eu.reportincident.incident_service.model.dto.Incident;
import eu.reportincident.incident_service.model.request.FilterRequest;
import eu.reportincident.incident_service.model.request.IncidentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IncidentService {

    Page<Incident> findAllApproved(Pageable page);

    Incident findById(long id);

    Incident create(IncidentRequest incidentRequest);

    Page<Incident> filter(Pageable page, FilterRequest filterRequest);
}
