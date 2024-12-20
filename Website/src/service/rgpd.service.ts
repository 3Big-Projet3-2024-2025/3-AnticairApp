import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RgpdService {

  constructor(private http: HttpClient) { }
  private baseUrl = 'http://localhost:8080/api/rgpd';
  private getHeaders(token: string): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });
  }
  updateUserProfile(userData: any, token: string): Observable<any> {
      const headers = this.getHeaders(token);
      return this.http.put(`${this.baseUrl}/update`, userData, { headers });
    }
}
