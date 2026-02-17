import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ContactService } from '../../service/contact.service';
import { ContactDto } from '../../api.platform.model';
import { SearchResult } from '../../../shared/api.shared.model';
import { PlatformHelper } from '../../platform.helper';
import { Observable } from 'rxjs';
import { AbstractIndexComponent } from '../../../shared/component/abstract-index.component';

@Component({
  selector: 'app-contact-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class ContactIndexComponent extends AbstractIndexComponent<ContactDto>  {
  displayedColumns: string[] = ['fullName', 'defaultEmail', 'defaultPhone', 'defaultAddress', 'actions'];
  PlatformHelper = PlatformHelper;
  
    constructor(
      protected override router: Router,
      private service: ContactService,
      protected override dialog: MatDialog
    ) {
      super(router, dialog);
  
      const searchCriteria = PlatformHelper.createDefaultSearchCriteria();
      searchCriteria.pageSize = 5;
  
      this.searchCriteria = searchCriteria;
  
      this.delete = (dto) => this.service.deleteContact(dto);
    }
  
    protected override duplicateDto(dto: ContactDto): ContactDto {
      return PlatformHelper.duplicateContact(dto);
    }
  
    protected override getBaseRoute(): string {
      return '/contacts';
    }
  
    override search(): Observable<SearchResult<ContactDto>> {
      return this.service.getContacts(this.searchCriteria);
    }
}
