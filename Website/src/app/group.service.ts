import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  private apiUrl = 'http://localhost:8080/api/groups/';


  constructor(private http: HttpClient) { }

  addGroupToUser(emailId: string, groupName: string) {
    const params = new HttpParams()
      .set('emailId', emailId)
      .set('groupName', groupName);

    return this.http.post(this.apiUrl + 'add', null, { params });
  }

}
