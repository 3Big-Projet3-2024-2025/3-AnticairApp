import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private baseUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  private getHeaders(token: string): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });
  }

  getUserByEmail(token: string): Observable<any> {
    const headers = this.getHeaders(token);
    return this.http.get(`${this.baseUrl}`, { headers });
  }

  updateUserProfile(userData: any, token: string): Observable<any> {
    const headers = this.getHeaders(token);
    return this.http.put(`${this.baseUrl}/update`, userData, { headers });
  }
}
