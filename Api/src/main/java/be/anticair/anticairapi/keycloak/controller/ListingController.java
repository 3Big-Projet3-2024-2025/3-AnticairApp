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


    @PostMapping("/create")
    public ResponseEntity<Listing> createListing(@RequestParam String email, @RequestBody Listing newListing) {
        try {
            // Call the service method using the injected instance
            Listing listing = listingService.createListing(email, newListing);
            return ResponseEntity.ok(listing); // Return the created Listing with HTTP 200
        } catch (RuntimeException e) {
            // Return HTTP 404 if the User with the given email does not exist
            return ResponseEntity.notFound().build();
        }
    }
}
