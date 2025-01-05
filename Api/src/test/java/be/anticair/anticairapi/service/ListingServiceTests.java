package be.anticair.anticairapi.service;

import be.anticair.anticairapi.Class.Listing;
import be.anticair.anticairapi.Class.ListingWithPhotosDto;
import be.anticair.anticairapi.keycloak.service.ListingRepository;
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
 * @author Verly Noah
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
    private static final String TEST_SELLER_EMAIL = "john.doe@example.com";
    /**
     * The mail that will be use for the owner of the antiquity
     */
    private static final String TEST_ANTIQUARIAN_EMAIL = "alexis.zarzycki0212@gmail.com";
    /**
     * The mail that will be use for the new owner of the antiquity
     */
    private static final String TEST_NEW_ANTIQUARIAN_EMAIL = "antiquarian@anticairapp.sixela.be";

    /**
     * Function which allow to delete a antiquity
     * @param listing, the listing that will be deleted
     * @author Verly Noah
     */
    public void cleanListing(Listing listing){
        this.listingRepository.delete(listing);
    }

<<<<<<< Updated upstream
=======
    /**
     * Test to check if the createListing service work
     * @author Blommaert Youry
     */
    @Test
    public void testCreateListing_Success() throws MessagingException, IOException {
        listing = new Listing();
        listing.setPriceAntiquity(100.0);
        listing.setDescriptionAntiquity("A description");
        listing.setTitleAntiquity("Pandora's box");

        Listing createdListing = listingService.createListing(TEST_SELLER_EMAIL, listing, new ArrayList<>());

        assertNotNull(createdListing);
        assertNotNull(createdListing.getMailSeller());
        assertEquals(TEST_SELLER_EMAIL, createdListing.getMailSeller());
        assertTrue(listingRepository.findById((long)createdListing.getIdAntiquity()).isPresent());
        this.cleanListing(createdListing);
    }

    /**
     * Test to check if the createListing service work with a price less than 0
     * @author Blommaert Youry
     */
    @Test
    public void testCreateListing_PriceLessThanZero() {
        listing = new Listing();
        listing.setPriceAntiquity(-1.0);
        listing.setDescriptionAntiquity("A description");
        listing.setTitleAntiquity("A title");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            listingService.createListing(TEST_SELLER_EMAIL, listing, Collections.emptyList());
        });

        assertEquals("Price is negative", exception.getMessage());
    }

    /**
     * Test to check if the createListing service work with a user not in the database
     * @author Blommaert Youry
     */
    @Test
    public void testCreateListing_UserNotFound() {
        listing = new Listing();
        listing.setPriceAntiquity(100.0);
        listing.setDescriptionAntiquity("A description");
        listing.setTitleAntiquity("A title");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            listingService.createListing("nonexistent@example.com", listing, Collections.emptyList());
        });

        assertEquals("No users found with email: nonexistent@example.com", exception.getMessage());
    }

    /**
     * Test to check if the createListing service work with missing required fields
     * @author Blommaert Youry
     */
    @Test
    public void testCreateListing_MissingRequiredFields() {
        listing = new Listing();
        listing.setPriceAntiquity(0.0);
        listing.setDescriptionAntiquity(null);
        listing.setTitleAntiquity(null);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            listingService.createListing(TEST_SELLER_EMAIL, listing, Collections.emptyList());
        });

        assertEquals("Price, description, and title are required", exception.getMessage());
    }

