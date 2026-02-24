import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AccountService } from '../../service/account.service';
import {
  AccountDto,
  AccountStatusLvo,
  OwnerTypeLvo,
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
import { AbstractEditWithStatusComponent } from '../../../shared/component/abstract-edit-with-status.component';
import { AccountHelper } from '../../helper/account.helper';

@Component({
  selector: 'app-account-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false
})
export class AccountEditComponent extends AbstractEditWithStatusComponent<AccountDto, AccountStatusLvo> {
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
    this.create = (dto) => this.service.createAccount(dto);
    this.update = (dto) => this.service.updateAccount(dto);
    this.changeStatus = (dto) => this.service.updateAccount(dto as AccountDto);
    this.buildFormData = (dto) => AccountHelper.buildFormData(dto);
  }

  protected override getBaseRoute(): string {
    return '/accounts';
  }

  override get isInvalidForm(): boolean {
    return this.primaryAccountContactForm.invalid
      || this.mainForm.invalid
      || (this.isCreateMode && this.adminUserForm.invalid);
  }

  protected override initializeForms(): FormGroup[] {
    // Account Details Form
    this.mainForm = this.fb.group({
      editAction: [this.editAction],
      id: [],
      name: ['', Validators.required],
      description: [''],
      status: [AccountStatusLvo.ACTIVE, Validators.required]
    });

    // Main Contact Form
    this.primaryAccountContactForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      birthDate: [''],
      ownerType: [OwnerTypeLvo.ACCOUNT, Validators.required],
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

    return [this.mainForm, this.primaryAccountContactForm, this.adminUserForm];
  }

  protected buildDtoFromForms(): AccountDto {
    const mainFormValue = this.mainForm.value;
    const contactFormValue = this.primaryAccountContactForm.value;

    const accountDto: AccountDto = {
      editAction: this.editAction, // required for validation in service layer
      id: mainFormValue.id,
      name: mainFormValue.name,
      description: mainFormValue.description,
      status: AccountStatusLvo.ACTIVE,
      adminUsername: this.adminUserForm.value.adminUsername,
      adminUsernameType: this.adminUserForm.value.usernameType
    };

    // Primary contact
    const primaryContact: ContactDto = {
      firstName: contactFormValue.firstName,
      lastName: contactFormValue.lastName,
      birthDate: contactFormValue.birthDate,
      ownerType: OwnerTypeLvo.ACCOUNT
    };

    // Defaut email
    const defaultEmail: ContactEmailDto = {
      email: contactFormValue.defaultEmail,
      type: contactFormValue.defaultEmailType,
      ownerType: OwnerTypeLvo.ACCOUNT,
      defaultContactPoint: true
    }

    // Defaut phone
    const defaultPhone: ContactPhoneDto = {
      phone: contactFormValue.defaultPhone,
      type: contactFormValue.defaultPhoneType,
      ownerType: OwnerTypeLvo.ACCOUNT,
      defaultContactPoint: true
    }

    // Defaut address
    const defaultAddress: ContactAddressDto = {
      address: contactFormValue.defaultAddress,
      type: contactFormValue.defaultAddressType,
      ownerType: OwnerTypeLvo.ACCOUNT,
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
