import { Component } from '@angular/core';
import { AccountService } from '../../service/account.service';
import { AccountDto, AccountStatusLvo } from '../../api.platform.model';
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
  
  constructor(
    override helper: AccountHelper,
    private service: AccountService ) {
    super(helper);

    this.searchCriteria = this.helper.createAccountSearchCriteria();

    this.delete = (dto) => this.service.deleteAccount(dto);
    this.changeStatus = (dto) => this.service.updateAccount(dto as AccountDto);
  }
}
