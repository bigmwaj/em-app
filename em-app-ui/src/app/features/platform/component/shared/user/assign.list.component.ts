import { Component, Input } from "@angular/core";
import { UserDto, UserSearchCriteria } from "../../../api.platform.model";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { Observable } from "rxjs";
import { SearchResult } from "../../../../shared/api.shared.model";
import { Router } from "@angular/router";
import { MatDialog } from "@angular/material/dialog";
import { UserService } from "../../../service/user.service";
import { UserHelper } from "../../../helper/user.helper";
import { ContactHelper } from "../../../helper/contact.helper";

@Component({
  selector: 'app-shared-user-assign-list',
  templateUrl: './assign.list.component.html',
  styleUrls: ['./assign.list.component.scss'],
  standalone: false
})
export class SharedUserAssignListComponent extends AbstractIndexComponent<UserDto> {

  displayedColumns: string[] = ['select', 'fullName', 'username', 'defaultEmail', 'defaultPhone', 'defaultAddress'];

  @Input()
  ownerId?: number;
  
  ContactHelper = ContactHelper;

  constructor(
    protected override router: Router,
    protected override dialog: MatDialog,
    protected service: UserService) {
    super(router, dialog);
    this.searchCriteria = UserHelper.createUserSearchCriteria() 
  }

  override search(): Observable<SearchResult<UserDto>> {
    const sc = this.searchCriteria as UserSearchCriteria;
    sc.assignableToRoleId = this.ownerId;
    return this.service.getUsers(this.searchCriteria);
  }

  protected override getBaseRoute(): string {
    throw new Error("Method not implemented.");
  }

  protected override duplicateDto(dto: UserDto): UserDto {
    throw new Error("Method not implemented.");
  }

  override getKeyLabel(dto: UserDto): string | number {
    return dto.id || '';
  }

  override equals(dto1: UserDto, dto2: UserDto): boolean {
    return dto1 === dto2 || (dto1.id === dto2.id);
  }
}
