import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = '/api/admin';

  constructor(private http: HttpClient) {}

  getUsers(): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/users/list`);
  }

  forcePasswordReset(userId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/force-password-reset/${userId}`, {});
  }
}
