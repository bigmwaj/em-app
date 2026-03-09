import { Component, EventEmitter, Input, Output } from "@angular/core";
import { GroupDto, GroupRoleDto } from "../../../api.platform.model";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { Router } from "@angular/router";
import { MatDialog } from "@angular/material/dialog";
import { GroupService } from "../../../service/group.service";
import { GroupRoleHelper } from "../../../helper/group-role.helper";

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
    protected override helper: GroupRoleHelper) {

    super(helper);
  }

  override ngOnInit(): void {  
    super.ngOnInit();
    if (this.isViewMode) {
      this.displayedColumns = this.displayedColumns.filter(col => col !== 'select' && col !== 'actions');
    }
  }

  override loadData(): void {    
    this.searchCriteria.variables = { groupId: this.dto?.id };
    super.loadData();
  }

  override getKeyLabel(dto: GroupRoleDto): string | number {
    return dto.role?.id || '';
  }

  override equals(dto1: GroupRoleDto, dto2: GroupRoleDto): boolean {
    return dto1 === dto2 || (dto1.role?.id === dto2.role?.id);
  }

  isDeleted(gr: GroupRoleDto): boolean {
    return gr.checked === false;
  }

  isCreated(gr: GroupRoleDto): boolean {
    return gr.checked === true;
  }

  /**
   * Call by GroupRoleAssignedListComponent when a role is unchecked from the assign list.
   * @param role to remove from the group's groupRoles list and deselect from assignRolesTable
   */
  removeRole(gr: GroupRoleDto) {
    if (gr.checked === true) {
      this.onRoleRemoved.emit(gr);
    } else {
      gr.checked = false;
    }
  }
}
