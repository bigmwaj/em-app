import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AccountService } from '../../service/account.service';
import { AccountDto, AccountStatusLvo } from '../../api.platform.model';
import { SearchResult } from '../../../shared/api.shared.model';
import { Observable } from 'rxjs';
import { AbstractIndexWithStatusComponent } from '../../../shared/component/abstract-index-with-status.component';
import { AccountHelper } from '../../helper/account.helper';

@Component({
  selector: 'app-account-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class AccountIndexComponent extends AbstractIndexWithStatusComponent<AccountStatusLvo, AccountDto>{

  displayedColumns: string[] = ['name', 'status', 'fullName', 'email', 'phone', 'address', 'actions'];
  override sortableFieldMap = new Map([
    ['name', 'name'],
    ['status', 'status']
  ]);

  override textSearchableFields = ['name', 'firstName', 'lastName', 'phone', 'email', 'address'];
  
  AccountHelper = AccountHelper;

  constructor(
    protected override router: Router,
    private service: AccountService,
    protected override dialog: MatDialog
  ) {
    super(router, dialog);

    this.searchCriteria = AccountHelper.createAccountSearchCriteria();

    this.delete = (dto) => this.service.deleteAccount(dto);
    this.changeStatus = (dto) => this.service.updateAccount(dto as AccountDto);
  }

  protected override duplicateDto(dto: AccountDto): AccountDto {
    return AccountHelper.buildFormData(dto);
  }

  protected override getBaseRoute(): string {
    return '/accounts';
  }

  override search(): Observable<SearchResult<AccountDto>> {
    return this.service.getAccounts(this.searchCriteria);
  }
}
