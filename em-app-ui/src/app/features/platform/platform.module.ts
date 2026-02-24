import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatDialogModule } from "@angular/material/dialog";
import { MatDividerModule } from "@angular/material/divider";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatMenuModule } from "@angular/material/menu";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatSelectModule } from "@angular/material/select";
import { MatSortModule } from "@angular/material/sort";
import { MatTableModule } from "@angular/material/table";
import { MatTabsModule } from "@angular/material/tabs";
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
import { PlatformRoutingModule } from "./platform-routing.module";
import { LoadingComponent } from "../shared/component/loading.component";
import { MessageComponent } from "../shared/component/message.component";

@NgModule({
  declarations: [
    MessageComponent,
    LoadingComponent,
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
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatDialogModule,
    MatDividerModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatMenuModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSortModule,
    MatTableModule,
    MatTabsModule,
    PlatformRoutingModule
  ]
})
export class PlatformModule { }