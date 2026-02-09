import { Component, OnInit } from '@angular/core';
import { ContactService } from '../../service/contact.service';
import { ContactDto, ContactSearchCriteria, createContactSearchCriteria } from '../../api.platform.model';
import { SearchResult, WhereClause, FilterOperator } from '../../../shared/api.shared.model';
import { CommonDataSource } from '../../../shared/common.datasource';

@Component({
  selector: 'app-contact-index',
  templateUrl: './contact-index.component.html',
  styleUrls: ['./contact-index.component.scss'],
  standalone: false
})
export class ContactIndexComponent extends CommonDataSource<ContactDto> implements OnInit {
  searchResult: SearchResult<ContactDto> = {} as SearchResult<ContactDto>;
  loading = true;
  error: string | null = null;
  searchCriteria: ContactSearchCriteria = createContactSearchCriteria();
  searchTerm: string = '';

  constructor(private contactService: ContactService) {
    super();
    this.searchCriteria.includeEmails = true;
    this.searchCriteria.includePhones = true;
    this.searchCriteria.includeAddresses = true;
  }

  override getKeyLabel(bean: ContactDto): string | number {
    return bean.id || `${bean.firstName} ${bean.lastName}`;
  }

  ngOnInit(): void {
    this.loadContacts();
  }

  loadContacts(): void {
    this.loading = true;
    this.error = null;

    // Apply search filter if search term exists
    if (this.searchTerm && this.searchTerm.trim()) {
      const whereClause: WhereClause = {
        name: 'firstName',
        oper: FilterOperator.LIKE,
        values: [this.searchTerm.trim()]
      };
      this.searchCriteria.filterByItems = [whereClause];
    } else {
      this.searchCriteria.filterByItems = [];
    }

    this.contactService.getContacts(this.searchCriteria).subscribe({
      next: (searchResult) => {
        this.searchResult = searchResult;
        this.loading = false;
        this.setData(searchResult.data);
      },
      error: (err) => {
        console.error('Failed to load contacts:', err);
        this.error = 'Failed to load contacts. Using sample data.';
        this.loading = false;
      }
    });
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.searchCriteria.filterByItems = [];
    this.loadContacts();
  }
  
  editContact(contact: ContactDto): void {
    console.log('Edit contact:', contact);
  }

  deleteContact(contact: ContactDto): void {
    console.log('Delete contact:', contact);
  }
}
