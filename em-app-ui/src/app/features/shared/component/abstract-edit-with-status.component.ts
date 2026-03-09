import { AbstractEditComponent } from './abstract-edit.component';
import { ChangeStatusDialogComponent, ChangeStatusDialogData } from './change-status-dialog.component';
import { Observable } from 'rxjs';
import { AbstractStatusTrackingDto } from '../api.shared.model';
import { Component } from '@angular/core';
import { BaseHelper } from '../base.helper';

@Component({
  selector: 'app-abstract-edit-with-status',
  template: '',
  styles: [''],
  standalone: false
})
export abstract class AbstractEditWithStatusComponent<T extends AbstractStatusTrackingDto<S>, S> extends AbstractEditComponent<T> {
  statusOptions: S[] = [];
  
  protected changeStatus?: (dto: AbstractStatusTrackingDto<S>) => Observable<AbstractStatusTrackingDto<S>>;

  constructor( protected override helper: BaseHelper<T> ) {
    super(helper);
  }

  get showChangeStatusButton(): boolean {
    return this.isViewMode;
  }

  onChangeStatus(): void {
    if (!this.dto) {
      throw new Error('No data to change status');
    }

    const dialogRef = this.dialog.open(ChangeStatusDialogComponent, {
      width: '400px',
      data: {
        dto: this.dto,
        changeStatusAction: this.changeStatus,
        statusOptions: this.statusOptions
      } as ChangeStatusDialogData<S>
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.dto) {
        // Update the status
        //this.dto.status = result;
        //this.accountForm.patchValue({ status: result });

        // In a real application, you would reload from the server
        // For now, just update the form to reflect the change
      }
    });
  }
}