>>>>>>> Stashed changes

    /**
     * Test to check if the applyCommission service work with normal values
     * @author Verly Noah
     */
    @Test
    public void applyCommission() throws MessagingException, IOException {
        //Creation of an antiquity
        this.listing = new Listing(0,100.0,"A description","Pandora's box",TEST_ANTIQUARIAN_EMAIL,0,false,TEST_SELLER_EMAIL);
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
     * @author Verly Noah
     */
    @Test
    public void applyCommissionNull(){
        assertNull(this.listingService.applyCommission(null));
        assertNull(this.listingService.applyCommission(0));
        assertNull(this.listingService.applyCommission(-1));

    }
    /**
     * Test to check if the changeListing antiquarian work
     * @author Verly Noah
     */
    @Test
    public void ChangeAntiquarianTestFromListingService(){
        for (int i = 0; i < 10; i++) {
            this.listing = new Listing(0,100.0,"A description","Pandora's box",TEST_ANTIQUARIAN_EMAIL,0,false,TEST_SELLER_EMAIL);
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
<<<<<<< Updated upstream
=======

    /**
     * Test to reject an antiquity
     * @author Verly Noah
     */
    @Test
    public void rejectAntiquarianTestFromListingService() {
        this.listing = new Listing(0,100.0,"A description","Pandora's box",TEST_ANTIQUARIAN_EMAIL,0,false,TEST_SELLER_EMAIL);
        this.listing = this.listingRepository.save(this.listing);
        Map<String,String> otherInformation = new HashMap<>();
        otherInformation.put("title",listing.getTitleAntiquity());
        otherInformation.put("description",listing.getDescriptionAntiquity());
        otherInformation.put("price",listing.getPriceAntiquity().toString());
        otherInformation.put("id",listing.getIdAntiquity().toString());
        otherInformation.put("note_title","test");
        otherInformation.put("note_description","test");
        otherInformation.put("note_price","test");
        otherInformation.put("note_photo","test");
        this.listing = this.listingService.rejectAntiquity(otherInformation);
        assertEquals(AntiquityState.REJECTED.getState(), this.listing.getState());
        this.cleanListing(listing);
    }

    /**
     * Test to accept an antiquity
     * @author Verly Noah
     */
    @Test
    public void acceptAntiquarianTestFromListingService() {
        this.listing = new Listing(0,100.0,"A description","Pandora's box",TEST_ANTIQUARIAN_EMAIL,AntiquityState.NEED_TO_BE_CHECKED.getState(), false,TEST_SELLER_EMAIL);
        this.listing = this.listingRepository.save(this.listing);
        Map<String,String> otherInformation = new HashMap<>();
        otherInformation.put("title",listing.getTitleAntiquity());
        otherInformation.put("description",listing.getDescriptionAntiquity());
        otherInformation.put("price",listing.getPriceAntiquity().toString());
        otherInformation.put("id",listing.getIdAntiquity().toString());
        this.listing = this.listingService.acceptAntiquity(otherInformation);
        assertEquals(AntiquityState.ACCEPTED.getState(), this.listing.getState());
        this.cleanListing(listing);
    }

    /**
     * Test to accept an antiquity but with no id
     * @author Verly Noah
     */
    @Test
    public void acceptAntiquarianTestFromListingServiceNull() {
        Map<String,String> otherInformation = new HashMap<>();
        assertNull(this.listingService.acceptAntiquity(otherInformation));
        assertNull(this.listingService.acceptAntiquity(null));
        otherInformation.put("id","");
        assertNull(this.listingService.acceptAntiquity(otherInformation));
    }

    /**
     *
     * Test to accept an antiquity but with no id
     * @author Verly Noah
     */
    @Test
    public void rejectAntiquarianTestFromListingServiceNull() {
        Map<String,String> otherInformation = new HashMap<>();
        assertNull(this.listingService.rejectAntiquity(otherInformation));
        assertNull(this.listingService.rejectAntiquity(null));
        otherInformation.put("id","");
        assertNull(this.listingService.rejectAntiquity(otherInformation));
        otherInformation.put("note_title","");
        otherInformation.put("note_description","");
        otherInformation.put("note_price","");
        otherInformation.put("note_photo","");
        otherInformation.put("id","-1");
        assertNull(this.listingService.rejectAntiquity(otherInformation));
    }
>>>>>>> Stashed changes
}
