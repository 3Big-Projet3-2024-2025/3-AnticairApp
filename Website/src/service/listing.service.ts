import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Antiquity } from '../modele/DtoListing';
import { AuthService } from './auth.service';

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

  getAntiquityById(id: string):Observable<Antiquity>{
    return this.http.get<Antiquity>(this.privateUrl + '/' + id);
  }

  updateAntiquityWithPhotos(id: number, antiquity: Antiquity, images?: File[]): Observable<any> {
    const formData = new FormData();

    // Convertir l'antiquité en JSON string
    formData.append('antiquity', JSON.stringify(antiquity));

    // Ajouter les images si présentes
    if (images && images.length > 0) {
      images.forEach((file, index) => {
        formData.append('images', file, file.name);
      });
    }

    return this.http.put<any>(`${this.privateUrl}/${id}`, formData);
  }

  getAllAntiquitiesChecked() : Observable<Antiquity[]>{
    return this.http.get<Antiquity[]>(this.privateUrl + '/checked');
  }

  buyAntiquity(id: number): Observable<string> {
    // Get the token from the authentication service
    const rawToken = this.authService.getToken();

    // Configure the headers with the token
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${rawToken}`
    });

    return this.http.post(`${this.privateUrl}/${id}/buy`, null, { headers, responseType: 'text' });
  }

  executePayment(paymentId: string, payerId: string): Observable<any> {
    // Get the token from the authentication service
    const rawToken = this.authService.getToken();

    // Configure the headers with the token
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${rawToken}`
    });

    return this.http.get<any>(`${this.privateUrl}/payment/execute`, { headers, params: { paymentId, PayerID: payerId } });
  }

  acceptAntiquity(antiquity : Antiquity) : Observable<Map<String,String>>{
    const formData = new FormData();
    formData.append('id', JSON.stringify(antiquity.idAntiquity));
     // Get the token from the authentication service
     const rawToken = this.authService.getToken();
    // Configure the headers with the token
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${rawToken}`
    });
    return this.http.put<any>(`${this.privateUrl}/acceptAntiquity`, formData, { headers});
  }


  rejectAntiquity(antiquity: Antiquity, note_title: string, note_description: string, note_price: string, note_photo: string): Observable<Map<string, string>> {
    const formData = new FormData();

    formData.append('note_title', JSON.stringify(note_title));
    formData.append('note_description', JSON.stringify(note_description));
    formData.append('note_price', JSON.stringify(note_price));
    formData.append('note_photo', JSON.stringify(note_photo));
    formData.append('id', JSON.stringify(antiquity.idAntiquity));

    // Get the token from the authentication service
    const rawToken = this.authService.getToken();

    // Configure the headers with the token
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${rawToken}`
    });

    // Envoi de la requête PUT
    return this.http.put<Map<string, string>>(`${this.privateUrl}/rejectAntiquity`,formData,{headers});
  }

  getListingVerify(mailAntiquarian: string):Observable<Antiquity[]>{
    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<Antiquity[]>(this.privateUrl + `/by-state?mailAntiquarian=${mailAntiquarian}`, {headers});
  }

  getListingSeller(mailSeller: string):Observable<Antiquity[]>{
    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<Antiquity[]>(this.privateUrl + `/byMailSeller?mailSeller=${mailSeller}`, {headers});
  }

  deleteById(id: number): Observable<Antiquity> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.put<Antiquity>(`${this.privateUrl}/isDisplay/${id}`, null, { headers});
  }
}
