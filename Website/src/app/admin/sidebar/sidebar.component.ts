import { Component } from '@angular/core';
import { AuthService } from '../../auth.service';
import { ThemeService } from '../../theme.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {

  currentTheme: 'dark' | 'light' = 'light'; // Actual theme, by default light


  constructor(private authService : AuthService, private themeService : ThemeService, private router: Router) { }

  ngOnInit(){
    // Subscribe to Theme event
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });
  }

  // Toggle between themes trough the service
  toggleTheme(): void {
    this.themeService.toggleTheme();
  }


  logout(){
    this.authService.logout();
    // Redirect to home page
    this.router.navigate(['/home']);
  }

}
