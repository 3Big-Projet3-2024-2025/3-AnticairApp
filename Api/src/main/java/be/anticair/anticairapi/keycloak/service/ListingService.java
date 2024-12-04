package be.anticair.anticairapi.keycloak.service;

import be.anticair.anticairapi.Class.PhotoAntiquity;
import jakarta.transaction.Transactional;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import be.anticair.anticairapi.Class.Listing;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for performing listing-related operations.
 *
 * @Author Blommaert Youry, Neve Thierry
 */

@Service
public class ListingService {

    public ListingService() {
    }

    @Autowired
    private ListingRepository ListingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoAntiquityService photoAntiquityService;

    public Optional<Listing> getAntiquityById(Long id) {
        return ListingRepository.findById(id);
    }

    public Listing createListing(String email, Listing newListing) {
        UserRepresentation user = userService.getUsersByEmail(email).get(0);

        if(user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        newListing.setMailMember(email);
        newListing.setState(0);  // Initialized to 0 (not yet verified)
        newListing.setEstAffiche(false);  // Initialized to false (not yet displayed)

        // Verify that the required fields are present
        if (newListing.getPriceAntiquity() == null ||
                newListing.getDescriptionAntiquity() == null ||
                newListing.getTitleAntiquity() == null) {
            throw new IllegalArgumentException("Price, description, and title are required");
        }

        // Save the new listing
        return ListingRepository.save(newListing);
    }

    public Listing updateListing(Long id, Listing updatedListing) {
        return ListingRepository.findById(id).map(antiquity -> {
            antiquity.setPriceAntiquity(updatedListing.getPriceAntiquity());
            antiquity.setDescriptionAntiquity(updatedListing.getDescriptionAntiquity());
            antiquity.setTitleAntiquity(updatedListing.getTitleAntiquity());
            antiquity.setMailMember(updatedListing.getMailMember());
            antiquity.setState(updatedListing.getState());
            antiquity.setEstAffiche(updatedListing.getEstAffiche());
            return ListingRepository.save(antiquity);
        }).orElseThrow(() -> new RuntimeException("Antiquity not found with id: " + id));
    }
}



