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
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';

@Component({
  selector: 'app-contact-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false,
})
export class ContactEditComponent extends AbstractEditComponent implements OnInit, OnDestroy {
  contactForm!: FormGroup;
  contact?: ContactDto;
  loading = false;
  error: string | null = null;

  // Enums for dropdowns
  ContactEditMode = SharedHelper.EditMode;
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
  ) {
    super();
  }
  
  get isInvalidForm(): boolean {
    return this.contactForm.invalid;
  }

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
      ?.valueChanges.pipe(takeUntil(this.destroy$))
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

  private loadContactData(): void {
    // Get navigation state data
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state || history.state;

    // Get mode from route params or state
    this.route.params.subscribe((params) => {
      const modeParam = params['mode'] || state.mode;

      if (modeParam === 'create') {
        this.mode = SharedHelper.EditMode.CREATE;
        // Check if we have a duplicated contact to populate
        if (state.contact) {
          this.contact = state.contact;
          if (this.contact) {
            this.populateForm(this.contact);
          }
        }
        this.contactForm.enable();
      } else if (modeParam === 'edit') {
        this.mode = SharedHelper.EditMode.EDIT;
        if (state.contact) {
          this.contact = state.contact;
          if (this.contact) {
            this.populateForm(this.contact);
          }
        }
        this.contactForm.enable();
      } else {
        // View mode
        this.mode = SharedHelper.EditMode.VIEW;
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

  private buildContactDto(): ContactDto {
    const formValue = this.contactForm.value;

    const contactDto: ContactDto = {
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      birthDate: formValue.birthDate,
      holderType: formValue.holderType,
    };

    // Add ID if editing
    if (this.mode === SharedHelper.EditMode.EDIT && this.contact?.id) {
      contactDto.id = this.contact.id;
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
        const existingEmail = PlatformHelper.getDefaultContactEmail(this.contact!);
        if (existingEmail?.id) {
          contactDto.emails[0].id = existingEmail.id;
          contactDto.emails[0].contactId = this.contact!.id;
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
        const existingPhone = PlatformHelper.getDefaultContactPhone(this.contact!);
        if (existingPhone?.id) {
          contactDto.phones[0].id = existingPhone.id;
          contactDto.phones[0].contactId = this.contact!.id;
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
        const existingAddress = PlatformHelper.getDefaultContactAddress(this.contact!);
        if (existingAddress?.id) {
          contactDto.addresses[0].id = existingAddress.id;
          contactDto.addresses[0].contactId = this.contact!.id;
        }
      }
    }

    return contactDto;
  }

  onCreate(): void {
    this.router.navigate(['/contact/edit', 'create'], {
      state: { mode: 'create' }
    });
  }

  // Navigation handlers
  onBack(): void {
    this.router.navigate(['/contacts']);
  }

  onCancel(): void {
    this.router.navigate(['/contacts']);
  }

  onSave(): void {
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
}
