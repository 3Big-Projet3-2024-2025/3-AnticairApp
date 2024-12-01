import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http : HttpClient) { }

  async numberOfUsers(rawToken: string): Promise<number> {
    return firstValueFrom(
        this.http.get<number>("http://localhost:8080/api/users/nbrUsers", {
            headers: {
                Authorization: 'Bearer ' + rawToken,
            }
        })
    );
}
}
