import { Injectable } from '@angular/core';
import { 
  CanActivate, 
  ActivatedRouteSnapshot, 
  RouterStateSnapshot, 
  Router 
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { AuthService } from './auth.service'; // Assume you have an AuthService

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    // Check if the user is logged in and has admin role
    if (this.authService.isLoggedIn() && this.authService.isAdmin()) {
      return of(true);
    }

    this.router.navigate(['/home'], {});

    return of(false);
  }
}