import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DeadLetterDto, DeadLetterStatusLvo } from '../../api.platform.model';
import { SearchResult } from '../../../shared/api.shared.model';
import { Observable } from 'rxjs';
import { DeadLetterHelper } from '../../helper/dead-letter.helper';
import { DeadLetterService } from '../../service/dead-letter.service';
import { AbstractIndexWithStatusComponent } from '../../../shared/component/abstract-index-with-status.component';

@Component({
  selector: 'app-dead-letter-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class DeadLetterIndexComponent extends AbstractIndexWithStatusComponent<DeadLetterStatusLvo,  DeadLetterDto>  {
  displayedColumns: string[] = ['eventName', 'createdDate', 'errorMessage', 'createdBy', 'status', 'actions'];
  DeadLetterHelper = DeadLetterHelper;

  constructor(
    protected override router: Router,
    private service: DeadLetterService,
    protected override dialog: MatDialog
  ) {
    super(router, dialog);

    this.delete = (dto) => this.service.deleteDeadLetter(dto);
  }

  protected override duplicateDto(dto: DeadLetterDto): DeadLetterDto {
    return DeadLetterHelper.buildFormData(dto);
  }

  protected override getBaseRoute(): string {
    return '/platform/dead-letters';
  }

  override search(): Observable<SearchResult<DeadLetterDto>> {
    return this.service.getDeadLetters(this.searchCriteria);
  }
}
