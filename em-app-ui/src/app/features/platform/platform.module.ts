import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { DashboardComponent } from "../../core/component/dashboard/dashboard.component";
import { LayoutComponent } from "../../core/component/layout/layout.component";
import { AuthGuard } from "../../core/guards/auth.guard";
import { AccountEditComponent } from "./component/account/edit.component";
import { AccountIndexComponent } from "./component/account/index.component";
import { ContactEditComponent } from "./component/contact/edit.component";
import { ContactIndexComponent } from "./component/contact/index.component";
import { GroupEditComponent } from "./component/group/edit.component";
import { GroupIndexComponent } from "./component/group/index.component";
import { RoleEditComponent } from "./component/role/edit.component";
import { RoleIndexComponent } from "./component/role/index.component";
import { UserEditComponent } from "./component/user/edit.component";
import { UserIndexComponent } from "./component/user/index.component";

const routes: Routes = [

  // Protected routes with layout
  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'platform/users', component: UserIndexComponent },
      { path: 'platform/users/edit/:mode', component: UserEditComponent },
      { path: 'platform/accounts', component: AccountIndexComponent },
      { path: 'platform/accounts/edit/:mode', component: AccountEditComponent },
      { path: 'platform/contacts', component: ContactIndexComponent },
      { path: 'platform/contacts/edit/:mode', component: ContactEditComponent },
      { path: 'platform/groups', component: GroupIndexComponent },
      { path: 'platform/groups/edit/:mode', component: GroupEditComponent },
      { path: 'platform/roles', component: RoleIndexComponent },
      { path: 'platform/roles/edit/:mode', component: RoleEditComponent },
    ]
  },
  
  // Redirect any unknown routes to login
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class PlatformModule { }