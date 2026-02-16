import { Component, OnInit, OnDestroy } from '@angular/core';
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
import { ContactDeleteDialogComponent } from './delete-dialog.component';
import { PlatformHelper } from '../../platform.helper';
import { SharedHelper } from '../../../shared/shared.helper';
import { Subject, takeUntil } from 'rxjs';
import { COUNTRIES } from '../../constants/country.constants';

@Component({
  selector: 'app-contact-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false,
})
export class ContactEditComponent implements OnInit, OnDestroy {
  mode = SharedHelper.AccountEditMode.VIEW;

  contactForm!: FormGroup;

  contact?: ContactDto;
  loading = false;
  error: string | null = null;

  // Enums for dropdowns
  ContactEditMode = SharedHelper.AccountEditMode;
  HolderTypeLvo = HolderTypeLvo;
  EmailTypeLvo = EmailTypeLvo;
  PhoneTypeLvo = PhoneTypeLvo;
  AddressTypeLvo = AddressTypeLvo;

  // Constants
  readonly countries = COUNTRIES;

  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private contactService: ContactService,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadContactData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForm(): void {
    this.contactForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      birthDate: [''],
      holderType: [HolderTypeLvo.ACCOUNT, Validators.required],
      mainEmail: [''],
      mainEmailType: [EmailTypeLvo.WORK],
      mainPhone: [''],
      mainPhoneType: [PhoneTypeLvo.WORK],
      mainAddress: [''],
      mainAddressType: [AddressTypeLvo.WORK],
      country: [''],
      region: [''],
      city: [''],
    });

    // Make country required when address is provided
    this.contactForm.get('mainAddress')?.valueChanges.subscribe((address) => {
      const countryControl = this.contactForm.get('country');
      if (address && address.trim()) {
        countryControl?.setValidators([Validators.required]);
      } else {
        countryControl?.clearValidators();
      }
      countryControl?.updateValueAndValidity();
    });
  }

  private loadContactData(): void {
    // Get navigation state data
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state || history.state;

    // Get mode from route params or state
    this.route.params.subscribe((params) => {
      const modeParam = params['mode'] || state.mode;

      if (modeParam === 'create') {
        this.mode = SharedHelper.AccountEditMode.CREATE;
        // Check if we have a duplicated contact to populate
        if (state.contact) {
          this.contact = state.contact;
          if (this.contact) {
            this.populateForm(this.contact);
          }
        }
        this.contactForm.enable();
      } else if (modeParam === 'edit') {
        this.mode = SharedHelper.AccountEditMode.EDIT;
        if (state.contact) {
          this.contact = state.contact;
          if (this.contact) {
            this.populateForm(this.contact);
          }
        }
        this.contactForm.enable();
      } else {
        // View mode
        this.mode = SharedHelper.AccountEditMode.VIEW;
        if (state.contact) {
          this.contact = state.contact;
          if (this.contact) {
            this.populateForm(this.contact);
          }
        }
        this.contactForm.disable();
      }
    });
  }

  private populateForm(contact: ContactDto): void {
    const defaultEmail = PlatformHelper.getDefaultContactEmail(contact);
    const defaultPhone = PlatformHelper.getDefaultContactPhone(contact);
    const defaultAddress = PlatformHelper.getDefaultContactAddress(contact);

    this.contactForm.patchValue({
      firstName: contact.firstName,
      lastName: contact.lastName,
      birthDate: contact.birthDate,
      holderType: contact.holderType,
      mainEmail: defaultEmail?.email || '',
      mainEmailType: defaultEmail?.type || EmailTypeLvo.WORK,
      mainPhone: defaultPhone?.phone || '',
      mainPhoneType: defaultPhone?.type || PhoneTypeLvo.WORK,
      mainAddress: defaultAddress?.address || '',
      mainAddressType: defaultAddress?.type || AddressTypeLvo.WORK,
      country: defaultAddress?.country || '',
      region: defaultAddress?.region || '',
      city: defaultAddress?.city || '',
    });
  }

  private buildContactDto(): ContactDto {
    const formValue = this.contactForm.value;

    const contactDto: ContactDto = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      birthDate: formValue.birthDate,
      holderType: formValue.holderType,
    };

    // Add ID if editing
    if (this.mode === SharedHelper.AccountEditMode.EDIT && this.contact?.id) {
      contactDto.id = this.contact.id;
    }

    // Build emails array
    if (formValue.mainEmail) {
      contactDto.emails = [
        {
          email: formValue.mainEmail,
          type: formValue.mainEmailType,
          holderType: formValue.holderType,
          defaultContactPoint: true,
        },
      ];

      // Preserve ID if editing
      if (this.mode === SharedHelper.AccountEditMode.EDIT) {
        const existingEmail = PlatformHelper.getDefaultContactEmail(this.contact!);
        if (existingEmail?.id) {
          contactDto.emails[0].id = existingEmail.id;
          contactDto.emails[0].contactId = this.contact!.id;
        }
      }
    }

    // Build phones array
    if (formValue.mainPhone) {
      contactDto.phones = [
        {
          phone: formValue.mainPhone,
          type: formValue.mainPhoneType,
          holderType: formValue.holderType,
          defaultContactPoint: true,
        },
      ];

      // Preserve ID if editing
      if (this.mode === SharedHelper.AccountEditMode.EDIT) {
        const existingPhone = PlatformHelper.getDefaultContactPhone(this.contact!);
        if (existingPhone?.id) {
          contactDto.phones[0].id = existingPhone.id;
          contactDto.phones[0].contactId = this.contact!.id;
        }
      }
    }

    // Build addresses array
    if (formValue.mainAddress) {
      contactDto.addresses = [
        {
          address: formValue.mainAddress,
          type: formValue.mainAddressType,
          holderType: formValue.holderType,
          defaultContactPoint: true,
          country: formValue.country,
          region: formValue.region,
          city: formValue.city,
        },
      ];

      // Preserve ID if editing
      if (this.mode === SharedHelper.AccountEditMode.EDIT) {
        const existingAddress = PlatformHelper.getDefaultContactAddress(this.contact!);
        if (existingAddress?.id) {
          contactDto.addresses[0].id = existingAddress.id;
          contactDto.addresses[0].contactId = this.contact!.id;
        }
      }
    }

    return contactDto;
  }

  // Navigation handlers
  onBack(): void {
    this.router.navigate(['/contacts']);
  }

  onCancel(): void {
    this.router.navigate(['/contacts']);
  }

  // CRUD handlers
  onCreate(): void {
    if (this.contactForm.invalid) {
      this.error = 'Please fill in all required fields';
      return;
    }

    this.loading = true;
    this.error = null;

    const contactDto = this.buildContactDto();

    this.contactService
      .createContact(contactDto)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (createdContact) => {
          this.loading = false;
          this.router.navigate(['/contacts']);
        },
        error: (err) => {
          console.error('Failed to create contact:', err);
          this.error = 'Failed to create contact. Please try again.';
          this.loading = false;
        },
      });
  }

  onEdit(): void {
    if (this.contactForm.invalid) {
      this.error = 'Please fill in all required fields';
      return;
    }

    this.loading = true;
    this.error = null;

    const contactDto = this.buildContactDto();

    this.contactService
      .updateContact(contactDto)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedContact) => {
          this.loading = false;
          this.router.navigate(['/contacts']);
        },
        error: (err) => {
          console.error('Failed to update contact:', err);
          this.error = 'Failed to update contact. Please try again.';
          this.loading = false;
        },
      });
  }

  // Action handlers
  onDuplicate(): void {
    if (this.contact) {
      const duplicatedContact = PlatformHelper.duplicateContact(this.contact);
      this.router.navigate(['/contacts/edit', 'create'], {
        state: { mode: 'create', contact: duplicatedContact },
      });
    }
  }

  onDelete(): void {
    if (this.contact) {
      const dialogRef = this.dialog.open(ContactDeleteDialogComponent, {
        width: '400px',
        data: { contact: this.contact },
      });

      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          // Navigate back to index after successful deletion
          this.router.navigate(['/contacts']);
        }
      });
    }
  }

  // Conditional visibility helpers
  get isCreateMode(): boolean {
    return this.mode === SharedHelper.AccountEditMode.CREATE;
  }

  get isEditMode(): boolean {
    return this.mode === SharedHelper.AccountEditMode.EDIT;
  }

  get isViewMode(): boolean {
    return this.mode === SharedHelper.AccountEditMode.VIEW;
  }
}
