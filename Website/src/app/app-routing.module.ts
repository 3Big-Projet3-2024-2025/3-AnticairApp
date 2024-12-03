import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { SellComponent } from './sell/sell.component';
import { AdminGuard } from './admin.guard';
import { DashboardComponent } from './admin/dashboard/dashboard.component';
import { UsersComponent } from './admin/users/users.component';
import { EditGroupsComponent } from './admin/users/edit-groups/edit-groups.component';
import {ManageUsersComponent} from './admin/users/manage-users/manage-users.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'sell', component: SellComponent },
  { path: 'admin', redirectTo: '/admin/dashboard', pathMatch: 'full'},
  { path: 'admin/dashboard', component: DashboardComponent, canActivate: [AdminGuard] },
  { path: 'admin/users', component: UsersComponent, canActivate: [AdminGuard] },
  { path: 'admin/users/edit-groups/:email', component: EditGroupsComponent, canActivate: [AdminGuard] },
  { path: 'admin/manage-users', component: ManageUsersComponent }, // Route pour le composant Admin
  { path: '**', redirectTo: '/home' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
