import { Component, EventEmitter, Input, Output } from "@angular/core";
import { RoleDto, RoleUserDto } from "../../../api.platform.model";
import { Observable } from "rxjs";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { Router } from "@angular/router";
import { MatDialog } from "@angular/material/dialog";
import { EditActionLvo, SearchResult } from "../../../../shared/api.shared.model";
import { RoleService } from "../../../service/role.service";

@Component({
  selector: 'app-role-user-assigned-list',
  templateUrl: './assigned.list.component.html',
  styleUrls: ['./assigned.list.component.scss'],
  standalone: false
})
export class RoleUserAssignedListComponent extends AbstractIndexComponent<RoleUserDto> {
  displayedColumns: string[] = ['actions', 'username'];

  @Output() onUserRemoved = new EventEmitter<RoleUserDto>();

  @Input()
  dto?: RoleDto;

  constructor(
    protected override router: Router,
    protected override dialog: MatDialog,
    protected service: RoleService) {
    super(router, dialog);
  }

  override search(): Observable<SearchResult<RoleUserDto>> {
    return this.service.getRoleUsers(this.dto?.id || 0);
  }

  protected override getBaseRoute(): string {
    throw new Error("Method not implemented.");
  }

  protected override duplicateDto(dto: RoleUserDto): RoleUserDto {
    throw new Error("Method not implemented.");
  }

  override getKeyLabel(dto: RoleUserDto): string | number {
    return dto?.user?.id || '';
  }

  override equals(dto1: RoleUserDto, dto2: RoleUserDto): boolean {
    return dto1 === dto2 || (dto1?.user?.id === dto2?.user?.id);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if (this.dto?.roleUsers) {
      this.setData(this.dto.roleUsers);
    }
  }

  isDeleted(rp: RoleUserDto): boolean {
    return rp.editAction === EditActionLvo.DELETE;
  }

  isCreated(rp: RoleUserDto): boolean {
    return rp.editAction === EditActionLvo.CREATE;
  }

  /**
   * Call by RoleUserAssignListComponent when a user is unchecked from the assign list.
   * @param user to remove from the role's roleUsers list and deselect from assignUsersTable
   */
  removeUser(ur: RoleUserDto) {
    if( ur.editAction === EditActionLvo.CREATE) {
      this.onUserRemoved.emit(ur);
    } else {
      ur.editAction = ur.editAction === EditActionLvo.DELETE ? EditActionLvo.NONE : EditActionLvo.DELETE;
    }   
  }
}
