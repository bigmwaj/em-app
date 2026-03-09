import { Component } from '@angular/core';
import { DeadLetterDto, DeadLetterStatusLvo } from '../../api.platform.model';
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
  displayedColumns: string[] = ['eventName', 'updatedDate', 'updatedBy', 'errorMessage', 'status', 'actions'];
  
  constructor(
    override helper: DeadLetterHelper,
    private service: DeadLetterService ) {

    super(helper);

    this.delete = (dto) => this.service.deleteDeadLetter(dto);
    this.changeStatus = (dto) => this.service.changeDeadLetterStatus(dto as DeadLetterDto);
    this.statusOptions = Object.values(DeadLetterStatusLvo);
  }
}
