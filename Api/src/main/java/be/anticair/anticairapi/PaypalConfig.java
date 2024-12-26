package be.anticair.anticairapi;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaypalConfig {

    // PayPal client ID, configured in application properties
    @Value("${paypal.client.id}")
    private String clientId;

    // PayPal client secret, configured in application properties
    @Value("${paypal.client.secret}")
    private String clientSecret;

    // PayPal mode ("sandbox" for test purposes or "live" for production)
    @Value("${paypal.mode}")
    private String mode;

    // API Context: Responsible for authenticating and managing PayPal API requests
    private APIContext apiContext;

    /**
     * Lazy loads and initializes the APIContext object.
     * Ensures that the mode is either "sandbox" or "live" to avoid PayPal API errors.
     *
     * @return APIContext instance
     * @Author Zarzycki Alexis
     */
    private APIContext getApiContext() {
        if (this.apiContext == null) {
            // Validate the mode before creating the APIContext
            if (!"sandbox".equalsIgnoreCase(mode) && !"live".equalsIgnoreCase(mode)) {
                throw new IllegalArgumentException("Invalid PayPal mode: The mode must be either 'sandbox' or 'live'.");
            }

            // Initialize the API context with the client ID, secret, and mode
            this.apiContext = new APIContext(clientId, clientSecret, mode);
        }
        return this.apiContext;
    }

    /**
     * Creates a PayPal Payment object with specific details such as price, currency, payment method, etc.
     * The payment object is sent to PayPal for processing.
     *
     * @param total       The total amount to be paid
     * @param currency    The currency (e.g., USD, EUR)
     * @param method      Payment method (must be "paypal")
     * @param intent      Payment intent (e.g., "sale" or "authorize")
     * @param description A description of the payment/message to the payer
     * @param successUrl  The callback URL for successful payment
     * @param cancelUrl   The callback URL for canceled payment
     * @param listingId   Custom ID to track the associated transaction (e.g., a product or order ID)
     * @return A Payment object created via PayPal APIs
     * @throws PayPalRESTException In case of communication error with PayPal
     * @Author Zarzycki Alexis
     */
    public Payment createPayment(Double total, String currency, String method, String intent,
                                 String description, String successUrl, String cancelUrl, Integer listingId) throws PayPalRESTException {

        // Set the payment amount and format it to 2 decimal places for PayPal
        Amount amount = new Amount();
        amount.setCurrency(currency);

        // Force format to US standards to prevent PayPal issues
        amount.setTotal(String.format(java.util.Locale.US, "%.2f", total));

        // Configure transaction details
        Transaction transaction = new Transaction();
        transaction.setDescription(description); // Transaction description
        transaction.setCustom(String.valueOf(listingId)); // Set a custom ID to track listing details
        transaction.setAmount(amount); // Associate amount information with the transaction

        // Configure the payment object
        Payment payment = new Payment();
        payment.setIntent(intent); // Defines the intent of the payment (e.g., sale)
        payment.setPayer(new Payer().setPaymentMethod(method)); // Sets the payer and payment method
        payment.setTransactions(List.of(transaction)); // Assign transaction details to the payment

        // Set redirection URLs for success and cancel scenarios
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl); // URL to redirect if the payment is canceled
        redirectUrls.setReturnUrl(successUrl); // URL to redirect after successful payment

        payment.setRedirectUrls(redirectUrls);

        // Sends the created payment object to PayPal for processing
        return payment.create(getApiContext());
    }

    /**
     * Executes a previously approved PayPal payment using payment ID and payer ID.
     * This completes the payment process.
     *
     * @param paymentId The ID of the previously created payment
     * @param payerId   The ID of the payer who approved the payment
     * @return The Payment object containing the finalized payment details
     * @throws PayPalRESTException In case of communication or execution error
     * @Author Zarzycki Alexis
     */
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        // Create a new payment object and set its ID
        Payment payment = new Payment();
        payment.setId(paymentId); // Associate the payment ID with the payment object

        // Create a PaymentExecution object and set the payer ID
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        // Execute the payment using the current API context
        return payment.execute(getApiContext(), paymentExecution);
    }
}