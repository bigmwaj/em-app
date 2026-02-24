import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ContactService } from '../../service/contact.service';
import {
  ContactDto,
  OwnerTypeLvo,
  EmailTypeLvo,
  PhoneTypeLvo,
  AddressTypeLvo,
} from '../../api.platform.model';
import { SharedHelper } from '../../../shared/shared.helper';
import { COUNTRIES } from '../../constants/country.constants';
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';
import { ContactHelper } from '../../helper/contact.helper';

@Component({
  selector: 'app-contact-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false,
})
export class ContactEditComponent extends AbstractEditComponent<ContactDto> {

  ContactHelper = ContactHelper;

  // Enums for dropdowns
  OwnerTypeLvo = OwnerTypeLvo;
  EmailTypeLvo = EmailTypeLvo;
  PhoneTypeLvo = PhoneTypeLvo;
  AddressTypeLvo = AddressTypeLvo;

  // Constants
  readonly countries = COUNTRIES;

  constructor(
    protected override fb: FormBuilder,
    protected override router: Router,
    protected override route: ActivatedRoute,
    protected override dialog: MatDialog,
    private service: ContactService,
  ) {
    super(fb, router, route, dialog);

    this.delete = (dto) => this.service.deleteContact(dto);
    this.create = (dto) => this.service.createContact(dto);
    this.update = (dto) => this.service.updateContact(dto);
    this.buildFormData = (dto) => ContactHelper.buildFormData(dto);
  }

  protected override getBaseRoute(): string {
    return '/platform/contacts';
  }

  protected override initializeForms(): FormGroup[] {
    this.mainForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      birthDate: [''],
      ownerType: [OwnerTypeLvo.ACCOUNT, Validators.required],
      defaultEmail: [''],
      defaultEmailType: [EmailTypeLvo.WORK],
      defaultPhone: [''],
      defaultPhoneType: [PhoneTypeLvo.WORK],
      defaultAddress: [''],
      defaultAddressType: [AddressTypeLvo.WORK],
      country: [''],
      region: [''],
      city: [''],
    });

    // Make country required when address is provided
    this.mainForm
      .get('defaultAddress')
      ?.valueChanges/*.pipe(takeUntil(this.destroy$))*/
      .subscribe((address) => {
        const countryControl = this.mainForm.get('country');
        if (address && address.trim()) {
          countryControl?.setValidators([Validators.required]);
        } else {
          countryControl?.clearValidators();
        }
        countryControl?.updateValueAndValidity();
      });

    return [this.mainForm];
  }

  protected populateForms(): void {
    const defaultEmail = ContactHelper.getDefaultContactEmail(this.dto!);
    const defaultPhone = ContactHelper.getDefaultContactPhone(this.dto!);
    const defaultAddress = ContactHelper.getDefaultContactAddress(this.dto!);

    this.mainForm.patchValue({
      firstName: this.dto!.firstName,
      lastName: this.dto!.lastName,
      birthDate: this.dto!.birthDate,
      ownerType: this.dto!.ownerType,
      defaultEmail: defaultEmail?.email || '',
      defaultEmailType: defaultEmail?.type || EmailTypeLvo.WORK,
      defaultPhone: defaultPhone?.phone || '',
      defaultPhoneType: defaultPhone?.type || PhoneTypeLvo.WORK,
      defaultAddress: defaultAddress?.address || '',
      defaultAddressType: defaultAddress?.type || AddressTypeLvo.WORK,
      country: defaultAddress?.country || '',
      region: defaultAddress?.region || '',
      city: defaultAddress?.city || '',
    });
  }

  protected buildDtoFromForms(): ContactDto {
    const formValue = this.mainForm.value;

    const contactDto: ContactDto = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      birthDate: formValue.birthDate,
      ownerType: formValue.ownerType,
    };

    // Add ID if editing
    if (this.isEditMode && this.dto?.id) {
      contactDto.id = this.dto.id;
    }

    // Build emails array
    if (formValue.defaultEmail) {
      contactDto.emails = [
        {
          email: formValue.defaultEmail,
          type: formValue.defaultEmailType,
          ownerType: formValue.ownerType,
          defaultContactPoint: true,
        },
      ];

      // Preserve ID if editing
      if (this.isEditMode) {
        const existingEmail = ContactHelper.getDefaultContactEmail(this.dto!);
        if (existingEmail?.id) {
          contactDto.emails[0].id = existingEmail.id;
          contactDto.emails[0].contactId = this.dto!.id;
        }
      }
    }

    // Build phones array
    if (formValue.defaultPhone) {
      contactDto.phones = [
        {
          phone: formValue.defaultPhone,
          type: formValue.defaultPhoneType,
          ownerType: formValue.ownerType,
          defaultContactPoint: true,
        },
      ];

      // Preserve ID if editing
      if (this.isEditMode) {
        const existingPhone = ContactHelper.getDefaultContactPhone(this.dto!);
        if (existingPhone?.id) {
          contactDto.phones[0].id = existingPhone.id;
          contactDto.phones[0].contactId = this.dto!.id;
        }
      }
    }

    // Build addresses array
    if (formValue.defaultAddress) {
      contactDto.addresses = [
        {
          address: formValue.defaultAddress,
          type: formValue.defaultAddressType,
          ownerType: formValue.ownerType,
          defaultContactPoint: true,
          country: formValue.country,
          region: formValue.region,
          city: formValue.city,
        },
      ];

      // Preserve ID if editing
      if (this.isEditMode) {
        const existingAddress = ContactHelper.getDefaultContactAddress(this.dto!);
        if (existingAddress?.id) {
          contactDto.addresses[0].id = existingAddress.id;
          contactDto.addresses[0].contactId = this.dto!.id;
        }
      }
    }

    return contactDto;
  }
}
