import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../app/auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ListingService {
  // This is the URL to the API
  privateUrl = 'http://localhost:8080/api/listing';

  constructor(private http: HttpClient, private authService: AuthService) { }

  createListing(
    email: string,
    title: string,
    description: string,
    price: number,
    photos: File[]
  ): Observable<any> {
    // Create a new FormData object
    const formData = new FormData();

    // Append the data to the FormData object
    formData.append('email', email);
    formData.append('title', title);
    formData.append('description', description);
    formData.append('price', price.toString());

    // Append the photos to the FormData object if there are any
    if (photos && photos.length > 0) {
      photos.forEach((photo, index) => {
        formData.append('photos', photo, photo.name);
      });
    }
    // Get the token from the authentication service
    const rawToken = this.authService.getToken();

    // Configure the headers with the token
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${rawToken}` 
    });
    
    // Send the request to API
    return this.http.post(this.privateUrl + '/create', formData, { headers });
  }
}
