import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {NgClass, NgIf} from "@angular/common";
import { ThemeService } from '../../service/theme.service';
import { AuthService } from '../../service/auth.service';
import { UserService } from '../../service/user.service';
import {RgpdService} from '../../service/rgpd.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  standalone: true,
<<<<<<< Updated upstream
  imports: [FormsModule, NgIf, NgClass],
=======
  imports: [FormsModule, NgIf, CurrencyPipe, NgForOf],
>>>>>>> Stashed changes
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
      private themeService: ThemeService,
      private rgpdService: RgpdService,
      private router : Router,
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

  async deleteData() {
    const bool = confirm("Deleting user data = account disabled");
    if (bool) {
      const token = await this.authService.getToken().then(res => {
        this.rgpdService.updateUserProfile(this.userDetails, res).subscribe(res => {
          console.log(res);

          this.logout();


        });
      });



    }
  }
}
