import { Component } from '@angular/core';
import { ThemeService } from '../theme.service';

@Component({
  selector: 'app-sell',
  templateUrl: './sell.component.html',
  styleUrl: './sell.component.css'
})
export class SellComponent {

  currentTheme: 'dark' | 'light' = 'light'; // Actual theme, by default light
  constructor(private themeService: ThemeService) {}

  ngOnInit(): void {
    // Subscribe to Theme event
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });

  }

}
