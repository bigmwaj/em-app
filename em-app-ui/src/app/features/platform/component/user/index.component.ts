import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../service/user.service';
import { UserDto, UserStatusLvo } from '../../api.platform.model';
import { SearchResult } from '../../../shared/api.shared.model';
import { Observable } from 'rxjs';
import { AbstractIndexWithStatusComponent } from '../../../shared/component/abstract-index-with-status.component';
import { UserHelper } from '../../helper/user.helper';
import { ContactHelper } from '../../helper/contact.helper';

@Component({
  selector: 'app-user-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class UserIndexComponent extends AbstractIndexWithStatusComponent<UserStatusLvo, UserDto>  {
  displayedColumns: string[] = ['fullName', 'status', 'username', 'defaultEmail', 'defaultPhone', 'actions'];
  override textSearchableFields = ['fullName', 'phone', 'email', 'address'];
  override sortableFieldMap = new Map([
      ['fullName', 'firstName'],
      ['status', 'status']
    ]);

  UserHelper = UserHelper;
  ContactHelper = ContactHelper;

  constructor(
    protected override router: Router,
    private service: UserService,
    protected override dialog: MatDialog
  ) {
    super(router, dialog);
    this.delete = (dto) => this.service.deleteUser(dto);
    this.changeStatus = (dto) => this.service.updateUser(dto as UserDto);
  }

  protected override duplicateDto(dto: UserDto): UserDto {
    return UserHelper.buildFormData(dto);
  }

  protected override getBaseRoute(): string {
    return '/users';
  }

  override search(): Observable<SearchResult<UserDto>> {
    return this.service.getUsers(this.searchCriteria);
  }
}
