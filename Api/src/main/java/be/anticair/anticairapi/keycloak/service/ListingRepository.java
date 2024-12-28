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
     * Find antiquities where the state is 0 or 2 and the mailSeller matches the given email
     *
     * @param states The list of states to filter (0, 2)
     * @param mailAntiquarian The email of the seller to filter
     * @return List of matching antiquities
     */
    List<Listing> findByStateInAndMailAntiquarian(List<Integer> states, String mailAntiquarian);


}
