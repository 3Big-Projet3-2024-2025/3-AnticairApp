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

    /**
     * Declaration to get all the antiquity (checked)
     *
     * @return a list of all the antiquity checked (state = 1)
     * @Author Blommaert Youry
     */
    @Query("SELECT a FROM Listing a WHERE a.state = 1")
    List<Listing> getAllAntiquityChecked();

    /**
     * Finds antiquities where the state matches one of the provided states (0 or 2)
     * and the seller's email matches the specified email.
     *
     * <p>This method queries the database for all antiquities that meet the
     * filtering criteria: the state must be either 0 or 2, and the email of the
     * seller must match the provided email address.</p>
     *
     * @param states a list of integers representing the states to filter (e.g., 0, 2)
     * @param mailAntiquarian the email address of the antiquarian to filter by
     * @return a list of {@link Listing} objects that match the specified criteria
     *
     * @author Neve Thierry
     */
    List<Listing> findByStateInAndMailAntiquarian(List<Integer> states, String mailAntiquarian);


}
