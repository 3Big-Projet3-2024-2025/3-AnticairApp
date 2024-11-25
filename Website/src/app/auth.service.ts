import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private keycloakInitialized = false; // Is Keycloak Initialized ?
  public userDetails: any = null; // To store the user's info
  private loggedInSubject = new BehaviorSubject<boolean>(false); // BehaviorSubject for login status


  constructor(private keycloakService: KeycloakService, private http: HttpClient, private router: Router) {} // Inject Router

  // Method to initialize Keycloak
  async initKeycloak(): Promise<boolean> {
    if (!this.keycloakInitialized) {
      try {
        const keycloakInitPromise = this.keycloakService.init({
          config: {
            url: 'https://keycloak.anticairapp.sixela.be:8443/', // Keycloak server URL
            realm: 'anticairapp',               // Keycloak realm name
            clientId: 'anticairapp',        // Keycloak client ID
          },
          initOptions: {
            onLoad: 'check-sso',
            silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
          },
        });

        const timeoutPromise = new Promise<boolean>((_, reject) => 
          setTimeout(() => reject(new Error('Keycloak initialization timed out')), 5000)
        );

        await Promise.race([keycloakInitPromise, timeoutPromise]);
        
        this.keycloakInitialized = true;
        await this.loadUserProfile();
        this.loggedInSubject.next(await this.keycloakService.isLoggedIn()); // Update login status
        return true;
      } catch (error) {
        console.error('Keycloak initialization failed or timed out', error);
        this.keycloakInitialized = false; // Mark as not initialized
        return false; // Indicate failure
      }
    }
    return true; // Already initialized
  }

  // Method to login through Keycloak
  async login(): Promise<void> {
    const initialized = await this.initKeycloak();
    if (initialized) {
      await this.keycloakService.login();
      await this.loadUserProfile(); // Load user info after login
      this.loggedInSubject.next(true); // Update login status
    } else {
      this.keycloakInitialized = false;
      this.loggedInSubject.next(false); // Update login status on failure
      console.error('Cannot login because Keycloak failed to initialize.');
    }
  }

  // Method to logout through Keycloak
  async logout(): Promise<void> {
    const loggedIn = await this.keycloakService.isLoggedIn();
    if (loggedIn) {
      this.userDetails = null; // Clear user details on logout
      await this.keycloakService.logout();
      this.loggedInSubject.next(false); // Update login status
    } else {
      console.error('Cannot logout because Keycloak is not initialized or user is not logged in.');
    }
  }

  // Method to check if user is logged
  isLoggedIn(): BehaviorSubject<boolean> {
    return this.loggedInSubject; // Return the BehaviorSubject for state management
  }

  // Method to load user profile details from Keycloak
  async loadUserProfile(): Promise<void> {
    if (await this.keycloakService.isLoggedIn()) {
      const userProfile = await this.keycloakService.loadUserProfile();
      const tokenParsed = this.keycloakService.getKeycloakInstance().tokenParsed;
      const phoneNumber = this.getFirstElement(userProfile['attributes']?.['phoneNumber']);

      // Initialize userDetails with basic information
      this.userDetails = {
        email: userProfile.email,
        firstName: userProfile.firstName,
        lastName: userProfile.lastName,
        phoneNumber: phoneNumber,
        groups: tokenParsed && tokenParsed['groups'] ? this.extractGroups(tokenParsed['groups']) : ''
      };

      console.table(this.userDetails);

    }
  }

  // Returns user details if available
  getUserDetails(): any {
    return this.userDetails;
  }

  // Method to get the first element from an array
  getFirstElement(array : any) {
    if (Array.isArray(array) && array.length > 0) {
      return array[0];
    }
    return null; // Return null if the array is empty or not an array
  }

  // Method to extract groups and return them as a comma-separated string
  private extractGroups(groups: string[]): string {
    return groups.join(', '); // Join the groups array into a single string with commas
  }


}
