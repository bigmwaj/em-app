import { Component, EventEmitter, Input, Output } from "@angular/core";
import { GroupDto, GroupRoleDto } from "../../../api.platform.model";
import { Observable, of } from "rxjs";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { Router } from "@angular/router";
import { MatDialog } from "@angular/material/dialog";
import { EditActionLvo, SearchInfos, SearchResult } from "../../../../shared/api.shared.model";
import { GroupService } from "../../../service/group.service";

@Component({
  selector: 'app-group-role-assigned-list',
  templateUrl: './assigned.list.component.html',
  styleUrls: ['./assigned.list.component.scss'],
  standalone: false
})
export class GroupRoleAssignedListComponent extends AbstractIndexComponent<GroupRoleDto> {
  displayedColumns: string[] = ['select', 'name', 'description', 'actions'];

  @Output() onRoleRemoved = new EventEmitter<GroupRoleDto>();

  @Input()
  dto?: GroupDto;

  @Input()
  isViewMode = false;

  constructor(
    protected override router: Router,
    protected override dialog: MatDialog,
    protected service: GroupService) {
    super(router, dialog);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if (this.isViewMode) {
      this.displayedColumns = this.displayedColumns.filter(col => col !== 'select' && col !== 'actions');
    }
  }

  override search(): Observable<SearchResult<GroupRoleDto>> {
    if (this.dto?.id) {
      return this.service.getGroupRoles(this.dto.id)
    }
    return of({ data: [], searchInfos: {} as SearchInfos } as SearchResult<GroupRoleDto>);
  }

  protected override getBaseRoute(): string {
    throw new Error("Method not implemented.");
  }

  protected override duplicateDto(dto: GroupRoleDto): GroupRoleDto {
    throw new Error("Method not implemented.");
  }

  override getKeyLabel(dto: GroupRoleDto): string | number {
    return dto.role?.id || '';
  }

  override equals(dto1: GroupRoleDto, dto2: GroupRoleDto): boolean {
    return dto1 === dto2 || (dto1.role?.id === dto2.role?.id);
  }

  isDeleted(gr: GroupRoleDto): boolean {
    return gr.editAction === EditActionLvo.DELETE;
  }

  isCreated(gr: GroupRoleDto): boolean {
    return gr.editAction === EditActionLvo.CREATE;
  }

  /**
   * Call by GroupRoleAssignedListComponent when a role is unchecked from the assign list.
   * @param role to remove from the group's groupRoles list and deselect from assignRolesTable
   */
  removeRole(gr: GroupRoleDto) {
    if (gr.editAction === EditActionLvo.CREATE) {
      this.onRoleRemoved.emit(gr);
    } else {
      gr.editAction = gr.editAction === EditActionLvo.DELETE ? EditActionLvo.NONE : EditActionLvo.DELETE;
    }
  }
}
