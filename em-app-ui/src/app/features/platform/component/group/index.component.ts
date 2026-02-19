import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { GroupService } from '../../service/group.service';
import { GroupDto } from '../../api.platform.model';
import { SearchResult } from '../../../shared/api.shared.model';
import { Observable } from 'rxjs';
import { AbstractIndexComponent } from '../../../shared/component/abstract-index.component';
import { GroupHelper } from '../../helper/group.helper';

@Component({
  selector: 'app-group-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class GroupIndexComponent extends AbstractIndexComponent<GroupDto>  {
  displayedColumns: string[] = ['name', 'description', 'holderType', 'actions'];
  GroupHelper = GroupHelper;

  constructor(
    protected override router: Router,
    private service: GroupService,
    protected override dialog: MatDialog
  ) {
    super(router, dialog);

    this.delete = (dto) => this.service.deleteGroup(dto);
  }

  protected override duplicateDto(dto: GroupDto): GroupDto {
    return GroupHelper.buildFormData(dto);
  }

  protected override getBaseRoute(): string {
    return '/groups';
  }

  override search(): Observable<SearchResult<GroupDto>> {
    return this.service.getGroups(this.searchCriteria);
  }
}
