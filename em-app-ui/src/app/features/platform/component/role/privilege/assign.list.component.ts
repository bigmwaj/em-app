import { Component, Input } from "@angular/core";
import { PrivilegeDto, PrivilegeSearchCriteria, RoleDto } from "../../../api.platform.model";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { Observable } from "rxjs";
import { SearchResult } from "../../../../shared/api.shared.model";
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
    protected override router: Router,
    protected override dialog: MatDialog,
    protected service: PrivilegeService) {
    super(router, dialog);

    this.searchCriteria = PrivilegeHelper.createPrivilegeSearchCriteria();
    
  }

  override search(): Observable<SearchResult<PrivilegeDto>> {
    const sc = this.searchCriteria as PrivilegeSearchCriteria;
    sc.assignableToRoleId = this.dto?.id;
    return this.service.getPrivileges(this.searchCriteria);
  }

  protected override getBaseRoute(): string {
    throw new Error("Method not implemented.");
  }

  protected override duplicateDto(dto: PrivilegeDto): PrivilegeDto {
    throw new Error("Method not implemented.");
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
}
