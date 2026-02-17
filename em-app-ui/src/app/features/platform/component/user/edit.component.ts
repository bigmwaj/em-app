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
} from '../../api.platform.model';
import { PlatformHelper } from '../../platform.helper';
import { SharedHelper } from '../../../shared/shared.helper';
import { AbstractEditWithStatusComponent } from '../../../shared/component/abstract-edit-with-status.component';

@Component({
  selector: 'app-user-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false
})
export class UserEditComponent extends AbstractEditWithStatusComponent<UserDto, UserStatusLvo> {

  userForm!: FormGroup;
  contactForm!: FormGroup;

  // Enums for dropdowns
  UserEditMode = SharedHelper.EditMode;
  UserStatusLvo = UserStatusLvo;
  UsernameTypeLvo = UsernameTypeLvo;
  HolderTypeLvo = HolderTypeLvo;
  EmailTypeLvo = EmailTypeLvo;
  PhoneTypeLvo = PhoneTypeLvo;
  AddressTypeLvo = AddressTypeLvo;

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
  }

  get isInvalidForm(): boolean {
    return this.contactForm.invalid || this.userForm.invalid;
  }

  protected override getBaseRoute(): string {
    return '/users';
  }

  protected duplicate(): UserDto {
    if (!this.dto) {
      throw new Error('No user data to duplicate');
    }
    return PlatformHelper.duplicateUser(this.dto)
  }

  protected initializeForms(): void {
    // User Details Form
    this.userForm = this.fb.group({
      username: ['', Validators.required],
      usernameType: [UsernameTypeLvo.EMAIL, Validators.required],
      password: [''],
      provider: ['LOCAL'],
      picture: [''],
      status: [UserStatusLvo.ACTIVE, Validators.required],
      holderType: [HolderTypeLvo.ACCOUNT, Validators.required]
    });

    // Contact Form
    this.contactForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      birthDate: [''],
      defaultEmail: [''],
      defaultEmailType: [EmailTypeLvo.WORK],
      defaultPhone: [''],
      defaultPhoneType: [PhoneTypeLvo.WORK],
      defaultAddress: [''],
      defaultAddressType: [AddressTypeLvo.WORK]
    });
  }


  protected setupCreateMode(): void {
    // Initialize with default values for create mode
    this.userForm.patchValue({
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
    this.userForm.patchValue({
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
      const defaultEmail = PlatformHelper.getDefaultContactEmail(user.contact);
      const defaultPhone = PlatformHelper.getDefaultContactPhone(user.contact);
      const defaultAddress = PlatformHelper.getDefaultContactAddress(user.contact);

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

  protected disableAllForms(): void {
    this.userForm.disable();
    this.contactForm.disable();
  }

  protected enableAllForms(): void {
    this.userForm.enable();
    this.contactForm.enable();
  }

  protected buildDtoFromForms(): UserDto {
    const userFormValue = this.userForm.value;
    const contactFormValue = this.contactForm.value;

    const userDto: UserDto = {
      username: userFormValue.username,
      usernameType: userFormValue.usernameType,
      password: userFormValue.password,
      provider: userFormValue.provider,
      picture: userFormValue.picture,
      status: userFormValue.status,
      holderType: userFormValue.holderType,
      contact: {
        firstName: contactFormValue.firstName,
        lastName: contactFormValue.lastName,
        birthDate: contactFormValue.birthDate,
        holderType: userFormValue.holderType,
        emails: contactFormValue.defaultEmail ? [{
          email: contactFormValue.defaultEmail,
          type: contactFormValue.defaultEmailType,
          holderType: userFormValue.holderType,
          defaultContactPoint: true
        }] : [],
        phones: contactFormValue.defaultPhone ? [{
          phone: contactFormValue.defaultPhone,
          type: contactFormValue.defaultPhoneType,
          holderType: userFormValue.holderType,
          defaultContactPoint: true
        }] : [],
        addresses: contactFormValue.defaultAddress ? [{
          address: contactFormValue.defaultAddress,
          type: contactFormValue.defaultAddressType,
          holderType: userFormValue.holderType,
          defaultContactPoint: true
        }] : []
      }
    };

    return userDto;
  }
}
