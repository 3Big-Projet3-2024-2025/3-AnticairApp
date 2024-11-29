package be.anticair.anticairapi.keycloak;

import be.anticair.anticairapi.keycloak.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;

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

    /**
     * Testing the listAntiquarian
     * @Author Zarzycki Alexis
     */
    @Test
    public void testGetAllAnticarianUsers() {
        // Attempt to retrieve User with a specific Email
        List<UserRepresentation> users = userService.getUsersByGroupName("Antiquarian");

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 1);
    }

}