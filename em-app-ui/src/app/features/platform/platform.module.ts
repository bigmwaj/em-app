import { NgModule } from "@angular/core";
import { Routes } from "@angular/router";
import { AuthGuard } from "../../core/guards/auth.guard";
import { ChangeStatusDialogComponent } from "../shared/component/change-status-dialog.component";
import { DeleteDialogComponent } from "../shared/component/delete-dialog.component";
import { SearchFormComponent } from "../shared/component/search-form.component";
import { AccountEditComponent } from "./component/account/edit.component";
import { EditAccountAdminUserFormComponent } from "./component/account/edit/account-admin-user.component";
import { EditAccountDetailsComponent } from "./component/account/edit/account-details.component";
import { EditPrincipalAccountContactComponent } from "./component/account/edit/principal-account-contact.component";
import { AccountIndexComponent } from "./component/account/index.component";
import { AddressListComponent } from "./component/contact/address/list.component";
import { ContactEditComponent } from "./component/contact/edit.component";
import { EmailListComponent } from "./component/contact/email/list.component";
import { ContactIndexComponent } from "./component/contact/index.component";
import { PhoneListComponent } from "./component/contact/phone/list.component";
import { GroupEditComponent } from "./component/group/edit.component";
import { GroupIndexComponent } from "./component/group/index.component";
import { GroupRoleAssignListComponent } from "./component/group/role/assign.list.component";
import { GroupRoleAssignedListComponent } from "./component/group/role/assigned.list.component";
import { RoleEditComponent } from "./component/role/edit.component";
import { RoleIndexComponent } from "./component/role/index.component";
import { RolePrivilegeAssignListComponent } from "./component/role/privilege/assign.list.component";
import { RolePrivilegeAssignedListComponent } from "./component/role/privilege/assigned.list.component";
import { SharedUserAssignListComponent } from "./component/shared/user/assign.list.component";
import { SharedUserAssignedListComponent } from "./component/shared/user/assigned.list.component";
import { UserEditComponent } from "./component/user/edit.component";
import { UserIndexComponent } from "./component/user/index.component";
import { CommonModule } from "@angular/common";

const routes: Routes = [

  // Protected routes with layout
  {
    path: 'platform',
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'users', component: UserIndexComponent },
      { path: 'users/edit/:mode', component: UserEditComponent },
      { path: 'accounts', component: AccountIndexComponent },
      { path: 'accounts/edit/:mode', component: AccountEditComponent },
      { path: 'contacts', component: ContactIndexComponent },
      { path: 'contacts/edit/:mode', component: ContactEditComponent },
      { path: 'groups', component: GroupIndexComponent },
      { path: 'groups/edit/:mode', component: GroupEditComponent },
      { path: 'roles', component: RoleIndexComponent },
      { path: 'roles/edit/:mode', component: RoleEditComponent },
    ]
  },
];

@NgModule({
  declarations: [
    UserIndexComponent,
    UserEditComponent,
    AccountIndexComponent,
    AccountEditComponent,
    EditAccountDetailsComponent,
    EditPrincipalAccountContactComponent,
    EditAccountAdminUserFormComponent,
    ContactIndexComponent,
    ContactEditComponent,
    GroupIndexComponent,
    GroupEditComponent,
    RoleIndexComponent,
    RoleEditComponent,
    ChangeStatusDialogComponent,
    DeleteDialogComponent,
    PhoneListComponent,
    EmailListComponent,
    AddressListComponent,
    SearchFormComponent,
    RolePrivilegeAssignedListComponent,
    RolePrivilegeAssignListComponent,
    GroupRoleAssignedListComponent,
    GroupRoleAssignListComponent,
    SharedUserAssignedListComponent,
    SharedUserAssignListComponent,
  ],
  imports: [
    CommonModule
  ],
  exports: [
    UserIndexComponent,
    RoleIndexComponent,
  ]
})
export class PlatformModule { }