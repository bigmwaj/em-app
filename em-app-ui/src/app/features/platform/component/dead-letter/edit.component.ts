import { Component } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { DeadLetterDto, DeadLetterStatusLvo } from '../../api.platform.model';
import { DeadLetterHelper } from '../../helper/dead-letter.helper';
import { DeadLetterService } from '../../service/dead-letter.service';
import { AbstractEditWithStatusComponent } from '../../../shared/component/abstract-edit-with-status.component';

@Component({
  selector: 'app-dead-letter-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false,
})
export class DeadLetterEditComponent extends AbstractEditWithStatusComponent<DeadLetterDto, DeadLetterStatusLvo> {

  constructor(
    protected override helper: DeadLetterHelper,
    private service: DeadLetterService ) {

    super(helper);

    this.delete = (dto) => this.service.deleteDeadLetter(dto);
    this.update = (dto) => this.service.updateDeadLetter(dto);
    this.changeStatus = (dto) => this.service.changeDeadLetterStatus(dto as DeadLetterDto);
    this.statusOptions = Object.values(DeadLetterStatusLvo);
  }

  protected override initializeForms(): FormGroup[] {
    this.mainForm = this.fb.group({
      createdDate: [this.dto?.createdDate],
      createdBy: [this.dto?.createdBy],
      id: [this.dto?.id],
      eventName: [this.dto?.eventName],
      message: [this.dto?.message],
      errorMessage: [this.dto?.errorMessage],
      status: [this.dto?.status ?? DeadLetterStatusLvo.RETRY]
    });
    return [this.mainForm];
  }

  protected buildDtoFromForms(): DeadLetterDto {
    const formValue = this.mainForm.value;

    const dto: DeadLetterDto = {
      New: this.isCreateMode,
      id: formValue.id,
      status: formValue.status ?? DeadLetterStatusLvo.RETRY,
      eventName: formValue.eventName,
      message: formValue.message,
      errorMessage: formValue.errorMessage
    };
    return dto;
  }
}
