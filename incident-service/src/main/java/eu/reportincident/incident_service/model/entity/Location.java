package eu.reportincident.incident_service.model.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    private double latitude;
    private double longitude;
    private double radius;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipcode;
}
