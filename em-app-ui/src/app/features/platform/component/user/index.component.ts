import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../service/user.service';
import { UserDto, UserStatusLvo } from '../../api.platform.model';
import { SearchResult } from '../../../shared/api.shared.model';
import { PlatformHelper } from '../../platform.helper';
import { Observable } from 'rxjs';
import { AbstractIndexWithStatusComponent } from '../../../shared/component/abstract-index-with-status.component';

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

  PlatformHelper = PlatformHelper;

  constructor(
    protected override router: Router,
    private service: UserService,
    protected override dialog: MatDialog
  ) {
    super(router, dialog);

    const accountSearchCriteria = PlatformHelper.createDefaultSearchCriteria();
    accountSearchCriteria.pageSize = 5;

    this.searchCriteria = accountSearchCriteria;
    this.delete = (dto) => this.service.deleteUser(dto);
    this.changeStatus = (dto) => this.service.updateUser(dto as UserDto);
  }

  protected override duplicateDto(dto: UserDto): UserDto {
    return PlatformHelper.duplicateUser(dto);
  }

  protected override getBaseRoute(): string {
    return '/users';
  }

  override search(): Observable<SearchResult<UserDto>> {
    return this.service.getUsers(this.searchCriteria);
  }
}
