import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {NgClass, NgIf} from "@angular/common";
import { ThemeService } from '../../service/theme.service';
import { AuthService } from '../../service/auth.service';
import { UserService } from '../../service/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  standalone: true,
  imports: [FormsModule, NgIf, NgClass],
})
export class ProfileComponent implements OnInit {
  showSuccessMessage: boolean = false;
  showErrorMessage: boolean = false;
  userDetails: any = {};
  isLoading: boolean = true;
  currentTheme: 'dark' | 'light' = 'light'; // Actual theme, by default light

  constructor(
      private authService: AuthService,
      private userService: UserService,
      private themeService: ThemeService
  ) {}


  async ngOnInit() {
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });
    try {
      this.userDetails = this.authService.getUserDetails();
      console.log('User details:', this.userDetails);
    } catch (error) {
      console.error('Error loading user details:', error);
    } finally {
      this.isLoading = false;
    }
  }

  async saveChanges(): Promise<void> {
    try {
      const token = await this.authService.getToken();
      const response = await this.userService
          .updateUserProfile(this.userDetails, token)
          .toPromise();

      this.showSuccessMessage = true;

      setTimeout(() => {
        this.showSuccessMessage = false;
      }, 3000);
    } catch (error) {
      // @ts-ignore
      this.showErrorMessage = true;
      setTimeout(() => {
        this.showErrorMessage = false;
      }, 3000);
    }
  }


  logout(): void {
    this.authService.logout();
  }
}
