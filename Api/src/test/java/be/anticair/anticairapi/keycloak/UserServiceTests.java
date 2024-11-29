package be.anticair.anticairapi.keycloak;

import be.anticair.anticairapi.keycloak.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;

import jakarta.ws.rs.NotFoundException;
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

    private static final String TEST_USER_EMAIL = "alexis.zarzycki0212@gmail.com";

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
     * Testing the getUserByEmail with a non existent User
     * @Author Zarzycki Alexis
     */
    @Test
    public void testGetNotExistentUserByEmail() {
        assertThrows(NotFoundException.class, () -> {
            // Attempt to retrieve User with a specific Email
            userService.getUsersByEmail("nonexistent_user_987654@anticairapp.be");
        });
    }

}