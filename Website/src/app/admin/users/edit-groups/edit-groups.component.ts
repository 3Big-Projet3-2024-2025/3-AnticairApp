import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-edit-groups',
  templateUrl: './edit-groups.component.html',
  styleUrl: './edit-groups.component.css'
})
export class EditGroupsComponent {

  userEmail: string = ''; // Email of the user

  constructor(private route : ActivatedRoute) { }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.userEmail = params.get('email') || ''; // We fetch the email from the URL
    });
  }

  saveGroups(){
    // Save the selected groups for the user
  }

}
