import { Component, Input } from "@angular/core";
import { GroupDto, RoleDto, RoleSearchCriteria} from "../../../api.platform.model";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { Observable } from "rxjs";
import { SearchResult } from "../../../../shared/api.shared.model";
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
    protected override router: Router,
    protected override dialog: MatDialog,
    protected service: RoleService) {
    super(router, dialog);

    this.searchCriteria = RoleHelper.createRoleSearchCriteria();
    
  }

  override search(): Observable<SearchResult<RoleDto>> {
    const sc = this.searchCriteria as RoleSearchCriteria;
    sc.assignableToGroupId = this.dto?.id;
    return this.service.getRoles(this.searchCriteria);
  }

  protected override getBaseRoute(): string {
    throw new Error("Method not implemented.");
  }

  protected override duplicateDto(dto: RoleDto): RoleDto {
    throw new Error("Method not implemented.");
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
}
