package be.anticair.anticairapi.keycloak.service;

import be.anticair.anticairapi.Class.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {
}
