import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  constructor(private http : HttpClient) { }

  async addGroupByEmail(emailId: string, groupName: string, rawToken : string): Promise<void> {
    try {
      const response = await firstValueFrom(
        this.http.post<void>(`http://localhost:8080/api/groups/add?emailId=${emailId}&groupName=${groupName}`, {}, {
          headers: {
            'Authorization': 'Bearer ' + rawToken,
          }
        })
      );
    } catch (error) {
      console.error('Erreur lors de l\'ajout au groupe:', error);
      // Affichez un message d'erreur ou gérez l'erreur ici
    }
  }
}
