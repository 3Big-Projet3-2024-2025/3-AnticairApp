package be.anticair.anticairapi.service;

import be.anticair.anticairapi.Class.Listing;
import be.anticair.anticairapi.Class.ListingWithPhotosDto;
import be.anticair.anticairapi.keycloak.service.ListingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that verify the Listing service function's
 * @Author Verly Noah
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
public class ListingServiceTests {

    /**
     * The service
     */
    @Autowired
    private ListingService listingService;

    /**
     * The antiquity that will be used for the tests
     */
    private Listing listing;

    /**
     * Test to check if the applyCommission service work with normal values
     * @Author Verly Noah
     */
    @Test
    public void applyCommission(){
        //Creation of an antiquity
        this.listing = new Listing(0,100.0,"A description","Pandora's box","noahverly@hotmail.be",0,false,1);
        //The photos for the antiquity
        List<MultipartFile> photos = new ArrayList<>();
        //Add the antiquity in the database
        Listing listingAdded = this.listingService.createListing("noahverly@hotmail.be",listing,photos);
        //Apply the commission
        Listing listingCommissionAplied = listingService.applyCommission(listingAdded.getIdAntiquity());
        //Check if the antiquity return is right
        assertEquals(120.0,listingCommissionAplied.getPriceAntiquity());
        //Get the modified antiquity from the database
        ListingWithPhotosDto listingWithPhotosDto = this.listingService.getListingById(listingCommissionAplied.getIdAntiquity());
        //Check if the antiquity return is right
        assertEquals(120.0,listingWithPhotosDto.getPriceAntiquity());

    }

    /**
     * Test to check if the applyCommission service return null, if the id is null or under 1
     * @Author Verly Noah
     */
    @Test
    public void applyCommissionNull(){
        assertNull(this.listingService.applyCommission(null));
        assertNull(this.listingService.applyCommission(0));
        assertNull(this.listingService.applyCommission(-1));

    }
}
