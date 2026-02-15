import { Component, OnInit } from '@angular/core';
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
import { UserChangeStatusDialogComponent } from './change-status-dialog.component';
import { UserDeleteDialogComponent } from './delete-dialog.component';
import { PlatformHelper } from '../../platform.helper';
import { SharedHelper } from '../../../shared/shared.helper';


@Component({
  selector: 'app-user-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false
})
export class UserEditComponent implements OnInit {
  mode = SharedHelper.AccountEditMode.VIEW;

  userForm!: FormGroup;
  contactForm!: FormGroup;

  user?: UserDto;
  loading = false;
  error: string | null = null;

  // Enums for dropdowns
  UserEditMode = SharedHelper.AccountEditMode;
  UserStatusLvo = UserStatusLvo;
  UsernameTypeLvo = UsernameTypeLvo;
  HolderTypeLvo = HolderTypeLvo;
  EmailTypeLvo = EmailTypeLvo;
  PhoneTypeLvo = PhoneTypeLvo;
  AddressTypeLvo = AddressTypeLvo;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private userService: UserService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.initializeForms();
    this.loadUserData();
  }

  private initializeForms(): void {
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
      mainEmail: [''],
      mainEmailType: [EmailTypeLvo.WORK],
      mainPhone: [''],
      mainPhoneType: [PhoneTypeLvo.WORK],
      mainAddress: [''],
      mainAddressType: [AddressTypeLvo.WORK]
    });
  }

  private loadUserData(): void {
    // Get navigation state data
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state || history.state;

    // Get mode from route params or state
    this.route.params.subscribe(params => {
      const modeParam = params['mode'] || state.mode;

      if (modeParam === 'create') {
        this.mode = SharedHelper.AccountEditMode.CREATE;
        // Check if we have a duplicated user to populate
        if (state.user) {
          this.user = state.user;
          if (this.user) {
            this.populateForms(this.user);
          }
        } else {
          this.setupCreateMode();
        }
      } else if (modeParam === 'edit' && state.user) {
        this.mode = SharedHelper.AccountEditMode.EDIT;
        this.user = state.user;
        if (this.user) {
          this.populateForms(this.user);
        }
      } else if (modeParam === 'view' && state.user) {
        this.mode = SharedHelper.AccountEditMode.VIEW;
        this.user = state.user;
        if (this.user) {
          this.populateForms(this.user);
        }
        this.disableAllForms();
      } else {
        // Invalid state - redirect back to index
        this.router.navigate(['/users']);
      }
    });
  }

  private setupCreateMode(): void {
    // Initialize with default values for create mode
    this.userForm.patchValue({
      status: UserStatusLvo.ACTIVE,
      usernameType: UsernameTypeLvo.EMAIL,
      holderType: HolderTypeLvo.ACCOUNT,
      provider: 'LOCAL'
    });
    this.contactForm.patchValue({
      mainEmailType: EmailTypeLvo.WORK,
      mainPhoneType: PhoneTypeLvo.WORK,
      mainAddressType: AddressTypeLvo.WORK
    });
  }

  private populateForms(user: UserDto): void {
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
        mainEmail: defaultEmail?.email,
        mainEmailType: defaultEmail?.type || EmailTypeLvo.WORK,
        mainPhone: defaultPhone?.phone,
        mainPhoneType: defaultPhone?.type || PhoneTypeLvo.WORK,
        mainAddress: defaultAddress?.address,
        mainAddressType: defaultAddress?.type || AddressTypeLvo.WORK
      });
    }
  }

  private disableAllForms(): void {
    this.userForm.disable();
    this.contactForm.disable();
  }

  private enableAllForms(): void {
    this.userForm.enable();
    this.contactForm.enable();
  }

  onSave(): void {
    if (this.mode === SharedHelper.AccountEditMode.VIEW) {
      return;
    }

    if (this.userForm.invalid) {
      this.error = 'Please fill in all required fields in User Details';
      return;
    }

    if (this.contactForm.invalid) {
      this.error = 'Please fill in all required fields in Contact Details';
      return;
    }

    this.loading = true;
    this.error = null;

    const userData = this.buildUserDto();

    if (this.mode === SharedHelper.AccountEditMode.CREATE) {
      this.userService.createUser(userData).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/users']);
        },
        error: (err) => {
          console.error('Failed to create user:', err);
          this.error = 'Failed to create user. Please try again.';
          this.loading = false;
        }
      });
    } else if (this.mode === SharedHelper.AccountEditMode.EDIT && this.user?.id) {
      userData.id = this.user.id;
      this.userService.updateUser(userData).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/users']);
        },
        error: (err) => {
          console.error('Failed to update user:', err);
          this.error = 'Failed to update user. Please try again.';
          this.loading = false;
        }
      });
    }
  }

  private buildUserDto(): UserDto {
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
        emails: contactFormValue.mainEmail ? [{
          email: contactFormValue.mainEmail,
          type: contactFormValue.mainEmailType,
          holderType: userFormValue.holderType,
          defaultContactPoint: true
        }] : [],
        phones: contactFormValue.mainPhone ? [{
          phone: contactFormValue.mainPhone,
          type: contactFormValue.mainPhoneType,
          holderType: userFormValue.holderType,
          defaultContactPoint: true
        }] : [],
        addresses: contactFormValue.mainAddress ? [{
          address: contactFormValue.mainAddress,
          type: contactFormValue.mainAddressType,
          holderType: userFormValue.holderType,
          defaultContactPoint: true
        }] : []
      }
    };

    return userDto;
  }

  onCancel(): void {
    this.router.navigate(['/users']);
  }

  onBack(): void {
    this.router.navigate(['/users']);
  }

  get isCreateMode(): boolean {
    return this.mode === SharedHelper.AccountEditMode.CREATE;
  }

  get isEditMode(): boolean {
    return this.mode === SharedHelper.AccountEditMode.EDIT;
  }

  get isViewMode(): boolean {
    return this.mode === SharedHelper.AccountEditMode.VIEW;
  }

  get showCancelButton(): boolean {
    return this.mode === SharedHelper.AccountEditMode.CREATE || this.mode === SharedHelper.AccountEditMode.EDIT;
  }

  get showCreateButton(): boolean {
    return this.mode === SharedHelper.AccountEditMode.CREATE;
  }

  get showEditButton(): boolean {
    return this.mode === SharedHelper.AccountEditMode.EDIT;
  }

  onDuplicate(): void {
    if (!this.user) {
      return;
    }

    // Create a deep copy of the user
    const duplicatedUser = PlatformHelper.duplicateUser(this.user);

    // Navigate to create mode with duplicated data
    this.router.navigate(['/users/edit', 'create'], {
      state: { mode: 'create', user: duplicatedUser }
    });
  }

  onEditUser(): void {
    if (this.mode === SharedHelper.AccountEditMode.VIEW) {
      this.mode = SharedHelper.AccountEditMode.EDIT;
      this.enableAllForms();
    }
  }

  onChangeStatus(): void {
    if (!this.user) {
      return;
    }

    const dialogRef = this.dialog.open(UserChangeStatusDialogComponent, {
      width: '400px',
      data: { user: this.user }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.user) {
        // Update the status
        this.user.status = result.status;
        this.userForm.patchValue({ status: result.status });
      }
    });
  }

  get showDuplicateButton(): boolean {
    return this.mode === SharedHelper.AccountEditMode.VIEW;
  }

  get showChangeStatusButton(): boolean {
    return this.mode === SharedHelper.AccountEditMode.VIEW;
  }

  get showDeleteButton(): boolean {
    return this.mode === SharedHelper.AccountEditMode.VIEW;
  }

  onDelete(): void {
    if (!this.user) {
      return;
    }

    const dialogRef = this.dialog.open(UserDeleteDialogComponent, {
      width: '400px',
      data: { user: this.user }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Navigate back to users list
        this.router.navigate(['/users']);
      }
    });
  }
}
