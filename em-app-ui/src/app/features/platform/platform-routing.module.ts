import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountEditComponent } from './component/account/edit.component';
import { AccountIndexComponent } from './component/account/index.component';
import { ContactEditComponent } from './component/contact/edit.component';
import { ContactIndexComponent } from './component/contact/index.component';
import { GroupEditComponent } from './component/group/edit.component';
import { GroupIndexComponent } from './component/group/index.component';
import { RoleEditComponent } from './component/role/edit.component';
import { RoleIndexComponent } from './component/role/index.component';
import { UserEditComponent } from './component/user/edit.component';
import { UserIndexComponent } from './component/user/index.component';

const routes: Routes = [
  { path: '', redirectTo: 'users', pathMatch: 'full' },
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
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PlatformRoutingModule { }
