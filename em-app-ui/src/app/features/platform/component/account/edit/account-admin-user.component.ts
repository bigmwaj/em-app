import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-edit-account-admin-user',
  templateUrl: './account-admin-user.component.html',
  styleUrls: ['./account-admin-user.component.scss'],
  standalone: false
})
export class EditAccountAdminUserFormComponent {
  @Input() mainUserForm!: FormGroup;
}
