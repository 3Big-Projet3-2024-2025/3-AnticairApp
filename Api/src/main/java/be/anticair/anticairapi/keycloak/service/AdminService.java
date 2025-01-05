package be.anticair.anticairapi.keycloak.service;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    public AdminService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    /**
     * Force la réinitialisation du mot de passe en utilisant les actions requises de Keycloak
     * @param userId ID de l'utilisateur
     */
    public void forcePasswordReset(String userId) {
        UsersResource usersResource = keycloak.realm(realm).users();

        // Utiliser les actions requises pour forcer la mise à jour du mot de passe
        usersResource.get(userId).executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
    }

}
