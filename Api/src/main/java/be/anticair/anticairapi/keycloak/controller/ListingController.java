package be.anticair.anticairapi.keycloak.controller;


import be.anticair.anticairapi.Class.Listing;
import be.anticair.anticairapi.Class.ListingWithPhotosDto;
import be.anticair.anticairapi.Class.PhotoAntiquity;
import be.anticair.anticairapi.keycloak.service.ListingService;
import be.anticair.anticairapi.keycloak.service.PhotoAntiquityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public ResponseEntity<Map<String,String>> updateAntiquityWithPhotos(@PathVariable Integer id, @RequestParam("antiquity") String antiquityJson, @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            // Deserialize the antiquity JSON into a Listing object
            ObjectMapper objectMapper = new ObjectMapper();
            Listing antiquity = objectMapper.readValue(antiquityJson, Listing.class);

            // Call the Listing service and the Images service to update the antiquity
            if(antiquity != null){
                listingService.updateListing(Long.valueOf(id), antiquity);
            }
            if(images!=null){
                photoAntiquityService.updatePhotos(id, images);
            }







            Map<String, String> responseMessage = new HashMap<>();
            responseMessage.put("message", "Antiquity updated successfully");
            return ResponseEntity.ok(responseMessage);
        } catch (Exception e) {
            Map<String, String> responseMessage = new HashMap<>();
            responseMessage.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseMessage);
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

    @GetMapping("/{id}")
    public ResponseEntity<ListingWithPhotosDto> getListingById(@PathVariable Integer id) {
        try {
            ListingWithPhotosDto response = listingService.getListingById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
