import { Component, Input } from "@angular/core";
import { GroupDto, RoleDto, RoleSearchCriteria} from "../../../api.platform.model";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { Router } from "@angular/router";
import { MatDialog } from "@angular/material/dialog";
import { RoleService } from "../../../service/role.service";
import { RoleHelper } from "../../../helper/role.helper";

@Component({
  selector: 'app-group-role-assign-list',
  templateUrl: './assign.list.component.html',
  styleUrls: ['./assign.list.component.scss'],
  standalone: false
})
export class GroupRoleAssignListComponent extends AbstractIndexComponent<RoleDto> {

  displayedColumns: string[] = ['select', 'name', 'description', 'actions'];

  @Input()
  dto?: GroupDto;

  constructor(
    protected override helper: RoleHelper) {
    super(helper);

    this.searchCriteria = this.helper.createRoleSearchCriteria();    
  }

  override getKeyLabel(dto: RoleDto): string | number {
    return dto.id || '';
  }

  override equals(dto1: RoleDto, dto2: RoleDto): boolean {
    return dto1 === dto2 || (dto1.id === dto2.id);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if (this.dto) {
      this.dto.groupRoles?.map(gr => gr.role)
        .filter(r => r !== undefined)
        .forEach(r => this.selection.setSelection(r));
    }
  }

  override loadData(): void {  
    const sc = this.searchCriteria as RoleSearchCriteria;
    sc.assignableToGroupId = this.dto?.id;
    super.loadData();
  }
}
