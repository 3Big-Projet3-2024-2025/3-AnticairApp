import {Component, OnInit} from '@angular/core';
import {AdminService} from '../../../service/admin.service';

@Component({
  selector: 'app-manage-users',
  templateUrl: './manage-users.component.html',
  styleUrl: './manage-users.component.css'
})
export class ManageUsersComponent implements OnInit {
  users: any[] = [];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.users = [
      { id: 'id1', name: 'John Doe', role: 'User' },
      { id: 'id2', name: 'Jane Smith', role: 'Admin' }
    ];
  }

  forcePasswordReset(userId: string): void {
    this.adminService.forcePasswordReset(userId).subscribe(() => {
      alert('Le lien de réinitialisation de mot de passe a été envoyé.');
    });
  }
}
