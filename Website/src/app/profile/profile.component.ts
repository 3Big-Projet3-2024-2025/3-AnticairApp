import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { UserService } from '../user.service';
import { FormsModule } from '@angular/forms';
import {NgClass, NgIf} from "@angular/common";

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

  constructor(
      private authService: AuthService,
      private userService: UserService
  ) {}

  async ngOnInit() {
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
