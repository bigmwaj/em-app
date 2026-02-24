import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { SearchResult } from '../../../shared/api.shared.model';
import { AbstractIndexComponent } from '../../../shared/component/abstract-index.component';
import { ContactDto } from '../../api.platform.model';
import { ContactHelper } from '../../helper/contact.helper';
import { ContactService } from '../../service/contact.service';

@Component({
  selector: 'app-contact-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class ContactIndexComponent extends AbstractIndexComponent<ContactDto> {
  displayedColumns: string[] = ['fullName', 'defaultEmail', 'defaultPhone', 'defaultAddress', 'actions'];
  ContactHelper = ContactHelper;

  constructor(
    protected override router: Router,
    private service: ContactService,
    protected override dialog: MatDialog
  ) {
    super(router, dialog);

    this.delete = (dto) => this.service.deleteContact(dto);
  }

  protected override duplicateDto(dto: ContactDto): ContactDto {
    return ContactHelper.buildFormData(dto);
  }

  protected override getBaseRoute(): string {
    return '/platform/contacts';
  }

  override search(): Observable<SearchResult<ContactDto>> {
    return this.service.getContacts(this.searchCriteria);
  }
}
