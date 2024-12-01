import { Component } from '@angular/core';
import { ThemeService } from './theme.service';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'anticairapp';
  isAdminRoute = false;

  currentTheme: 'dark' | 'light' = 'light'; // Current theme by default Light
  constructor(private themeService: ThemeService, private router: Router) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.checkAdminRoute();
    });
  }

  ngOnInit(): void {
    // Subscribe to Theme event
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });
  }

  checkAdminRoute(){
    this.isAdminRoute = this.router.url.includes('admin');
  }
}
