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

  // Sorting properties
  currentSortColumn: string = 'email'; // Default value
  isSortAscending: boolean = true;

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
    let usersToSort: any[];
    switch (this.selectedUserType) {
      case 'admin':
        usersToSort = this.adminUsers;
        break;
      case 'antiquarian':
        usersToSort = this.antiquarianUsers;
        break;
      case 'basic':
      default:
        usersToSort = this.basicUsers;
        break;
    }

    // If a sort column is set, apply sorting
    if (this.currentSortColumn) {
      this.displayedUsers = this.sortUsers(usersToSort, this.currentSortColumn, this.isSortAscending);
    } else {
      this.displayedUsers = usersToSort;
    }
  }

  // Sorting method
  sortByColumn(column: string) {
    // If clicking the same column, toggle sort direction
    if (this.currentSortColumn === column) {
      this.isSortAscending = !this.isSortAscending;
    } else {
      // If sorting a new column, default to ascending
      this.currentSortColumn = column;
      this.isSortAscending = true;
    }

    // Apply sorting to current user type
    this.loadSelectedUsers();
  }

  // Helper method to sort users
  private sortUsers(users: any[], column: string, ascending: boolean): any[] {
    return users.sort((a, b) => {
      let valueA: any, valueB: any;

      // Handle nested attributes for specific columns
      if (column === 'phoneNumber') {
        valueA = a.attributes.phoneNumber;
        valueB = b.attributes.phoneNumber;
      } else if (column === 'homeAddress') {
        valueA = a.attributes.homeAddress;
        valueB = b.attributes.homeAddress;
      } else {
        valueA = a[column];
        valueB = b[column];
      }

      // Handle string comparison
      if (typeof valueA === 'string') {
        return ascending 
          ? valueA.localeCompare(valueB) 
          : valueB.localeCompare(valueA);
      }

      // Handle numeric comparison
      return ascending 
        ? (valueA - valueB) 
        : (valueB - valueA);
    });
  }
}