import { Component } from '@angular/core';
import { GroupService } from '../../service/group.service';
import { GroupDto } from '../../api.platform.model';
import { AbstractIndexComponent } from '../../../shared/component/abstract-index.component';
import { GroupHelper } from '../../helper/group.helper';

@Component({
  selector: 'app-group-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class GroupIndexComponent extends AbstractIndexComponent<GroupDto>  {
  displayedColumns: string[] = ['name', 'description', 'ownerType', 'actions'];

  constructor(
    override helper: GroupHelper,
    private service: GroupService) {
    super(helper);

    this.delete = (dto) => this.service.deleteGroup(dto);
  }
}
