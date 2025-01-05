package be.anticair.anticairapi.keycloak;

import be.anticair.anticairapi.Class.Listing;
import be.anticair.anticairapi.enumeration.AntiquityState;
import be.anticair.anticairapi.keycloak.service.AdminService;
import be.anticair.anticairapi.keycloak.service.ListingRepository;
import be.anticair.anticairapi.keycloak.service.UserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import jakarta.ws.rs.NotFoundException;

import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that verify the UserService function's
 * @Author Zarzycki Alexis
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
public class UserServiceTests {

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;
  
    private static final String TEST_USER_EMAIL = "testuser@gmail.com";

    /**
     * The antiquity that will be used for the test
     */
    private Listing listing;

    /**
     * The mail that will be use for the owner of the antiquity
     */
    private static final String TEST_ANTIQUARIAN_EMAIL = "testantiquarian2@gmail.com";

    /**
     * The Listing repository
     */
    @Autowired
    private ListingRepository listingRepository;

    /**
     * Testing the listAntiquarian
     * @Author Zarzycki Alexis
     */
    @Test
    public void testGetAllAntiquarianUsers() {
        // Attempt to retrieve User with a specific Email
        List<UserRepresentation> users = userService.getUsersByGroupName("Antiquarian");

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 1);
    }

    /**
     * Testing the getUserByEmail with a existent User
     * @Author Zarzycki Alexis
     */
    @Test
    public void testGetExistentUserByEmail() {
        // Attempt to retrieve User with a specific Email
        List<UserRepresentation> users = userService.getUsersByEmail(TEST_USER_EMAIL);

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals(TEST_USER_EMAIL, users.getFirst().getEmail());
    }

    /**
     * Testing the getUserByEmail with a non-existent User
     * @Author Zarzycki Alexis
     */
    @Test
    public void testGetNotExistentUserByEmail() {
        assertThrows(NotFoundException.class, () -> {
            // Attempt to retrieve User with a specific Email
            userService.getUsersByEmail("nonexistent_user_987654@anticairapp.be");
        });
    }

    /** Testing the list of all users
     * @Author Blommaert Youry
     */
    @Test
    public void testGetAllUsers() {
        List<UserRepresentation> users = userService.getAllUsers();

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 1);
    }

    /**
     * Testing the list of all users in admin group.
     * @Author Blommaert Youry
     *
     */
    @Test
    public void testGetAllAdminUsers() {
        List<UserRepresentation> users = userService.getUsersByGroupName("Admin");

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 1);
    }

    /** Testing the desactivate an existent User
     * @Author Zarzycki Alexis
     */
    @Test
    public void testDesactiveUser(){
        assertTrue(userService.disableUser(TEST_USER_EMAIL));
    }

    /**
     * Testing the desactivate a non-existent user
     * @Author Zarzycki Alexis
     */
    @Test
    public void testDesactiveNonExistentUser(){
        assertThrows(NotFoundException.class, () -> {
            userService.disableUser("nonexistent_user_987654@anticairapp.be");
        });
    }

    /**
     * Testing the enable on an existent User
     * @Author Zarzycki Alexis
     */
    @Test
    public void testEnableUser(){
        assertTrue(userService.enableUser(TEST_USER_EMAIL));
    }

    /**
     * Testing the enable on an non-existent user
     * @Author Zarzycki Alexis
     */
    @Test
    public void testEnableNonExistentUser(){
        assertThrows(NotFoundException.class, () -> {
            userService.enableUser("nonexistent_user_987654@anticairapp.be");
        });

    }

    /**
     * Testing the get User Status on a enabled user
     * @Author Zarzycki Alexis
     */
    @Test
    public void testGetStatusEnabledUser(){
        assertTrue(userService.getUserStatus(TEST_USER_EMAIL));
    }

    /**
     * Testing the get User Status on a disabled user
     * @Author Zarzycki Alexis
     */
    @Test
    public void testGetStatusDisabledUser(){
        userService.disableUser(TEST_USER_EMAIL);
        assertFalse(userService.getUserStatus(TEST_USER_EMAIL));
        userService.enableUser(TEST_USER_EMAIL);
    }

    /**
     * Testing the get User Status on a non-existent user
     * @Author Zarzycki Alexis
     */
    @Test
    public void testGetStatusEnabledNonExistentUser(){
        assertThrows(NotFoundException.class, () -> {
            userService.getUserStatus("nonexistent_user_987654@anticairapp.be");
        });
    }

    /**
     * Test to verify successful password reset for an existing user.
     * @Author Lawzen
     */
    @Test
    @DisplayName("Force Password Reset - Existing User")
    public void testForcePasswordReset_Success() {
        assertDoesNotThrow(() -> {
            adminService.forcePasswordReset("alexis.zarzycki0212@gmail.com");
        }, "Password reset should not throw an exception for an existing user.");
    }

    /**
     * Test to verify that a UserNotFoundException is thrown for a non-existent user.
     * @Author Lawzen
     */
    @Test
    @DisplayName("Force Password Reset - Non-Existent User")
    public void testForcePasswordReset_UserNotFound() {
        Exception exception = assertThrows(Exception.class, () -> {
            adminService.forcePasswordReset("NON_EXISTENT_USER_EMAIL@gmail.com");
        }, "A UserNotFoundException should be thrown for a non-existent user.");

        assertEquals("No users found with email: NON_EXISTENT_USER_EMAIL@gmail.com", exception.getMessage());
    }



    /**
     * Testing the get User Status on a non-existent user
     * @Author Zarzycki Alexis
     */
    @Test
    public void testGetStatusDisabledNonExistentUser(){
        assertThrows(NotFoundException.class, () -> {
            userService.getUserStatus("nonexistent_user_987654@anticairapp.be");
        });
    }

    /**
     * Testing the get change antiquarian
     * @Author Verly Noah
     */
    @Test
    public void testChangeAntiquarianFromAntiquityOK() throws MessagingException, IOException {
        for (int i = 0; i < 5; i++) {
            this.listing = new Listing(0,100.0,"A description","Pandora's box",TEST_ANTIQUARIAN_EMAIL,0,false,TEST_USER_EMAIL);
            this.listingRepository.save(this.listing);
        }

        List<Listing> listingList = this.listingRepository.getAllAntiquityNotCheckedFromAnAntiquarian(TEST_ANTIQUARIAN_EMAIL);
        this.userService.redistributeAntiquity(TEST_ANTIQUARIAN_EMAIL);
        assertEquals(0,this.listingRepository.getAllAntiquityNotCheckedFromAnAntiquarian(TEST_ANTIQUARIAN_EMAIL).size());
        this.listingRepository.deleteAll(listingList);

    }

    /**
     * Testing the get change antiquarian but with a null value for email
     * @Author Verly Noah
     */
    @Test
    public void testChangeAntiquarianFromAntiquityNull() throws MessagingException, IOException {
        assertEquals("No email address provided",this.userService.redistributeAntiquity(null));
    }

    /**
     * Test for getUserBalance with a valid user.
     * Assumes a user with email 'testuser@gmail.com' exists in Keycloak
     * and has a 'balance' attribute set.
     * @Author Zarzycki Alexis
     */
    @Test
    public void testGetUserBalanceValidUser() {
        double balance = userService.getUserBalance(TEST_USER_EMAIL);
        assertTrue(balance >= 0, "Balance should be a non-negative integer.");
    }

    /**
     * Test for getUserBalance with a non-existent user.
     * Assumes no user exists with email 'nonexistent_user_987654@anticairapp.be'.
     * @Author Zarzycki Alexis
     */
    @Test
    public void testGetUserBalanceNonExistentUser() {
        String nonExistentEmail = "nonexistent_user_987654@anticairapp.be";
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserBalance(nonExistentEmail)
        );

        assertEquals("No users found with the email: " + nonExistentEmail, exception.getMessage());
    }

    /**
     * Test for addToUserBalance with a valid user.
     * Assumes a user with email 'testuser@gmail.com' exists in Keycloak.
     * @Author Zarzycki Alexis
     */
    @Test
    public void testAddToUserBalanceValidUser() {
        double initialBalance = userService.getUserBalance(TEST_USER_EMAIL);

        // Add 50 to the user's balance
        userService.addToUserBalance(TEST_USER_EMAIL, 50);

        double updatedBalance = userService.getUserBalance(TEST_USER_EMAIL);
        assertEquals(initialBalance + 50, updatedBalance, 0.01);  // Allow a small tolerance for floating-point comparisons
    }

    /**
     * Test for addToUserBalance with a non-existent user.
     * Assumes no user exists with email 'nonexistent_user_987654@anticairapp.be'.
     * @Author Zarzycki Alexis
     */
    @Test
    public void testAddToUserBalanceNonExistentUser() {
        String nonExistentEmail = "nonexistent_user_987654@anticairapp.be";
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.addToUserBalance(nonExistentEmail, 50)
        );

        assertEquals("No users found with the email: " + nonExistentEmail, exception.getMessage());
    }

    /**
     * Test for addToUserBalance ensuring the balance cannot be negative.
     * Assumes a user with email 'testuser@gmail.com' exists in Keycloak.
     * @Author Zarzycki Alexis
     */
    @Test
    public void testAddToUserBalanceNegativeAmount() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.addToUserBalance(TEST_USER_EMAIL, -50)
        );

        assertEquals("Error while updating the balance of the user with email: " + TEST_USER_EMAIL, exception.getMessage());
    }

}