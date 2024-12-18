package be.anticair.anticairapi.service;

import be.anticair.anticairapi.Class.Listing;
import be.anticair.anticairapi.Class.ListingWithPhotosDto;
import be.anticair.anticairapi.enumeration.AntiquityState;
import be.anticair.anticairapi.keycloak.service.ListingRepository;
import be.anticair.anticairapi.keycloak.service.ListingService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
     * The repository
     */
    @Autowired
    private ListingRepository listingRepository;

    /**
     * The antiquity that will be used for the tests
     */
    private Listing listing;

    /**
     * The mail that will be use for the owner of the antiquity
     */
    private static final String TEST_SELLER_EMAIL = "testseller@gmail.com";
    /**
     * The mail that will be use for the owner of the antiquity
     */
    private static final String TEST_ANTIQUARIAN_EMAIL = "testmail2@gmail.com";
    /**
     * The mail that will be use for the new owner of the antiquity
     */
    private static final String TEST_NEW_ANTIQUARIAN_EMAIL = "testmail1@gmail.com";

    /**
     * Function which allow to delete a antiquity
     * @param listing, the listing that will be deleted
     * @Author Verly Noah
     */
    public void cleanListing(Listing listing){
        this.listingRepository.delete(listing);
    }


    /**
     * Test to check if the applyCommission service work with normal values
     * @Author Verly Noah
     */
    @Test
    public void applyCommission(){
        //Creation of an antiquity
        this.listing = new Listing(0,100.0,"A description","Pandora's box",TEST_ANTIQUARIAN_EMAIL,0,false,TEST_SELLER_EMAIL, AntiquityState.ACCEPTED);
        //The photos for the antiquity
        List<MultipartFile> photos = new ArrayList<>();
        //Add the antiquity in the database
        Listing listingAdded = this.listingService.createListing(TEST_SELLER_EMAIL,listing,photos);
        //Apply the commission
        Listing listingCommissionAplied = listingService.applyCommission(listingAdded.getIdAntiquity());
        //Check if the antiquity return is right
        assertEquals(120.0,listingCommissionAplied.getPriceAntiquity());
        //Get the modified antiquity from the database
        ListingWithPhotosDto listingWithPhotosDto = this.listingService.getListingById(listingCommissionAplied.getIdAntiquity());
        //Check if the antiquity return is right
        assertEquals(120.0,listingWithPhotosDto.getPriceAntiquity());
        this.cleanListing(listingCommissionAplied);

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
    /**
     * Test to check if the changeListing antiquarian work
     * @Author Verly Noah
     */
    @Test
    public void ChangeAntiquarianTestFromListingService() throws MessagingException, IOException {
        for (int i = 0; i < 10; i++) {
            this.listing = new Listing(0,100.0,"A description","Pandora's box",TEST_ANTIQUARIAN_EMAIL,0,false,TEST_SELLER_EMAIL, AntiquityState.ACCEPTED);
            this.listingRepository.save(this.listing);
        }
        List<Listing> listingList = this.listingRepository.getAllAntiquityNotCheckedFromAnAntiquarian(TEST_ANTIQUARIAN_EMAIL);
        for (Listing listing : listingList) {
            this.listingService.changeListingAntiquarian(listing,TEST_NEW_ANTIQUARIAN_EMAIL);
        }
        assertEquals(0,this.listingRepository.getAllAntiquityNotCheckedFromAnAntiquarian(TEST_ANTIQUARIAN_EMAIL).size());
        for (Listing listing : listingList) {
            this.cleanListing(listing);
        }

    }
}
