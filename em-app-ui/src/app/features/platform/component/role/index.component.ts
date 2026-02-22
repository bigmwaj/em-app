import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { RoleService } from '../../service/role.service';
import { RoleDto } from '../../api.platform.model';
import { SearchResult } from '../../../shared/api.shared.model';
import { Observable } from 'rxjs';
import { AbstractIndexComponent } from '../../../shared/component/abstract-index.component';
import { RoleHelper } from '../../helper/role.helper';

@Component({
  selector: 'app-role-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class RoleIndexComponent extends AbstractIndexComponent<RoleDto> {
  displayedColumns: string[] = ['name', 'description', 'holderType', 'actions'];

  RoleHelper = RoleHelper;

  constructor(
    protected override router: Router,
    private service: RoleService,
    protected override dialog: MatDialog
  ) {
    super(router, dialog);

    this.delete = (dto) => this.service.deleteRole(dto);
  }

  protected override duplicateDto(dto: RoleDto): RoleDto {
    return RoleHelper.buildFormData(dto);
  }

  protected override getBaseRoute(): string {
    return '/roles';
  }

  override search(): Observable<SearchResult<RoleDto>> {
    return this.service.getRoles(this.searchCriteria);
  }

  protected override prepareEdit(dto: RoleDto): RoleDto {
    if (!dto.rolePrivileges) {
      if (!dto.rolePrivileges) {
        dto.rolePrivileges = [];
      }
    }
    return dto;
  }

  protected override prepareView(dto: RoleDto): RoleDto {
    if (!dto.rolePrivileges) {
      dto.rolePrivileges = [];
    }
    return dto;
  }
}
