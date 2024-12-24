import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../app/auth.service';
import { map, Observable } from 'rxjs';
import { DtoImage } from '../modele/DtoImage';

@Injectable({
  providedIn: 'root'
})
export class ImageServiceService {

  constructor(private http: HttpClient, private authService: AuthService) {}

  getImageUrl(filePath: String): Observable<String>{
    const token = this.authService.getToken();
    return this.http.get<Blob>(`http://localhost:8080${filePath}`, {
      headers: { Authorization: `Bearer ${token}` },
      responseType: 'blob' as 'json'  // Convert the image to blob.
    }).pipe(
      map((blob: Blob | MediaSource) => URL.createObjectURL(blob))  // Convert Blob to url.
    );
  }

  getImageFile(filePath: String): Observable<Blob>{
    const token = this.authService.getToken();
    return this.http.get<Blob>(`http://localhost:8080${filePath}`, {
      headers: { Authorization: `Bearer ${token}` }, responseType: 'blob' as 'json'
    })
  }

  getImageFromAntiquity(antiquityId: number): Observable<string[]>{
    const token = this.authService.getToken();
    return this.http.get<string[]>(`http://localhost:8080/api/photoAntiquity/images/${antiquityId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
  }
}
