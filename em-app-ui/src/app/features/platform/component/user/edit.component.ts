import { Component } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../service/user.service';
import {
  UserDto,
  UserStatusLvo,
  OwnerTypeLvo,
  EmailTypeLvo,
  PhoneTypeLvo,
  AddressTypeLvo,
  UsernameTypeLvo,
  ContactDto,
} from '../../api.platform.model';
import { AbstractEditWithStatusComponent } from '../../../shared/component/abstract-edit-with-status.component';
import { UserHelper } from '../../helper/user.helper';
import { ContactHelper } from '../../helper/contact.helper';
import { COUNTRIES } from '../../constants/country.constants';

@Component({
  selector: 'app-user-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false
})
export class UserEditComponent extends AbstractEditWithStatusComponent<UserDto, UserStatusLvo> {

  contactForm!: FormGroup;
  defaultEmailForm!: FormGroup;
  defaultPhoneForm!: FormGroup;
  defaultAddressForm!: FormGroup;

  // Enums for dropdowns
  UserStatusLvo = UserStatusLvo;
  UsernameTypeLvo = UsernameTypeLvo;
  OwnerTypeLvo = OwnerTypeLvo;
  EmailTypeLvo = EmailTypeLvo;
  PhoneTypeLvo = PhoneTypeLvo;
  AddressTypeLvo = AddressTypeLvo;

  // Constants
  readonly countries = COUNTRIES;

  constructor(
    public contactHelper: ContactHelper,
    protected override helper: UserHelper,
    private service: UserService  ) {
    super(helper);
    this.delete = (dto) => this.service.deleteUser(dto);
    this.changeStatus = (dto) => this.service.updateUser(dto as UserDto);
    this.create = (dto) => this.service.createUser(dto);
    this.update = (dto) => this.service.updateUser(dto);
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
      ownerType: [this.dto?.ownerType, Validators.required]
    });

    // Contact Form
    const contact = this.dto?.contact || {} as ContactDto;

    this.contactForm = this.fb.group({
      id: [contact?.id],
      firstName: [contact?.firstName, Validators.required],
      lastName: [contact?.lastName, Validators.required],
      birthDate: [contact?.birthDate],
    });

    const defaultEmail = this.contactHelper.getDefaultContactEmail(contact);
    this.defaultEmailForm = this.fb.group({
      id: [defaultEmail?.id],
      email: [defaultEmail?.email],
      type: [defaultEmail?.type || EmailTypeLvo.WORK]
    });

    const defaultPhone = this.contactHelper.getDefaultContactPhone(contact);
    this.defaultPhoneForm = this.fb.group({
      id: [defaultPhone?.id],
      phone: [defaultPhone?.phone],
      type: [defaultPhone?.type || PhoneTypeLvo.WORK],
    });

    const defaultAddress = this.contactHelper.getDefaultContactAddress(contact);
    this.defaultAddressForm = this.fb.group({
      id: [defaultAddress?.id],
      address: [defaultAddress?.address],
      type: [defaultAddress?.type || AddressTypeLvo.WORK],
      country: [defaultAddress?.country],
      region: [defaultAddress?.region],
      city: [defaultAddress?.city]
    });

    // Make country required when address is provided
    const subscription$ = this.defaultAddressForm.get('address')?.valueChanges
      .subscribe((address) => {
        const countryControl = this.defaultAddressForm.get('country');
        if (address && address.trim()) {
          countryControl?.setValidators([Validators.required]);
        } else {
          countryControl?.clearValidators();
        }
        countryControl?.updateValueAndValidity();
      });
    if (subscription$) {
      this.subscription$.push(subscription$);
    }

    return [this.mainForm, this.contactForm, this.defaultEmailForm, this.defaultPhoneForm, this.defaultAddressForm];
  }

  protected buildDtoFromForms(): UserDto {
    const mainFormValue = this.mainForm.value;
    const contactFormValue = this.contactForm.value;
    const defaultEmailFormValue = this.defaultEmailForm.value;
    const defaultPhoneFormValue = this.defaultPhoneForm.value;
    const defaultAddressFormValue = this.defaultAddressForm.value;

    const userDto: UserDto = {
      id: mainFormValue.id,
      username: mainFormValue.username,
      usernameType: mainFormValue.usernameType,
      password: mainFormValue.password,
      provider: mainFormValue.provider,
      picture: mainFormValue.picture,
      status: mainFormValue.status,
      ownerType: mainFormValue.ownerType,

      contact: {
        id: contactFormValue.id,
        firstName: contactFormValue.firstName,
        lastName: contactFormValue.lastName,
        birthDate: contactFormValue.birthDate,
        ownerType: mainFormValue.ownerType,

        emails: [{
          id: defaultEmailFormValue.id,
          email: defaultEmailFormValue.email,
          type: defaultEmailFormValue.type,
          ownerType: mainFormValue.ownerType,
          defaultContactPoint: true
        }],

        phones: [{
          id: defaultPhoneFormValue.id,
          phone: defaultPhoneFormValue.phone,
          type: defaultPhoneFormValue.type,
          ownerType: mainFormValue.ownerType,
          defaultContactPoint: true
        }],

        addresses: [{
          id: defaultAddressFormValue.id,
          address: defaultAddressFormValue.address,
          type: defaultAddressFormValue.type,
          ownerType: mainFormValue.ownerType,
          defaultContactPoint: true
        }]
      }
    };

    return userDto;
  }
}
