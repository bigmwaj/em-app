import { Component, EventEmitter, Input, Output } from "@angular/core";
import { RoleDto, RolePrivilegeDto } from "../../../api.platform.model";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { Router } from "@angular/router";
import { MatDialog } from "@angular/material/dialog";
import { RoleService } from "../../../service/role.service";
import { PageEvent } from "@angular/material/paginator";
import { PrivilegeHelper } from "../../../helper/privilege.helper";
import { AbstractSearchCriteria, SearchResult } from "../../../../shared/api.shared.model";
import { Observable, of } from "rxjs";
import { RolePrivilegeHelper } from "../../../helper/role-privilege.helper";

@Component({
  selector: 'app-role-privilege-assigned-list',
  templateUrl: './assigned.list.component.html',
  styleUrls: ['./assigned.list.component.scss'],
  standalone: false
})
export class RolePrivilegeAssignedListComponent extends AbstractIndexComponent<RolePrivilegeDto> {
  
  displayedColumns: string[] = ['select', 'name', 'description', 'actions'];

  @Output() onPrivilegeRemoved = new EventEmitter<RolePrivilegeDto>();

  @Input()
  dto?: RoleDto;

  @Input()
  isViewMode = false;

  constructor( protected override helper: RolePrivilegeHelper) {
    super(helper);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if (this.isViewMode) {
      this.displayedColumns = this.displayedColumns.filter(col => col !== 'select' && col !== 'actions');
    }
  }

  override loadData(): void {    
    this.searchCriteria.variables = { roleId: this.dto?.id };
    super.loadData();
  }

  override getKeyLabel(dto: RolePrivilegeDto): string | number {
    return dto.privilege?.id || '';
  }

  override equals(dto1: RolePrivilegeDto, dto2: RolePrivilegeDto): boolean {
    return dto1 === dto2 || (dto1.privilege?.id === dto2.privilege?.id);
  }

  isDeleted(rp: RolePrivilegeDto): boolean {
    return rp.retired === true;
  }

  isCreated(rp: RolePrivilegeDto): boolean {
    return rp.New === true;
  }

  /**
   * Call by RolePrivilegeAssignListComponent when a privilege is unchecked from the assign list.
   * @param privilege to remove from the role's rolePrivileges list and deselect from assignPrivilegesTable
   */
  removePrivilege(rp: RolePrivilegeDto) {
    if (rp.New === true) {
      this.onPrivilegeRemoved.emit(rp);
    } else {
      rp.retired = !rp.retired;
    }
  }

  override handlePageEvent(e: PageEvent) {
    this.searchCriteria.pageIndex = e.pageIndex;
    this.searchCriteria.pageSize = e.pageSize;
    this.loadData();
  }
}
