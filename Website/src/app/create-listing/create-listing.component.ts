import { Component, OnInit } from '@angular/core';
import { ThemeService } from '../theme.service';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { ListingService } from '../../service/listing.service';

@Component({
  selector: 'app-create-listing',
  templateUrl: './create-listing.component.html',
  styleUrl: './create-listing.component.css'
})
export class CreateListingComponent {
  currentTheme: 'dark' | 'light' = 'light';

  email: string = '';
  title: string = '';
  description: string = '';
  price: number = 0;
  photos: File[] = [];

  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private themeService: ThemeService,
    private router: Router, 
    private authService: AuthService, 
    private listingService: ListingService
  ) {}

  ngOnInit() {
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });

    this.email = this.authService.getUserDetails().email;
    console.log(this.email);
  }

  // Method who manages the file selection
  onFileSelected(event: any) {
    this.photos = Array.from(event.target.files);
  }

  // Method who submits the form
  onSubmit() {
    // Reset the error and success messages
    this.errorMessage = '';
    this.successMessage = '';
    // Verify the field are not empty
    if (!this.title) {
      this.errorMessage = 'Title is required';
      return;
    }

    if (!this.description) {
      this.errorMessage = 'Description is required';
      return;
    }

    if (this.price <= 0) {
      this.errorMessage = 'Price must be greater than 0';
      return;
    }

    // Validation for Files
    if (this.photos && this.photos.length > 0) {
      for (let photo of this.photos) {
        if (!photo.name.toLowerCase().endsWith('.jpg')) {
          this.errorMessage = 'All files must be in .jpg format';
          return;
        }
    }
    }

    this.listingService.createListing(
      this.email,
      this.title, 
      this.description, 
      this.price, 
      this.photos,
    ).subscribe({
      next: (response) => {
        this.successMessage = 'Listing created successfully!';
        this.resetForm();
      },
      error: (error) => {
        // Gestion des erreurs
        if (error.status === 400) {
          this.errorMessage = 'Invalid input. Please check your data.';
        } else if (error.status === 401) {
          this.errorMessage = 'Unauthorized. Please log in again.';
        } else if (error.status === 500) {
          this.errorMessage = 'Server error. Please try again later.';
        } else if(error.status === 413) {
          this.errorMessage = 'File too large. Please upload files smaller';
        } else {
          this.errorMessage = error.message || 'An unexpected error occurred';
        }
      }
    });
  }

  // Method who resets the form
  resetForm() {
    this.title = '';
    this.description = '';
    this.price = 0;
    this.photos = [];
  }
}
