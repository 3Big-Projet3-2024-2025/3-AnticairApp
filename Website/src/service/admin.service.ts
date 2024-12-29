import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = '/api/admin';
  private readonly KEYCLOAK_URL = 'http://localhost:8081/realms/anticairapp';


  constructor(private http: HttpClient) {}

  getUsers(): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/users/list`);
  }

  /*forcePasswordReset(userId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/force-password-reset/${userId}`, {});
  }*/
  forcePasswordReset(userId: string): Observable<any> {
    return this.http.put(`${this.KEYCLOAK_URL}/users/${userId}`, {
      requiredActions: ['UPDATE_PASSWORD']
    });
  }
}
