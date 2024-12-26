package be.anticair.anticairapi.keycloak.controller;


import be.anticair.anticairapi.Class.Listing;
import be.anticair.anticairapi.Class.ListingWithPhotosDto;
import be.anticair.anticairapi.PaypalConfig;
import be.anticair.anticairapi.enumeration.AntiquityState;
import be.anticair.anticairapi.keycloak.service.ListingService;
import be.anticair.anticairapi.keycloak.service.PhotoAntiquityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.Invoice;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException;
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
 * @Author Blommaert Youry, Neve Thierry, Zarzycki Alexis
 */

@RestController
@RequestMapping("/api/listing")
public class ListingController {
    @Autowired
    private ListingService listingService;
    @Autowired
    private PhotoAntiquityService photoAntiquityService;
    @Autowired
    private PaypalConfig paypalConfig;


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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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


    /**
     * Get all listings in the database.
     *
     * @return ResponseEntity containing a list of all listings.
     * @Author Blommaert Youry
     */
    @GetMapping("/checked")
    public ResponseEntity<List<Listing>> getAllListingsChecked() {
        try {
            List<Listing> response = listingService.getAllListingsAccepted();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/rejectAntiquity")
    public ResponseEntity<Map<String,String>> rejectAntiquity(@RequestBody Map<String, String> otherInformation) {
        Listing rejectedAntiquity= this.listingService.rejectAntiquity(otherInformation);
        Map<String, String> responseMessage = new HashMap<>();
        if(rejectedAntiquity != null){
            responseMessage.put("message", "Antiquity has been rejected");
            return ResponseEntity.ok(responseMessage);
        }
        responseMessage.put("message", "Error while rejecting antiquity");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
    }

    @PutMapping("/acceptAntiquity")
    public ResponseEntity<Map<String,String>> acceptAntiquity(@RequestBody Map<String, String> otherInformation) {
        Listing acceptAntiquity= this.listingService.acceptAntiquity(otherInformation);
        Map<String, String> responseMessage = new HashMap<>();
        if(acceptAntiquity != null){
            responseMessage.put("message", "Antiquity has been accepted");
            return ResponseEntity.ok(responseMessage);
        }
        responseMessage.put("message", "Error while rejecting antiquity");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
    }


    /**
     * Handles the purchase process for a specific listing by its ID.
     * This method creates a payment authorization request using PayPal
     * and returns a payment approval URL for the user.
     *
     * @param id The unique identifier of the listing to be purchased.
     * @return A ResponseEntity containing the payment approval URL if successful,
     *         or an error message and HTTP status code if the operation fails.
     * @Author Zarzycki Alexis
     */
    @PostMapping("/{id}/buy")
    public ResponseEntity<?> buyListing(@PathVariable Integer id) {
        Map<String, String> responseMessage = new HashMap<>();
        try {
            ListingWithPhotosDto listing = listingService.getListingById(id);
            if (listing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Listing not found");
            }

            if(listing.getState() == AntiquityState.SOLD.getState()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Listing already sold");
            }

            Payment payment = paypalConfig.createPayment(
                    listing.getPriceAntiquity(),
                    "EUR",
                    "paypal",
                    "Sale",
                    "Buying an antiquity : " + listing.getTitleAntiquity(),
                    "http://localhost:4200/payment/success",
                    "http://localhost:4200/payment/error",
                    listing.getIdAntiquity()
            );
            return ResponseEntity.ok(payment.getLinks().stream()
                    .filter(link -> "approval_url".equals(link.getRel()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No approval URL generated"))
                    .getHref());
        } catch (Exception e) {
            responseMessage.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    /**
     * Executes a payment transaction using PayPal API. This method verifies the payment status,
     * retrieves custom metadata related to the transaction, and updates the associated listing state
     * to "sold" if the payment is approved.
     *
     * @param paymentId the unique identifier of the PayPal payment
     * @param payerId the unique identifier of the payer provided by PayPal
     * @return a ResponseEntity containing a success message with the listingId if the payment is
     *         successfully processed, or an error message if the payment fails or encounters an exception
     * @Author Zarzycki Alexis
     */
    @GetMapping("/payment/execute")
    public ResponseEntity<?> executePayment(@RequestParam("paymentId") String paymentId,
                                            @RequestParam("PayerID") String payerId) {
        Map<String, String> responseMessage = new HashMap<>();
        try {
            Payment payment = paypalConfig.executePayment(paymentId, payerId);

            // Check the payment state
            if ("approved".equals(payment.getState())) {
                String listingIdCustomField = payment.getTransactions()
                        .get(0)
                        .getCustom();

                if (listingIdCustomField == null || listingIdCustomField.isEmpty()) {
                    throw new IllegalArgumentException("Listing ID missing in payment metadata.");
                }

                Transaction transaction = payment.getTransactions().get(0);
                String buyerEmail = payment.getPayer().getPayerInfo().getEmail();
                String invoiceDescription = transaction.getDescription();
                Double amount = Double.valueOf(transaction.getAmount().getTotal());
                String currency = transaction.getAmount().getCurrency();
                int quantity = 1;
                Long listingId = Long.valueOf(listingIdCustomField);

                Invoice invoice = paypalConfig.createAndSendInvoice(
                        buyerEmail,
                        invoiceDescription,
                        amount,
                        currency,
                        quantity,
                        listingId
                );

                if (invoice == null) {
                    responseMessage.put("message", "Failed to create or send the invoice.");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
                }

                listingService.markAsSold(listingId);
                responseMessage.put("message", "Payment successful and listing marked as sold");
                responseMessage.put("listingId", listingId.toString());
                responseMessage.put("invoiceNumber", invoice.getId());
                return ResponseEntity.ok(responseMessage);
            }
            responseMessage.put("message", "Payment not approved");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
        } catch (PayPalRESTException e) {
            responseMessage.put("message", "PayPal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        } catch (Exception e) {
            responseMessage.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

}
