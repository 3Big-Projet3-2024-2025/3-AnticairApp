import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  private apiUrl = 'http://localhost:8080/api/groups';


  constructor(private http: HttpClient, private authService: AuthService) { }

  addGroupToUser(emailId: string, groupName: string): Observable<any> {
    return new Observable((observer) => {
      // Retrieve the token
      this.authService.getToken().then((token: string) => {
        // Check if the token exists
        if (!token) {
          observer.error('No token found');
          return;
        }
        const headers = new HttpHeaders({
          'Authorization': `Bearer ${token}`,
        });

        // Set query parameters
        const params = new HttpParams()
          .set('emailId', emailId)
          .set('groupName', groupName);

        // Send POST request with params and headers
        this.http.post(`${this.apiUrl}/add`, null, { params, headers })
          .subscribe({
            next: (response) => {
              observer.next(response);  // Notify the success
              observer.complete();  // Complete the observable stream
            },
            error: (error) => {
              observer.error(error);  // Notify the error
            }
          });

      }).catch((error) => {
        observer.error(error);  // Notify the error in case of failure in token retrieval
      });
    });
  }

}
