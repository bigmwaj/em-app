import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DeadLetterDto, DeadLetterStatusLvo } from '../../api.platform.model';
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';
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
  DeadLetterHelper = DeadLetterHelper;

  constructor(
    protected override fb: FormBuilder,
    protected override router: Router,
    protected override route: ActivatedRoute,
    protected override dialog: MatDialog,
    private service: DeadLetterService,
  ) {
    super(fb, router, route, dialog);

    this.delete = (dto) => this.service.deleteDeadLetter(dto);
    this.update = (dto) => this.service.updateDeadLetter(dto);
    this.buildFormData = (dto) => DeadLetterHelper.buildFormData(dto);
  }

  protected override getBaseRoute(): string {
    return '/platform/dead-letters';
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
      editAction: this.editAction,
      id: formValue.id,
      status: formValue.status ?? DeadLetterStatusLvo.RETRY,
      eventName: formValue.eventName,
      message: formValue.message,
      errorMessage: formValue.errorMessage
    };
    return dto;
  }
}
