import { Component, EventEmitter, Input, Output } from "@angular/core";
import { RoleDto, RolePrivilegeDto } from "../../../api.platform.model";
import { Observable } from "rxjs";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { Router } from "@angular/router";
import { MatDialog } from "@angular/material/dialog";
import { EditActionLvo, SearchResult } from "../../../../shared/api.shared.model";
import { RoleService } from "../../../service/role.service";

@Component({
  selector: 'app-role-privilege-assigned-list',
  templateUrl: './assigned.list.component.html',
  styleUrls: ['./assigned.list.component.scss'],
  standalone: false
})
export class RolePrivilegeAssignedListComponent extends AbstractIndexComponent<RolePrivilegeDto> {
  displayedColumns: string[] = ['select', 'name', 'description','actions'];

  @Output() onPrivilegeRemoved = new EventEmitter<RolePrivilegeDto>();

  @Input()
  dto?: RoleDto;

  constructor(
    protected override router: Router,
    protected override dialog: MatDialog,
    protected service: RoleService) {
    super(router, dialog);
  }

  override search(): Observable<SearchResult<RolePrivilegeDto>> {
    return this.service.getRolePrivileges(this.dto?.id || 0);
  }

  protected override getBaseRoute(): string {
    throw new Error("Method not implemented.");
  }

  protected override duplicateDto(dto: RolePrivilegeDto): RolePrivilegeDto {
    throw new Error("Method not implemented.");
  }

  override getKeyLabel(dto: RolePrivilegeDto): string | number {
    return dto.privilege?.id || '';
  }

  override equals(dto1: RolePrivilegeDto, dto2: RolePrivilegeDto): boolean {
    return dto1 === dto2 || (dto1.privilege?.id === dto2.privilege?.id);
  }

  isDeleted(rp: RolePrivilegeDto): boolean {
    return rp.editAction === EditActionLvo.DELETE;
  }

  isCreated(rp: RolePrivilegeDto): boolean {
    return rp.editAction === EditActionLvo.CREATE;
  }

  /**
   * Call by RolePrivilegeAssignListComponent when a privilege is unchecked from the assign list.
   * @param privilege to remove from the role's rolePrivileges list and deselect from assignPrivilegesTable
   */
  removePrivilege(rp: RolePrivilegeDto) {
    if( rp.editAction === EditActionLvo.CREATE) {
      this.onPrivilegeRemoved.emit(rp);
    } else {
      rp.editAction = rp.editAction === EditActionLvo.DELETE ? EditActionLvo.NONE : EditActionLvo.DELETE;
    }    
  }
}
