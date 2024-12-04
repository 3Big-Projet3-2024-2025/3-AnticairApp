import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { firstValueFrom, Observable } from 'rxjs';

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

getAdminUsers(rawToken: string): Observable<any[]> {
  return this.http.get<any[]>("http://localhost:8080/api/users/list/admin", {
    headers: {
      Authorization: 'Bearer ' + rawToken,
    }
  });
}

getAntiquarianUsers(rawToken: string): Observable<any[]> {
  return this.http.get<any[]>("http://localhost:8080/api/users/list/antiquarian", {
    headers: {
      Authorization: 'Bearer ' + rawToken,
    }
  });
}

getSimpleUsers(rawToken: string): Observable<any[]> {
  return this.http.get<any[]>("http://localhost:8080/api/users/list", {
    headers: {
      Authorization: 'Bearer ' + rawToken,
    }
  });
}
 
}