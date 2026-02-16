import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ContactService } from '../../service/contact.service';
import { ContactDto } from '../../api.platform.model';
import { createDefaultSearchCriteria, DefaultSearchCriteria, SearchResult, FilterOperator } from '../../../shared/api.shared.model';
import { CommonDataSource } from '../../../shared/common.datasource';
import { PlatformHelper } from '../../platform.helper';
import { PageEvent } from '@angular/material/paginator';
import { Subject, takeUntil } from 'rxjs';
import { ContactDeleteDialogComponent } from './delete-dialog.component';

@Component({
  selector: 'app-contact-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class ContactIndexComponent extends CommonDataSource<ContactDto> implements OnInit, OnDestroy {
  searchResult: SearchResult<ContactDto> = {} as SearchResult<ContactDto>;
  loading = true;
  error: string | null = null;
  searchCriteria: DefaultSearchCriteria = createDefaultSearchCriteria();
  displayedColumns: string[] = ['fullName', 'holderType', 'defaultEmail', 'defaultPhone', 'defaultAddress', 'actions'];
  searchText = '';
  PlatformHelper = PlatformHelper;
  private destroy$ = new Subject<void>();

  constructor(
    private contactService: ContactService,
    private router: Router,
    private dialog: MatDialog
  ) {
    super();
    this.searchCriteria.pageSize = 5;
  }

  override getKeyLabel(bean: ContactDto): string | number {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    this.loadContacts();
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  handlePageEvent(e: PageEvent) {
    this.searchCriteria.pageIndex = e.pageIndex;
    this.searchCriteria.pageSize = e.pageSize;  
    this.loadContacts();
  }

  loadContacts(): void {
    this.loading = true;
    this.error = null;

    this.contactService.getContacts(this.searchCriteria).pipe(takeUntil(this.destroy$)).subscribe({
      next: (searchResult) => {
        this.searchResult = searchResult;
        this.loading = false;
        this.setData(searchResult.data);
      },
      error: (err) => {
        console.error('Failed to load contacts:', err);
        this.error = 'Failed to load contacts. Please try again.';
        this.loading = false;
      }
    });
  }

  /**
   * Navigate to create contact page
   */
  createContact(): void {
    this.router.navigate(['/contacts/edit', 'create'], {
      state: { mode: 'create' }
    });
  }

  /**
   * Navigate to view contact page
   */
  viewContact(contact: ContactDto): void {
    this.router.navigate(['/contacts/edit', 'view'], {
      state: { mode: 'view', contact: contact }
    });
  }

  /**
   * Navigate to edit contact page
   */
  editContact(contact: ContactDto): void {
    this.router.navigate(['/contacts/edit', 'edit'], {
      state: { mode: 'edit', contact: contact }
    });
  }

  /**
   * Duplicate contact and navigate to create mode
   */
  duplicateContact(contact: ContactDto): void {
    const duplicatedContact = PlatformHelper.duplicateContact(contact);
    this.router.navigate(['/contacts/edit', 'create'], {
      state: { mode: 'create', contact: duplicatedContact }
    });
  }

  /**
   * Open delete confirmation dialog
   */
  deleteContact(contact: ContactDto): void {
    const dialogRef = this.dialog.open(ContactDeleteDialogComponent, {
      width: '400px',
      data: { contact: contact }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Reload the contacts list after successful deletion
        this.loadContacts();
      }
    });
  }

  onSearch(): void {
    this.searchCriteria = createDefaultSearchCriteria();

    if (this.searchText && this.searchText.trim()) {
      this.searchCriteria.whereClauses = [{
        name: 'firstName',
        oper: FilterOperator.LIKE,
        values: [this.searchText.trim()]
      }];
    }

    this.loadContacts();
  }

  onClearSearch(): void {
    this.searchText = '';
    this.searchCriteria = createDefaultSearchCriteria();
    this.loadContacts();
  }
}
