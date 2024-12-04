import { Component, OnInit } from '@angular/core';
import { ThemeService } from '../../theme.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../../service/user.service';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent implements OnInit {

  currentTheme: 'dark' | 'light' = 'light';

  // Tables where users will be stored
  adminUsers: any[] = [];
  antiquarianUsers: any[] = [];
  basicUsers: any[] = [];

  // Table where users will be displayed
  displayedUsers: any[] = [];

  // Users type selection
  selectedUserType: string = 'basic'; // Default value

  constructor(
    private themeService: ThemeService, 
    private router: Router, 
    private dialog: MatDialog,
    private userService: UserService,
    private authService: AuthService
  ) { }

  ngOnInit() {
    // Subscribe to the theme service to get the current theme
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });

    // Initial loading of all users
    this.loadAllUsers();
  }

  // Load of all users
  private async loadAllUsers() {
    const token = this.authService.getToken();

    // Load the number of users
    this.userService.getAdminUsers(await token).subscribe({
      next: (users) => {
        this.adminUsers = users;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des utilisateurs administrateurs:', error);
      }
    });

    // Load the antiquarian users
    this.userService.getAntiquarianUsers(await token).subscribe({
      next: (users) => {
        this.antiquarianUsers = users;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des utilisateurs antiquaires:', error);
      }
    });
    

    // Load the basic users
    this.userService.getSimpleUsers(await token).subscribe({
      next: (users) => {
        this.basicUsers = users;
        // If the selected user type is basic, we display the basic users
        if (this.selectedUserType === 'basic') {
          this.displayedUsers = users;
        }
      },
      error: (error) => {
        console.error('Erreur lors du chargement de tous les utilisateurs:', error);
      }
    });
  }

  // Method to change the selected user type
  loadSelectedUsers() {
    switch (this.selectedUserType) {
      case 'admin':
        this.displayedUsers = this.adminUsers;
        break;
      case 'antiquarian':
        this.displayedUsers = this.antiquarianUsers;
        break;
      case 'basic':
      default:
        this.displayedUsers = this.basicUsers;
        break;
    }
  }
}