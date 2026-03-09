import { Component } from '@angular/core';
import { UserService } from '../../service/user.service';
import { UserDto, UserStatusLvo } from '../../api.platform.model';
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

  constructor(
    public contactHelper: ContactHelper,
    protected override helper: UserHelper,
    private service: UserService ) {

    super(helper);
    this.delete = (dto) => this.service.deleteUser(dto);
    this.changeStatus = (dto) => this.service.changeUserStatus(dto as UserDto);
  }
}
