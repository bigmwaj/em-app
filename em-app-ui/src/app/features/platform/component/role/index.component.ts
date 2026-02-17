import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { RoleService } from '../../service/role.service';
import { RoleDto } from '../../api.platform.model';
import { SearchResult } from '../../../shared/api.shared.model';
import { PlatformHelper } from '../../platform.helper';
import { Observable } from 'rxjs';
import { AbstractIndexComponent } from '../../../shared/component/abstract-index.component';

@Component({
  selector: 'app-role-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class RoleIndexComponent extends AbstractIndexComponent<RoleDto>  {
  displayedColumns: string[] = ['name', 'description', 'holderType', 'actions'];
  PlatformHelper = PlatformHelper;
  
    constructor(
      protected override router: Router,
      private service: RoleService,
      protected override dialog: MatDialog
    ) {
      super(router, dialog);
  
      const searchCriteria = PlatformHelper.createDefaultSearchCriteria();
      searchCriteria.pageSize = 5;
  
      this.searchCriteria = searchCriteria;
  
      this.delete = (dto) => this.service.deleteRole(dto);
    }
  
    protected override duplicateDto(dto: RoleDto): RoleDto {
      return PlatformHelper.duplicateRole(dto);
    }
  
    protected override getBaseRoute(): string {
      return '/roles';
    }
  
    override search(): Observable<SearchResult<RoleDto>> {
      return this.service.getRoles(this.searchCriteria);
    }
}
