import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ContactService } from '../../service/contact.service';
import { ContactDto, ContactSearchCriteria, createContactSearchCriteria } from '../../api.platform.model';
import { SearchResult, FilterOperator, WhereClause } from '../../../shared/api.shared.model';
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
  message = "";
  error: string | null = null;
  searchCriteria: ContactSearchCriteria = createContactSearchCriteria();
  displayedColumns: string[] = ['firstName', 'lastName', 'mainEmail', 'mainPhone', 'mainAddress', 'actions'];
  
  searchForm = new FormGroup({
    firstName: new FormControl(''),
    lastName: new FormControl(''),
    email: new FormControl('')
  });

  constructor(private contactService: ContactService) { 
    super();
    this.searchCriteria.includeEmails = true;
    this.searchCriteria.includePhones = true;
    this.searchCriteria.includeAddresses = true;
  }

  override getKeyLabel(bean: ContactDto): string | number {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    this.loadContacts();
  }

  loadContacts(): void {
    this.loading = true;
    this.error = null;

    this.contactService.getContacts(this.searchCriteria).subscribe({
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

  onSearch(): void {
    const filters: WhereClause[] = [];
    
    if (this.searchForm.value.firstName) {
      filters.push({ 
        name: 'firstName', 
        oper: FilterOperator.LIKE, 
        values: [this.searchForm.value.firstName] 
      });
    }
    
    if (this.searchForm.value.lastName) {
      filters.push({ 
        name: 'lastName', 
        oper: FilterOperator.LIKE, 
        values: [this.searchForm.value.lastName] 
      });
    }
    
    if (this.searchForm.value.email) {
      filters.push({ 
        name: 'email', 
        oper: FilterOperator.LIKE, 
        values: [this.searchForm.value.email] 
      });
    }
    
    this.searchCriteria.filterByItems = filters;
    this.loadContacts();
  }

  resetSearch(): void {
    this.searchForm.reset();
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
