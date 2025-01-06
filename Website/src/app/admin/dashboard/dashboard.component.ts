import { Component } from '@angular/core';
import { ThemeService } from '../../../service/theme.service';
import {UserService} from '../../../service/user.service';
import { AuthService } from '../../../service/auth.service';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {

  currentTheme: 'dark' | 'light' = 'light'; // Actual theme, by default light
  numberOfUsers: number = 0;
  isLoading: any;
  error: string | undefined;
  constructor(private themeService: ThemeService, private userService: UserService,private authService: AuthService) {}

  ngOnInit(): void {
    // Subscribe to Theme event
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });
    this.fetchUserCount();

  }

  async fetchUserCount() {
    try {
      const token = await this.authService.getToken();
      this.numberOfUsers = await this.userService.numberOfUsers(token);
      this.isLoading = false;
    } catch (err) {
      this.error = 'Failed to load user count';
      this.isLoading = false;
      console.error('Error fetching user count:', err);
    }
  }

}
