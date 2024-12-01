import { Component } from '@angular/core';
import { ThemeService } from '../../theme.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent {

  currentTheme: 'dark' | 'light' = 'light'; // Actual theme, by default light

  constructor(private themeService : ThemeService) { }


  ngOnInit(){
    // Subscribe to Theme event
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });
  }
}
