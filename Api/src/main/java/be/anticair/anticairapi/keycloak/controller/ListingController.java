package be.anticair.anticairapi.keycloak.controller;


import be.anticair.anticairapi.Class.Listing;
import be.anticair.anticairapi.keycloak.service.ListingService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * REST Controller for managing listing in the database.
 * @Author Blommaert Youry, Neve Thierry
 */

@RestController
@RequestMapping("/api/listing")
public class ListingController {
    @Autowired
    private ListingService listingService;


    /**
     * Update an existing Antiquity by its ID.
     *
     * @param id              The ID of the Antiquity to update.
     * @param updatedListing The updated Antiquity details from the request body.
     * @return ResponseEntity with the updated Antiquity or a 404 status if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Listing> updateListing(@PathVariable Long id, @RequestBody Listing updatedListing) {
        try {
            // Call the service method using the injected instance
            Listing listing = listingService.updateListing(id, updatedListing);
            return ResponseEntity.ok(listing); // Return the updated Listing with HTTP 200
        } catch (RuntimeException e) {
            // Return HTTP 404 if the Listing with the given ID does not exist
            return ResponseEntity.notFound().build();
        }
    }
}
