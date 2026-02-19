import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../service/user.service';
import {
  UserDto,
  UserStatusLvo,
  HolderTypeLvo,
  EmailTypeLvo,
  PhoneTypeLvo,
  AddressTypeLvo,
  UsernameTypeLvo,
  ContactDto,
} from '../../api.platform.model';
import { SharedHelper } from '../../../shared/shared.helper';
import { AbstractEditWithStatusComponent } from '../../../shared/component/abstract-edit-with-status.component';
import { UserHelper } from '../../helper/user.helper';
import { ContactHelper } from '../../helper/contact.helper';
import { takeUntil } from 'rxjs';
import { COUNTRIES } from '../../constants/country.constants';

@Component({
  selector: 'app-user-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false
})
export class UserEditComponent extends AbstractEditWithStatusComponent<UserDto, UserStatusLvo> {

  UserHelper = UserHelper;
  contactForm!: FormGroup;
  defaultEmailForm!: FormGroup;
  defaultPhoneForm!: FormGroup;
  defaultAddressForm!: FormGroup;

  // Enums for dropdowns
  UserEditMode = SharedHelper.EditMode;
  UserStatusLvo = UserStatusLvo;
  UsernameTypeLvo = UsernameTypeLvo;
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
    private service: UserService
  ) {
    super(fb, router, route, dialog);
    this.delete = (dto) => this.service.deleteUser(dto);
    this.changeStatus = (dto) => this.service.updateUser(dto as UserDto);
    this.create = (dto) => this.service.createUser(dto);
    this.update = (dto) => this.service.updateUser(dto);
    this.buildFormData = (dto) => UserHelper.buildFormData(dto);
  }

  protected override getBaseRoute(): string {
    return '/users';
  }

  protected override initializeForms(): FormGroup[] {
    // User Details Form
    this.mainForm = this.fb.group({
      id: [this.dto?.id],
      username: [this.dto?.username, Validators.required],
      usernameType: [this.dto?.usernameType || UsernameTypeLvo.EMAIL, Validators.required],
      password: [this.dto?.password],
      provider: [this.dto?.provider],
      picture: [this.dto?.picture],
      status: [this.dto?.status, Validators.required],
      holderType: [this.dto?.holderType, Validators.required]
    });

    // Contact Form
    const contact = this.dto?.contact || {} as ContactDto;

    this.contactForm = this.fb.group({
      id: [contact?.id],
      firstName: [contact?.firstName, Validators.required],
      lastName: [contact?.lastName, Validators.required],
      birthDate: [contact?.birthDate],
    });

    const defaultEmail = ContactHelper.getDefaultContactEmail(contact);
    this.defaultEmailForm = this.fb.group({
      id: [defaultEmail?.id],
      email: [defaultEmail?.email],
      type: [defaultEmail?.type || EmailTypeLvo.WORK]
    });

    const defaultPhone = ContactHelper.getDefaultContactPhone(contact);
    this.defaultPhoneForm = this.fb.group({
      id: [defaultPhone?.id],
      phone: [defaultPhone?.phone],
      type: [defaultPhone?.type || PhoneTypeLvo.WORK],
    });

    const defaultAddress = ContactHelper.getDefaultContactAddress(contact);
    this.defaultAddressForm = this.fb.group({
      id: [defaultAddress?.id],
      address: [defaultAddress?.address],
      type: [defaultAddress?.type || AddressTypeLvo.WORK],
      country: [defaultAddress?.country],
      region: [defaultAddress?.region],
      city: [defaultAddress?.city]
    });

    // Make country required when address is provided
    this.defaultAddressForm.get('address')?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((address) => {
        const countryControl = this.defaultAddressForm.get('country');
        if (address && address.trim()) {
          countryControl?.setValidators([Validators.required]);
        } else {
          countryControl?.clearValidators();
        }
        countryControl?.updateValueAndValidity();
      });

    return [this.mainForm, this.contactForm, this.defaultEmailForm, this.defaultPhoneForm, this.defaultAddressForm];
  }


  protected setupCreateMode(): void {
    // Initialize with default values for create mode
    this.mainForm.patchValue({
      status: UserStatusLvo.ACTIVE,
      usernameType: UsernameTypeLvo.EMAIL,
      holderType: HolderTypeLvo.ACCOUNT,
      provider: 'LOCAL'
    });
    this.contactForm.patchValue({
      defaultEmailType: EmailTypeLvo.WORK,
      defaultPhoneType: PhoneTypeLvo.WORK,
      defaultAddressType: AddressTypeLvo.WORK
    });
  }

  protected populateForms(user: UserDto): void {
    // Populate user details
    this.mainForm.patchValue({
      username: user.username,
      usernameType: user.usernameType,
      password: user.password,
      provider: user.provider,
      picture: user.picture,
      status: user.status,
      holderType: user.holderType
    });

    // Populate contact
    if (user.contact) {
      const defaultEmail = ContactHelper.getDefaultContactEmail(user.contact);
      const defaultPhone = ContactHelper.getDefaultContactPhone(user.contact);
      const defaultAddress = ContactHelper.getDefaultContactAddress(user.contact);

      this.contactForm.patchValue({
        firstName: user.contact.firstName,
        lastName: user.contact.lastName,
        birthDate: user.contact.birthDate,
        defaultEmail: defaultEmail?.email,
        defaultEmailType: defaultEmail?.type || EmailTypeLvo.WORK,
        defaultPhone: defaultPhone?.phone,
        defaultPhoneType: defaultPhone?.type || PhoneTypeLvo.WORK,
        defaultAddress: defaultAddress?.address,
        defaultAddressType: defaultAddress?.type || AddressTypeLvo.WORK
      });
    }
  }

  protected buildDtoFromForms(): UserDto {
    const mainFormValue = this.mainForm.value;
    const contactFormValue = this.contactForm.value;
    const defaultEmailFormValue = this.defaultEmailForm.value;
    const defaultPhoneFormValue = this.defaultPhoneForm.value;
    const defaultAddressFormValue = this.defaultAddressForm.value;

    const userDto: UserDto = {
      editAction: mainFormValue.editAction,
      id: mainFormValue.id,
      username: mainFormValue.username,
      usernameType: mainFormValue.usernameType,
      password: mainFormValue.password,
      provider: mainFormValue.provider,
      picture: mainFormValue.picture,
      status: mainFormValue.status,
      holderType: mainFormValue.holderType,

      contact: {
        editAction: contactFormValue.editAction,
        id: contactFormValue.id,
        firstName: contactFormValue.firstName,
        lastName: contactFormValue.lastName,
        birthDate: contactFormValue.birthDate,
        holderType: mainFormValue.holderType,

        emails: [{
          editAction: defaultEmailFormValue.editAction,
          id: defaultEmailFormValue.id,
          email: defaultEmailFormValue.email,
          type: defaultEmailFormValue.type,
          holderType: mainFormValue.holderType,
          defaultContactPoint: true
        }],

        phones: [{
          editAction: defaultPhoneFormValue.editAction,
          id: defaultPhoneFormValue.id,
          phone: defaultPhoneFormValue.phone,
          type: defaultPhoneFormValue.type,
          holderType: mainFormValue.holderType,
          defaultContactPoint: true
        }],

        addresses: [{
          editAction: defaultAddressFormValue.editAction,
          id: defaultAddressFormValue.id,
          address: defaultAddressFormValue.address,
          type: defaultAddressFormValue.type,
          holderType: mainFormValue.holderType,
          defaultContactPoint: true
        }]
      }
    };

    return userDto;
  }
}
