import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { SellComponent } from './sell/sell.component';
import { AdminGuard } from './guards/admin.guard';
import { DashboardComponent } from './admin/dashboard/dashboard.component';
import { UsersComponent } from './admin/users/users.component';
import { EditGroupsComponent } from './admin/users/edit-groups/edit-groups.component';
import { CreateListingComponent } from './create-listing/create-listing.component';
import {ManageUsersComponent} from './admin/users/manage-users/manage-users.component';
import { EditListingComponent } from './edit-listing/edit-listing.component';

import {ProfileComponent} from './profile/profile.component';

import { editAntiquityGuard } from './edit-antiquity.guard';
import { OneAntiquityComponent } from './one-antiquity/one-antiquity.component';
import { PaymentConfComponent } from './payment-conf/payment-conf.component';
import { BuyListingComponent } from './buy-listing/buy-listing.component';
import {isLoginGuard} from './guards/is-login.guard';
import {ListListingVerifyComponent} from './list-listing-verify/list-listing-verify.component';
import {isAntiquarianGuard} from './guards/is-antiquarian.guard';


const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [isLoginGuard] },
  { path: 'sell', component: SellComponent },
  { path: 'sell/:id', component: BuyListingComponent },
  { path: 'payment/success', component: PaymentConfComponent },
  { path: 'payment/error', component: PaymentConfComponent },
  { path: 'create-listing', component: CreateListingComponent },
  { path: 'admin', redirectTo: '/admin/dashboard', pathMatch: 'full'},
  { path: 'admin/dashboard', component: DashboardComponent, canActivate: [AdminGuard] },
  { path: 'admin/users', component: UsersComponent, canActivate: [AdminGuard] },
  { path: 'admin/users/edit-groups/:email', component: EditGroupsComponent, canActivate: [AdminGuard] },
  { path: 'admin/manage-users', component: ManageUsersComponent,},
  { path: 'edit/:id', component: EditListingComponent, canActivate: [editAntiquityGuard]},
  { path:'list-antiquity-verify', component: ListListingVerifyComponent, canActivate: [isAntiquarianGuard]},
  { path: '**', redirectTo: '/home' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
