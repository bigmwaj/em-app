import { Component, OnInit } from '@angular/core';
import { ContactService } from '../../service/contact.service';
import { ContactDto } from '../../api.platform.model';
import { createDefaultSearchCriteria, DefaultSearchCriteria, SearchResult, FilterOperator } from '../../../shared/api.shared.model';
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
  searchCriteria: DefaultSearchCriteria = createDefaultSearchCriteria();
  displayedColumns: string[] = ['firstName', 'lastName', 'mainEmail', 'mainPhone', 'mainAddress', 'actions'];
  searchText = '';

  constructor(private contactService: ContactService) { super(); }

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
        this.error = 'Failed to load contacts. Using sample data.';
        this.loading = false;
      }
    });
  }

  editContact(contact: ContactDto): void {
    console.log('Edit contact:', contact);
  }

  deleteContact(contact: ContactDto): void {
    console.log('Delete contact:', contact);
  }

  onSearch(): void {
    this.searchCriteria = createDefaultSearchCriteria();
    
    if (this.searchText && this.searchText.trim()) {
      this.searchCriteria.filterByItems = [{
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
