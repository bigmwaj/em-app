import { Component, Input } from "@angular/core";
import { PrivilegeDto, PrivilegeSearchCriteria, RoleDto } from "../../../api.platform.model";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { PrivilegeService } from "../../../service/privilege.service";
import { Router } from "@angular/router";
import { MatDialog } from "@angular/material/dialog";
import { PrivilegeHelper } from "../../../helper/privilege.helper";

@Component({
  selector: 'app-role-privilege-assign-list',
  templateUrl: './assign.list.component.html',
  styleUrls: ['./assign.list.component.scss'],
  standalone: false
})
export class RolePrivilegeAssignListComponent extends AbstractIndexComponent<PrivilegeDto> {

  displayedColumns: string[] = ['select', 'name', 'description', 'actions'];

  @Input()
  dto?: RoleDto;

  constructor(
    protected override helper: PrivilegeHelper) {
    super(helper);

    this.searchCriteria = this.helper.createPrivilegeSearchCriteria();

  }

  override getKeyLabel(dto: PrivilegeDto): string | number {
    return dto.id || '';
  }

  override equals(dto1: PrivilegeDto, dto2: PrivilegeDto): boolean {
    return dto1 === dto2 || (dto1.id === dto2.id);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if (this.dto) {
      this.dto.rolePrivileges?.map(rp => rp.privilege)
        .filter(p => p !== undefined)
        .forEach(p => this.selection.setSelection(p));
    }
  }

  override loadData(): void {  
    const sc = this.searchCriteria as PrivilegeSearchCriteria;
    sc.assignableToRoleId = this.dto?.id;
    super.loadData();
  }
}
