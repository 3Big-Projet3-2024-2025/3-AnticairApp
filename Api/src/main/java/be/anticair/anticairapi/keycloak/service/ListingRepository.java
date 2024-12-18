package be.anticair.anticairapi.keycloak.service;

import be.anticair.anticairapi.Class.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    /**
     * Declaration to get all the antiquity (not checked) that an antiquarian is the checker
     * @param email the email of the antiquarian
     * @return a list of all the antiquity
     */
    @Query("SELECT a FROM Listing a WHERE a.mailAntiquarian = :email AND a.state = 0")
    List<Listing> getAllAntiquityNotCheckedFromAnAntiquarian(@Param("email") String email);
}
