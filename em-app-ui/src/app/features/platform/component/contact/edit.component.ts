import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ContactService } from '../../service/contact.service';
import {
  ContactDto,
  HolderTypeLvo,
  EmailTypeLvo,
  PhoneTypeLvo,
  AddressTypeLvo,
} from '../../api.platform.model';
import { PlatformHelper } from '../../platform.helper';
import { SharedHelper } from '../../../shared/shared.helper';
import { COUNTRIES } from '../../constants/country.constants';
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';

@Component({
  selector: 'app-contact-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false,
})
export class ContactEditComponent extends AbstractEditComponent<ContactDto> {

  contactForm!: FormGroup;

  // Enums for dropdowns
  ContactEditMode = SharedHelper.EditMode;
  HolderTypeLvo = HolderTypeLvo;
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
  }

  get isInvalidForm(): boolean {
    return this.contactForm.invalid;
  }

  protected override getBaseRoute(): string {
    return '/contacts';
  }

  protected duplicate(): ContactDto {
    if (!this.dto) {
      throw new Error('No contact data to duplicate');
    }
    return PlatformHelper.duplicateContact(this.dto)
  }

  protected disableAllForms(): void {
    this.contactForm.disable();
  }

  protected enableAllForms(): void {
    this.contactForm.enable();
  }

  protected override setupCreateMode(): void {
    // Initialize with default values for create mode
    this.contactForm.patchValue({
      holderType: HolderTypeLvo.ACCOUNT,
      defaultEmailType: EmailTypeLvo.WORK,
      defaultPhoneType: PhoneTypeLvo.WORK,
      defaultAddressType: AddressTypeLvo.WORK
    });
  }

  protected initializeForms(): void {
    this.contactForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      birthDate: [''],
      holderType: [HolderTypeLvo.ACCOUNT, Validators.required],
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
    this.contactForm
      .get('defaultAddress')
      ?.valueChanges/*.pipe(takeUntil(this.destroy$))*/
      .subscribe((address) => {
        const countryControl = this.contactForm.get('country');
        if (address && address.trim()) {
          countryControl?.setValidators([Validators.required]);
        } else {
          countryControl?.clearValidators();
        }
        countryControl?.updateValueAndValidity();
      });
  }

  protected populateForms(): void {
    const defaultEmail = PlatformHelper.getDefaultContactEmail(this.dto!);
    const defaultPhone = PlatformHelper.getDefaultContactPhone(this.dto!);
    const defaultAddress = PlatformHelper.getDefaultContactAddress(this.dto!);

    this.contactForm.patchValue({
      firstName: this.dto!.firstName,
      lastName: this.dto!.lastName,
      birthDate: this.dto!.birthDate,
      holderType: this.dto!.holderType,
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
    const formValue = this.contactForm.value;

    const contactDto: ContactDto = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      birthDate: formValue.birthDate,
      holderType: formValue.holderType,
    };

    // Add ID if editing
    if (this.mode === SharedHelper.EditMode.EDIT && this.dto?.id) {
      contactDto.id = this.dto.id;
    }

    // Build emails array
    if (formValue.defaultEmail) {
      contactDto.emails = [
        {
          email: formValue.defaultEmail,
          type: formValue.defaultEmailType,
          holderType: formValue.holderType,
          defaultContactPoint: true,
        },
      ];

      // Preserve ID if editing
      if (this.mode === SharedHelper.EditMode.EDIT) {
        const existingEmail = PlatformHelper.getDefaultContactEmail(this.dto!);
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
          holderType: formValue.holderType,
          defaultContactPoint: true,
        },
      ];

      // Preserve ID if editing
      if (this.mode === SharedHelper.EditMode.EDIT) {
        const existingPhone = PlatformHelper.getDefaultContactPhone(this.dto!);
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
          holderType: formValue.holderType,
          defaultContactPoint: true,
          country: formValue.country,
          region: formValue.region,
          city: formValue.city,
        },
      ];

      // Preserve ID if editing
      if (this.mode === SharedHelper.EditMode.EDIT) {
        const existingAddress = PlatformHelper.getDefaultContactAddress(this.dto!);
        if (existingAddress?.id) {
          contactDto.addresses[0].id = existingAddress.id;
          contactDto.addresses[0].contactId = this.dto!.id;
        }
      }
    }

    return contactDto;
  }
}
