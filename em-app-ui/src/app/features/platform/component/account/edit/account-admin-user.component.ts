import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-edit-account-admin-user',
  templateUrl: './account-admin-user.component.html',
  standalone: false
})
export class EditAccountAdminUserFormComponent {
  @Input() form!: FormGroup;
}
