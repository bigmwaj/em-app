import { Component, OnInit } from '@angular/core';
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
} from '../../api.platform.model';
import { AccountChangeStatusDialogComponent } from './change-status-dialog.component';
import { AccountDeleteDialogComponent } from './delete-dialog.component';
import { PlatformHelper } from '../../platform.helper';
import { SharedHelper } from '../../../shared/shared.helper';


@Component({
  selector: 'app-account-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false
})
export class AccountEditComponent implements OnInit {
  mode = SharedHelper.AccountEditMode.VIEW;
  
  accountForm!: FormGroup;
  primaryAccountContactForm!: FormGroup;
  adminUserForm!: FormGroup;
  
  account?: AccountDto;
  loading = false;
  error: string | null = null;

  // Enums for dropdowns
  AccountEditMode = SharedHelper.AccountEditMode;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private accountService: AccountService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.initializeForms();
    this.loadAccountData();
  }

  private initializeForms(): void {
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
      mainEmail: [''],
      mainEmailType: [EmailTypeLvo.WORK],
      mainPhone: [''],
      mainPhoneType: [PhoneTypeLvo.WORK],
      mainAddress: [''],
      mainAddressType: [AddressTypeLvo.WORK]
    });

    // Main User Form
    this.adminUserForm = this.fb.group({
      adminUsername: ['', Validators.required],
      usernameType: [UsernameTypeLvo.EMAIL, Validators.required]
    });
  }

  private loadAccountData(): void {
    // Get navigation state data
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state || history.state;
    
    // Get mode from route params or state
    this.route.params.subscribe(params => {
      const modeParam = params['mode'] || state.mode;
      
      if (modeParam === 'create') {
        this.mode = SharedHelper.AccountEditMode.CREATE;
        // Check if we have a duplicated account to populate
        if (state.account) {
          this.account = state.account;
          if (this.account) {
            this.populateForms(this.account);
          }
        } else {
          this.setupCreateMode();
        }
      } else if (modeParam === 'edit' && state.account) {
        this.mode = SharedHelper.AccountEditMode.EDIT;
        this.account = state.account;
        if (this.account) {
          this.populateForms(this.account);
        }
      } else if (modeParam === 'view' && state.account) {
        this.mode = SharedHelper.AccountEditMode.VIEW;
        this.account = state.account;
        if (this.account) {
          this.populateForms(this.account);
        }
        this.disableAllForms();
      } else {
        // Invalid state - redirect back to index
        this.router.navigate(['/accounts']);
      }
    });
  }

  private setupCreateMode(): void {
    // Initialize with default values for create mode
    this.accountForm.patchValue({
      status: AccountStatusLvo.ACTIVE
    });
    this.primaryAccountContactForm.patchValue({
      holderType: HolderTypeLvo.ACCOUNT,
      mainEmailType: EmailTypeLvo.WORK,
      mainPhoneType: PhoneTypeLvo.WORK,
      mainAddressType: AddressTypeLvo.WORK
    });
    this.adminUserForm.patchValue({
      adminUsername: ''
    });
  }

  private populateForms(account: AccountDto): void {
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
    this.accountForm.disable();
    this.primaryAccountContactForm.disable();
    this.adminUserForm.disable();
  }

  onSave(): void {
    if (this.mode === SharedHelper.AccountEditMode.VIEW) {
      return;
    }

    if (this.accountForm.invalid) {
      this.error = 'Please fill in all required fields in Account Details';
      return;
    }

    if (this.primaryAccountContactForm.get('firstName')?.value && this.primaryAccountContactForm.invalid) {
      this.error = 'Please fill in all required fields in Account Main Contact';
      return;
    }

    if (this.mode === SharedHelper.AccountEditMode.CREATE && this.adminUserForm.invalid) {
      this.error = 'Please fill in all required fields in Account Admin User';
      return;
    }

    this.loading = true;
    this.error = null;

    const accountData = PlatformHelper.buildAccountDto(this.accountForm, this.primaryAccountContactForm, this.adminUserForm);

    if (this.mode === SharedHelper.AccountEditMode.CREATE) {
      this.accountService.createAccount(accountData).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/accounts']);
        },
        error: (err) => {
          console.error('Failed to create account:', err);
          this.error = 'Failed to create account. Please try again.';
          this.loading = false;
        }
      });
    } else if (this.mode === SharedHelper.AccountEditMode.EDIT && this.account?.id) {
      this.accountService.updateAccount(accountData).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/accounts']);
        },
        error: (err) => {
          console.error('Failed to update account:', err);
          this.error = 'Failed to update account. Please try again.';
          this.loading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/accounts']);
  }

  onBack(): void {
    this.router.navigate(['/accounts']);
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

  get showSaveButton(): boolean {
    return this.mode === SharedHelper.AccountEditMode.CREATE || this.mode === SharedHelper. AccountEditMode.EDIT;
  }

  onDuplicate(): void {
    if (!this.account) {
      return;
    }

    // Create a deep copy of the account
    const duplicatedAccount = PlatformHelper.duplicateAccount(this.account);

    // Navigate to create mode with duplicated data
    this.router.navigate(['/accounts/edit', 'create'], {
      state: { mode: 'create', account: duplicatedAccount }
    });
  }

  onCreateAccount(): void {
    this.router.navigate(['/accounts/edit', 'create'], {
      state: { mode: 'create' }
    });
  }

  onEditAccount(): void {
    if (this.mode === SharedHelper.AccountEditMode.VIEW) {
      this.mode = SharedHelper.AccountEditMode.EDIT;
      this.enableAllForms();
    }
  }

  onChangeStatus(): void {
    if (!this.account) {
      return;
    }

    const dialogRef = this.dialog.open(AccountChangeStatusDialogComponent, {
      width: '400px',
      data: { account: this.account }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.account) {
        // Update the status
        this.account.status = result;
        this.accountForm.patchValue({ status: result });

        // In a real application, you would reload from the server
        // For now, just update the form to reflect the change
      }
    });
  }

  private enableAllForms(): void {
    this.accountForm.enable();
    this.primaryAccountContactForm.enable();
    this.adminUserForm.enable();
  }

  get showDuplicateButton(): boolean {
    return this.mode === SharedHelper.AccountEditMode.EDIT || this.mode === SharedHelper.AccountEditMode.VIEW;
  }

  get showCreateAccountButton(): boolean {
    return true; // Visible in all modes
  }

  get showEditAccountButton(): boolean {
    return this.mode === SharedHelper.AccountEditMode.VIEW;
  }

  get showChangeStatusButton(): boolean {
    return this.mode === SharedHelper.AccountEditMode.EDIT || this.mode === SharedHelper.AccountEditMode.VIEW;
  }

  get showDeleteButton(): boolean {
    return this.mode === SharedHelper.AccountEditMode.VIEW;
  }

  onDelete(): void {
    if (!this.account) {
      return;
    }

    const dialogRef = this.dialog.open(AccountDeleteDialogComponent, {
      width: '400px',
      data: { account: this.account }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Navigate back to accounts list
        this.router.navigate(['/accounts']);
      }
    });
  }
}
