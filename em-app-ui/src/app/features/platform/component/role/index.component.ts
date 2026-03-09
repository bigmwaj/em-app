import { Component } from '@angular/core';
import { RoleService } from '../../service/role.service';
import { RoleDto } from '../../api.platform.model';
import { AbstractIndexComponent } from '../../../shared/component/abstract-index.component';
import { RoleHelper } from '../../helper/role.helper';

@Component({
  selector: 'app-role-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class RoleIndexComponent extends AbstractIndexComponent<RoleDto> {
  displayedColumns: string[] = ['name', 'description', 'ownerType', 'actions'];

  constructor(
    override helper: RoleHelper,
    private service: RoleService ) {
    super(helper);

    this.delete = (dto) => this.service.deleteRole(dto);
  }
}
