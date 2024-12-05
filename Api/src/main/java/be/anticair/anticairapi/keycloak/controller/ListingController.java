package be.anticair.anticairapi.keycloak.controller;


import be.anticair.anticairapi.Class.Listing;
import be.anticair.anticairapi.Class.PhotoAntiquity;
import be.anticair.anticairapi.keycloak.service.ListingService;
import be.anticair.anticairapi.keycloak.service.PhotoAntiquityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * REST Controller for managing listing in the database.
 * @Author Blommaert Youry, Neve Thierry
 */

@RestController
@RequestMapping("/api/listing")
public class ListingController {
    @Autowired
    private ListingService listingService;
    @Autowired
    private PhotoAntiquityService photoAntiquityService;


    /**
     * Update an antiquity along with its associated images.
     *
     * @param antiquityJson JSON representation of the antiquity object.
     * @param images Multipart files representing new images for the antiquity.
     * @return ResponseEntity indicating the update status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateAntiquityWithPhotos(@PathVariable Integer id, @RequestParam("antiquity") String antiquityJson, @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            // Deserialize the antiquity JSON into a Listing object
            ObjectMapper objectMapper = new ObjectMapper();
            Listing antiquity = objectMapper.readValue(antiquityJson, Listing.class);

            // Call the Listing service and the Images service to update the antiquity
            listingService.updateListing(Long.valueOf(id), antiquity);
            photoAntiquityService.updatePhotos(id, images);

            return ResponseEntity.ok("Antiquity and images updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error : " + e.getMessage());
        }
    }


    /**
     * Create a new listing in the database.
     *
     * @param email The email of the user creating the listing.
     * @param title The title of the listing.
     * @param description The description of the listing.
     * @param price The price of the listing.
     * @param photos The images associated with the listing.
     * @return ResponseEntity indicating the creation status.
     * @Author Blommaert Youry
     */
    @PostMapping("/create")
    public ResponseEntity<?> createListing(
            @RequestParam("email") String email,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos) {

        try {
            // Create a new Listing object
            Listing newListing = new Listing();
            newListing.setTitleAntiquity(title);
            newListing.setDescriptionAntiquity(description);
            newListing.setPriceAntiquity(price);

            // call the Listing service to create the listing
            Listing createdListing = listingService.createListing(email, newListing, photos);

            // Return the created listing
            return ResponseEntity.status(HttpStatus.CREATED).body(createdListing);

        } catch (IllegalArgumentException e) {
            // Manage missing required fields
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (RuntimeException e) {
            // Manage generic exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

        } catch (Exception e) {
            // Manage unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}
