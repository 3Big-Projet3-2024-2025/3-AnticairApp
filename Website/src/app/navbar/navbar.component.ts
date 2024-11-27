import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { ThemeService } from '../theme.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {

  currentTheme: 'dark' | 'light' = 'light'; // Actual theme, by default light
  isMenuOpen = false; // Flag used for the navbar on the mobile phone
  isUserLoggedIn: boolean = false; // Track the login status
  private loginStatusSubscription: Subscription | undefined;

  constructor(private themeService: ThemeService, private authService: AuthService) {}

  ngOnInit(): void {
    // Subscribe to Theme event
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });

     // Subscribe to login status
     this.loginStatusSubscription = this.authService.isLoggedIn().subscribe(loggedIn => {
      this.isUserLoggedIn = loggedIn;
    });
  }

  ngOnDestroy(): void {
    // Unsubscribe to avoid memory leaks
    if (this.loginStatusSubscription) {
      this.loginStatusSubscription.unsubscribe();
    }
  }

  // Toggle entre les thèmes via le service
  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  // Method to login from the navbar
  login() {
    // Calling keycloak method
    this.authService.login();
  }
  
  // Method to logout from the navbar
  logout() {
    // Calling keycloak method
    this.authService.logout();
  }

}