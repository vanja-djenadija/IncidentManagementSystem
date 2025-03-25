package eu.reportincident.incident_service.repository;

import eu.reportincident.incident_service.model.entity.IncidentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidentEntityRepository extends JpaRepository<IncidentEntity, Long> {
    Page<IncidentEntity> findAllByApproved(boolean b, Pageable page);
    Optional<IncidentEntity> findByIdAndApproved(Long id, boolean approved);
}
