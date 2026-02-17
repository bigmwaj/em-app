import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AccountService } from '../../service/account.service';
import {
  AccountDto,
  AccountStatusLvo,
  HolderTypeLvo,
  EmailTypeLvo,
  PhoneTypeLvo,
  AddressTypeLvo,
  UsernameTypeLvo,
  ContactDto,
  ContactEmailDto,
  ContactPhoneDto,
  ContactAddressDto,
  AccountContactDto,
  AccountContactRoleLvo,
} from '../../api.platform.model';
import { PlatformHelper } from '../../platform.helper';
import { AbstractEditWithStatusComponent } from '../../../shared/component/abstract-edit-with-status.component';

@Component({
  selector: 'app-account-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false
})
export class AccountEditComponent extends AbstractEditWithStatusComponent<AccountDto, AccountStatusLvo> {
  accountForm!: FormGroup;
  primaryAccountContactForm!: FormGroup;
  adminUserForm!: FormGroup;

  constructor(
    protected override fb: FormBuilder,
    protected override router: Router,
    protected override route: ActivatedRoute,
    protected override dialog: MatDialog,
    private service: AccountService
  ) {
    super(fb, router, route, dialog);
    this.delete = (dto) => this.service.deleteAccount(dto);
    this.changeStatus = (dto) => this.service.updateAccount(dto as AccountDto);
    this.create = (dto) => this.service.createAccount(dto);
    this.update = (dto) => this.service.updateAccount(dto);
  }

  protected override getBaseRoute(): string {
    return '/accounts';
  }

  protected duplicate(): AccountDto {
    if (!this.dto) {
      throw new Error('No account data to duplicate');
    }
    return PlatformHelper.duplicateAccount(this.dto)
  }

  protected disableAllForms(): void {
    this.accountForm.disable();
    this.primaryAccountContactForm.disable();
    this.adminUserForm.disable();
  }

  protected enableAllForms(): void {
    this.accountForm.enable();
    this.primaryAccountContactForm.enable();
    this.adminUserForm.enable();
  }

  get isInvalidForm(): boolean {
    return this.primaryAccountContactForm.invalid
      || this.accountForm.invalid
      || (this.isCreateMode && this.adminUserForm.invalid);
  }

  protected initializeForms(): void {
    // Account Details Form
    this.accountForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      status: [AccountStatusLvo.ACTIVE, Validators.required]
    });

    // Main Contact Form
    this.primaryAccountContactForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      birthDate: [''],
      holderType: [HolderTypeLvo.ACCOUNT, Validators.required],
      defaultEmail: [''],
      defaultEmailType: [EmailTypeLvo.WORK],
      defaultPhone: [''],
      defaultPhoneType: [PhoneTypeLvo.WORK],
      defaultAddress: [''],
      defaultAddressType: [AddressTypeLvo.WORK]
    });

    // Main User Form
    this.adminUserForm = this.fb.group({
      adminUsername: ['', Validators.required],
      usernameType: [UsernameTypeLvo.EMAIL, Validators.required]
    });
  }

  protected override setupCreateMode(): void {
    // Initialize with default values for create mode
    this.accountForm.patchValue({
      status: AccountStatusLvo.ACTIVE
    });
    this.primaryAccountContactForm.patchValue({
      holderType: HolderTypeLvo.ACCOUNT,
      defaultEmailType: EmailTypeLvo.WORK,
      defaultPhoneType: PhoneTypeLvo.WORK,
      defaultAddressType: AddressTypeLvo.WORK
    });
    this.adminUserForm.patchValue({
      adminUsername: ''
    });
  }

  protected populateForms(account: AccountDto): void {
    // Populate account details
    this.accountForm.patchValue({
      name: account.name,
      description: account.description,
      status: account.status,
      adminUsername: account.adminUsername
    });

    const primaryContact = PlatformHelper.getPrimaryAccountContact(account);

    // Populate main contact
    if (primaryContact) {

      const defaultEmail = PlatformHelper.getDefaultContactEmail(primaryContact);
      const defaultPhone = PlatformHelper.getDefaultContactPhone(primaryContact);
      const defaultAddress = PlatformHelper.getDefaultContactAddress(primaryContact);

      this.primaryAccountContactForm.patchValue({
        firstName: primaryContact.firstName,
        lastName: primaryContact.lastName,
        birthDate: primaryContact.birthDate,
        holderType: primaryContact.holderType,
        defaultEmail: defaultEmail?.email,
        defaultEmailType: defaultEmail?.type || EmailTypeLvo.WORK,
        defaultPhone: defaultPhone?.phone,
        defaultPhoneType: defaultPhone?.type || PhoneTypeLvo.WORK,
        defaultAddress: defaultAddress?.address,
        defaultAddressType: defaultAddress?.type || AddressTypeLvo.WORK
      });
    }
  }

  protected buildDtoFromForms(): AccountDto {
    const accountFormValue = this.accountForm.value;
    const contactFormValue = this.primaryAccountContactForm.value;

    const accountDto: AccountDto = {
      name: accountFormValue.name,
      description: accountFormValue.description,
      status: AccountStatusLvo.ACTIVE,
      adminUsername: this.adminUserForm.value.adminUsername,
      adminUsernameType: this.adminUserForm.value.usernameType
    };

    // Primary contact
    const primaryContact: ContactDto = {
      firstName: contactFormValue.firstName,
      lastName: contactFormValue.lastName,
      birthDate: contactFormValue.birthDate,
      holderType: HolderTypeLvo.ACCOUNT
    };

    // Defaut email
    const defaultEmail: ContactEmailDto = {
      email: contactFormValue.defaultEmail,
      type: contactFormValue.defaultEmailType,
      holderType: HolderTypeLvo.ACCOUNT,
      defaultContactPoint: true
    }

    // Defaut phone
    const defaultPhone: ContactPhoneDto = {
      phone: contactFormValue.defaultPhone,
      type: contactFormValue.defaultPhoneType,
      holderType: HolderTypeLvo.ACCOUNT,
      defaultContactPoint: true
    }

    // Defaut address
    const defaultAddress: ContactAddressDto = {
      address: contactFormValue.defaultAddress,
      type: contactFormValue.defaultAddressType,
      holderType: HolderTypeLvo.ACCOUNT,
      defaultContactPoint: true
    }

    primaryContact.emails = [defaultEmail];
    primaryContact.phones = [defaultPhone];
    primaryContact.addresses = [defaultAddress];

    const accountContact: AccountContactDto = {
      contact: primaryContact,
      role: AccountContactRoleLvo.PRINCIPAL
    };

    accountDto.accountContacts = [accountContact];

    return accountDto;
  }
}
