package be.anticair.anticairapi.keycloak.service;

import be.anticair.anticairapi.Class.ListingWithPhotosDto;
import be.anticair.anticairapi.Class.PhotoAntiquity;
import jakarta.transaction.Transactional;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import be.anticair.anticairapi.Class.Listing;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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

    /**
     * Create a new listing in the database.
     *
     * @param email The email of the user creating the listing.
     * @param newListing The listing to create.
     * @param photos The photos to associate with the listing.
     * @return The created listing.
     * @Author Blommaert Youry
     */
    public Listing createListing(String email, Listing newListing, List<MultipartFile> photos) {
        UserRepresentation user = userService.getUsersByEmail(email).get(0);

        if(user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        // Verify that the listing has a price, description, and title
        if (newListing.getPriceAntiquity() == null ||
                newListing.getDescriptionAntiquity() == null ||
                newListing.getTitleAntiquity() == null) {
            throw new IllegalArgumentException("Price, description, and title are required");
        }

        newListing.setMailMember(email);
        newListing.setState(0);  // Initialized to 0 (not yet verified)
        newListing.setEstAffiche(false);  // Initialized to false (not yet displayed)

        // Save the listing
        Listing savedListing = ListingRepository.save(newListing);

        // Save the photos
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                try {
                    // Use the PhotoAntiquityService to save the photo
                    PhotoAntiquity photoAntiquity = photoAntiquityService.createPhotoAntiquity(savedListing, photo);
                } catch (IOException e) {
                    // Make sure to delete the listing if the photo fails to save
                    throw new RuntimeException("Failed to save photo", e);
                }
            }
        }

        return savedListing;
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

    public ListingWithPhotosDto getListingById(Integer id) {
        // Get the listing
        Listing listing = ListingRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Listing not found with id " + id));

        // get the images associates with the antiquity
        List<PhotoAntiquity> photos = photoAntiquityService.findByIdAntiquity(id);

        // create and return the objects
        return new ListingWithPhotosDto(listing, photos);
    }
}



