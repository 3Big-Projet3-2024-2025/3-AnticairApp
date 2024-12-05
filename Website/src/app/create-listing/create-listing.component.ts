import { Component, OnInit } from '@angular/core';
import { ThemeService } from '../theme.service';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

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
  price: number | null = null;
  photos: File[] = [];

  constructor(
    private themeService: ThemeService,
    private router: Router, 
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });
  }

}
