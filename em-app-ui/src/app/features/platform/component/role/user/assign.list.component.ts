import { Component, Input } from "@angular/core";
import { RoleDto, UserDto, UserSearchCriteria } from "../../../api.platform.model";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { Observable } from "rxjs";
import { SearchResult } from "../../../../shared/api.shared.model";
import { Router } from "@angular/router";
import { MatDialog } from "@angular/material/dialog";
import { UserService } from "../../../service/user.service";
import { UserHelper } from "../../../helper/user.helper";

@Component({
  selector: 'app-role-user-assign-list',
  templateUrl: './assign.list.component.html',
  styleUrls: ['./assign.list.component.scss'],
  standalone: false
})
export class RoleUserAssignListComponent extends AbstractIndexComponent<UserDto> {

  displayedColumns: string[] = ['actions', 'username'];

  @Input()
  dto?: RoleDto;

  constructor(
    protected override router: Router,
    protected override dialog: MatDialog,
    protected service: UserService) {
    super(router, dialog);
    this.searchCriteria = UserHelper.createUserSearchCriteria() 
  }

  override search(): Observable<SearchResult<UserDto>> {
    const sc = this.searchCriteria as UserSearchCriteria;
    sc.assignableToRoleId = this.dto?.id;
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

  override ngOnInit(): void {
    super.ngOnInit();
    if (this.dto) {
      this.dto.roleUsers?.map(rp => rp.user)
        .filter(u => u !== undefined)
        .forEach(u => this.selection.setSelection(u));
    }
  }
}
