import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { HolderTypeLvo, EmailTypeLvo, PhoneTypeLvo, AddressTypeLvo } from '../../../api.platform.model';

@Component({
  selector: 'app-edit-principal-account-contact',
  templateUrl: './principal-account-contact.component.html',
  standalone: false
})
export class EditPrincipalAccountContactComponent {
  @Input() form!: FormGroup;
  
  holderTypes = Object.values(HolderTypeLvo);
  emailTypes = Object.values(EmailTypeLvo);
  phoneTypes = Object.values(PhoneTypeLvo);
  addressTypes = Object.values(AddressTypeLvo);
}
