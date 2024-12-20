package be.anticair.anticairapi.keycloak.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;

@Service
public class AdminService {

    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    public AdminService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void forcePasswordReset(String userId) {
        UsersResource usersResource = keycloak.realm(realm).users();

        CredentialRepresentation passwordCredential = new CredentialRepresentation();
        passwordCredential.setTemporary(true);
        passwordCredential.setType(CredentialRepresentation.PASSWORD);
        //passwordCredential.setValue("azerty");

        usersResource.get(userId).resetPassword(passwordCredential);
    }
}
