package eu.reportincident.incident_service.controller;

import eu.reportincident.incident_service.model.dto.Incident;
import eu.reportincident.incident_service.model.request.FilterRequest;
import eu.reportincident.incident_service.model.request.IncidentRequest;
import eu.reportincident.incident_service.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    @Autowired
    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    public Page<Incident> findByApproved(Pageable page) {
        return incidentService.findAllApproved(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> findById(@PathVariable long id) {
        Optional<Incident> incident = Optional.ofNullable(incidentService.findById(id));
        // return 404 if not found
        return incident.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping
    public ResponseEntity<Incident> create(@RequestBody @Valid IncidentRequest incidentRequest) {
        Incident incident = incidentService.create(incidentRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(incident.getId())
                .toUri();

        return ResponseEntity.created(location).body(incident);
    }

    @PostMapping("/filter")
    public Page<Incident> findByFilter(Pageable page, @RequestBody @Valid FilterRequest filterRequest) {
        return incidentService.filter(page, filterRequest);
    }
}
