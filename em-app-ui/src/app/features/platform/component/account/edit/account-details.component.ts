import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { AccountStatusLvo } from '../../../api.platform.model';

@Component({
  selector: 'app-edit-account-details',
  templateUrl: './account-details.component.html',
  styleUrls: ['./account-details.component.scss'],
  standalone: false
})
export class EditAccountDetailsComponent {
  @Input() accountForm!: FormGroup;
  
  accountStatuses = Object.values(AccountStatusLvo);
}
