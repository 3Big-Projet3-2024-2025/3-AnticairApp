import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GroupService } from '../../../group.service';

@Component({
  selector: 'app-edit-groups',
  templateUrl: './edit-groups.component.html',
  styleUrl: './edit-groups.component.css'
})
export class EditGroupsComponent {

  userEmail: string = ''; // Email of the user
  // List of groups
  groups: { name: string; selected: boolean }[] = [
    { name: 'Admin', selected: false },
    { name: 'Antiquarian', selected: false },
  ];

  constructor(private route : ActivatedRoute, private router : Router, private groupsService : GroupService) { }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.userEmail = params.get('email') || ''; // We fetch the email from the URL
    });
  }

  saveGroups() {
    const selectedGroups = this.groups
      .filter((group) => group.selected)
      .map((group) => group.name);
  
    if (!selectedGroups.length) {
      alert('Please select at least one group.');
      return;
    }
  
    // Call the API for each selected group
    const requests = selectedGroups.map((groupName) => {
      const observable = this.groupsService.addGroupToUser(this.userEmail, groupName);
      if (!observable) {
        console.error(`Failed to create request for group: ${groupName}`);
        return Promise.resolve();
      }
      return observable.toPromise();
    });
  }
  

}
