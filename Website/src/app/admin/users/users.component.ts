import { Component, OnInit } from '@angular/core';
import { ThemeService } from '../../../service/theme.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../../service/user.service';
import { AuthService } from '../../../service/auth.service';

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
  currentPage: number = 1;
  itemsPerPage: number = 3; // Adjust this value based on your requirements
  totalPages: number = 1;
  paginatedUsers: any[] = [];

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

  private async loadAllUsers() {
    const token = await this.authService.getToken();

    // Load all users as before...
    this.userService.getAdminUsers(await token).subscribe({
      next: (users) => {
        this.adminUsers = users;
        this.calculateTotalPages(users);
      },
      error: (error) => {
        console.error('Error loading admin users:', error);
      }
    });

    this.userService.getAntiquarianUsers(await token).subscribe({
      next: (users) => {
        this.antiquarianUsers = users;
        this.calculateTotalPages(users);
      },
      error: (error) => {
        console.error('Error loading antiquarian users:', error);
      }
    });

    this.userService.getSimpleUsers(await token).subscribe({
      next: (users) => {
        this.basicUsers = users;
        this.calculateTotalPages(users);
        if (this.selectedUserType === 'basic') {
          this.displayedUsers = users;
          this.paginateUsers();
        }
      },
      error: (error) => {
        console.error('Error loading basic users:', error);
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

    this.changePage(1);
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
      } else if (column === 'balance') {
        valueA = a.attributes.balance;
        valueB = b.attributes.balance;
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

  // Method to change the user status (Enabled to Disabled and vice versa)
  async changeUserStatus(emailid: string, status: string) {
    const token = await this.authService.getToken();
  
    if (status === 'Disabled') {
      this.userService.enableUser(token, emailid).subscribe({
        next: (response) => {
          console.log('User enabled successfully:', response);
          // Mettre à jour l'utilisateur dans la bonne liste
          this.updateUserStatus(emailid, 'Enabled');
        },
        error: (error) => {
          console.error('Error enabling user:', error);
        }
      });
    } else {
      this.userService.disableUser(token, emailid).subscribe({
        next: (response) => {
          console.log('User disabled successfully:', response);
          // Mettre à jour l'utilisateur dans la bonne liste
          this.updateUserStatus(emailid, 'Disabled');
        },
        error: (error) => {
          console.error('Error disabling user:', error);
        }
      });
    }
  }

  // Method to update the status of a user in the different lists

  private updateUserStatus(emailid: string, newStatus: string) {
    // Trouver l'utilisateur dans les différentes listes
    const updateUserStatusInList = (users: any[], emailid: string) => {
      const user = users.find(user => user.email === emailid);
      if (user) {
        user.enabled = (newStatus === 'Enabled');
      }
    };
  
    // Mettre à jour dans chaque liste spécifique
    updateUserStatusInList(this.adminUsers, emailid);
    updateUserStatusInList(this.antiquarianUsers, emailid);
    updateUserStatusInList(this.basicUsers, emailid);
  
    // Charger à nouveau les utilisateurs filtrés selon le type sélectionné
    this.loadSelectedUsers();
  }

  // Method to calculate total pages
  calculateTotalPages(users: any[]) {
    this.totalPages = Math.ceil(users.length / this.itemsPerPage);
    this.paginateUsers();
  }

  // Method to paginate the users based on the current page
  paginateUsers() {
    let usersToDisplay: any[];

    switch (this.selectedUserType) {
      case 'admin':
        usersToDisplay = this.adminUsers;
        break;
      case 'antiquarian':
        usersToDisplay = this.antiquarianUsers;
        break;
      case 'basic':
      default:
        usersToDisplay = this.basicUsers;
        break;
    }

    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;

    this.paginatedUsers = usersToDisplay.slice(startIndex, endIndex);
  }

  // Method to change the page
  changePage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.paginateUsers();
    }
  }
  
  

}