package be.anticair.anticairapi.keycloak.service;

import be.anticair.anticairapi.Class.ListingWithPhotosDto;
import be.anticair.anticairapi.Class.PhotoAntiquity;
import be.anticair.anticairapi.enumeration.TypeOfMail;
import jakarta.mail.MessagingException;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import be.anticair.anticairapi.Class.Listing;
import be.anticair.anticairapi.enumeration.AntiquityState;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static be.anticair.anticairapi.enumeration.AntiquityState.*;

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
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoAntiquityService photoAntiquityService;

    private AntiquityState antiquityState;

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
        if(newListing.getPriceAntiquity() < 0) {
            throw new IllegalArgumentException("Price is negative");
        }

        List<UserRepresentation> users = userService.getUsersByEmail(email);


        if(users.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }

        UserRepresentation user = userService.getUsersByEmail(email).get(0);

        // Verify that the listing has a price, description, and title
        if (newListing.getPriceAntiquity() == 0 ||
                newListing.getDescriptionAntiquity() == null ||
                newListing.getTitleAntiquity() == null) {
            throw new NullPointerException("Price, description, and title are required");
        }

        List<UserRepresentation> usersAntiquarians = userService.getUsersByGroupName("Antiquarian");
        if(usersAntiquarians.isEmpty()) {
            throw new RuntimeException("Antiquarian not found");
        }

        UserRepresentation userAntiquarian;
        // Get a random antiquarian different from the author of the listing
        do {
            Random random = new Random();
            userAntiquarian = usersAntiquarians.get(random.nextInt(usersAntiquarians.size()));
        } while (email.equals(userAntiquarian.getEmail()));

        newListing.setMailSeller(email);
        newListing.setState(0);  // Initialized to 0 (not yet verified)
        newListing.setIsDisplay(false);  // Initialized to false (not yet displayed)
        newListing.setMailAntiquarian(userAntiquarian.getEmail());

        // Save the listing if it has all the required fields
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


    public Listing rejectAntiquity(Map<String,String> otherInformation){
        Optional<Listing> antiquity = getAntiquityById(Long.valueOf(otherInformation.get("id")));
        if(antiquity.isEmpty()){return null;}
        antiquity.get().setState(REJECTED.getState());

        otherInformation.put("title",antiquity.get().getTitleAntiquity());
        otherInformation.put("description",antiquity.get().getDescriptionAntiquity());
        otherInformation.put("price", antiquity.get().getPriceAntiquity().toString());

        otherInformation.put("note_title",otherInformation.get("note_title"));
        otherInformation.put("note_description",otherInformation.get("note_description"));
        otherInformation.put("note_price",otherInformation.get("note_price"));
        otherInformation.put("note_photo",otherInformation.get("note_photo"));
        antiquity = Optional.of(this.ListingRepository.save(antiquity.get()));
        try {
            this.emailService.sendHtmlEmail(antiquity.get().getMailSeller(),"",TypeOfMail.REJECTIONOFANTIQUITY,otherInformation);
            return antiquity.get();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Listing acceptAntiquity(Map<String,String> otherInformation){
        Optional<Listing> antiquity = getAntiquityById(Long.valueOf(otherInformation.get("id")));
        if(antiquity.isEmpty()){return null;}
        antiquity.get().setState(ACCEPTED.getState());

        otherInformation.put("title",antiquity.get().getTitleAntiquity());
        otherInformation.put("description",antiquity.get().getDescriptionAntiquity());
        otherInformation.put("price", antiquity.get().getPriceAntiquity().toString());

        antiquity = Optional.of(this.ListingRepository.save(antiquity.get()));
        try {
            this.emailService.sendHtmlEmail(antiquity.get().getMailSeller(),"",TypeOfMail.VALIDATIONOFANANTIQUITY,otherInformation);

            return this.applyCommission(Integer.valueOf(otherInformation.get("id")));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Listing updateListing(Long id, Listing updatedListing) {
        return ListingRepository.findById(id).map(antiquity -> {
            antiquity.setPriceAntiquity(updatedListing.getPriceAntiquity());
            antiquity.setDescriptionAntiquity(updatedListing.getDescriptionAntiquity());
            antiquity.setTitleAntiquity(updatedListing.getTitleAntiquity());
            antiquity.setMailSeller(updatedListing.getMailSeller());
            antiquity.setMailAntiquarian(updatedListing.getMailAntiquarian());
            antiquity.setState(updatedListing.getState());
            antiquity.setIsDisplay(updatedListing.getIsDisplay());
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

    /**
     *Function to apply the commission
     *
     * @param id the id of the antiquity
     * @return the antiquity with the commission
     * @Author Verly Noah
     */
    public Listing applyCommission(Integer id){
        //Check if the id is valid
        if(id==null ||id<1) return null;
        //Get the antiquity with the id
        Optional<Listing> listing = ListingRepository.findById(Long.valueOf(id));
        //If there isn't antiquity with this id, return null
        if(listing.isEmpty()) {return null;}
        //Applied the commission
        listing.get().applyCommission();
        //Save the change
        return ListingRepository.save(listing.get());
    }

    /**
     * Allow to change the antiquarian of the antiquity
     *
     * @param antiquity the antiquity that we want to change the antiquarian
     * @param emailNewAntiquarian the email of the new antiquarian
     * @return a boolean, true if the change has been made, false, in case of a problem
     */
    public boolean changeListingAntiquarian(Listing antiquity, String emailNewAntiquarian) throws MessagingException, IOException {
        if(antiquity==null || emailNewAntiquarian.isEmpty()) return false;
        if(userService.getUsersByEmail(emailNewAntiquarian).getFirst() == null) {return false;}
        if(!this.userService.getUserStatus(emailNewAntiquarian)) return false;
        antiquity.setMailAntiquarian(emailNewAntiquarian);
        ListingRepository.save(antiquity);
        Map<String,String> otherInformation = new HashMap<>();
        otherInformation.put("title", antiquity.getTitleAntiquity());
        otherInformation.put("description", antiquity.getDescriptionAntiquity());
        otherInformation.put("price", antiquity.getPriceAntiquity().toString());
        this.emailService.sendHtmlEmail(emailNewAntiquarian, "info@anticairapp.sixela.be", TypeOfMail.REDISTRIBUTEANTIQUITYNEWANTIQUARIAN, otherInformation);
        return true;
    }
}


