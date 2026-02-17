import { FormBuilder } from '@angular/forms';
import { AbstractEditComponent } from './abstract-edit.component';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ChangeStatusDialogComponent, ChangeStatusDialogData } from './change-status-dialog.component';
import { Observable } from 'rxjs';
import { AbstractStatusTrackingDto } from '../api.shared.model';
import { Component } from '@angular/core';

@Component({
  selector: 'app-account-edit',
  template: '',
  styles: [''],
  standalone: false
})
export abstract class AbstractEditWithStatusComponent<T, S> extends AbstractEditComponent<T> {
  statusOptions: S[] = [];
  protected changeStatus?: (dto: AbstractStatusTrackingDto<S>) => Observable<AbstractStatusTrackingDto<S>>;

  constructor(
    protected override fb: FormBuilder,
    protected override router: Router,
    protected override route: ActivatedRoute,
    protected override dialog: MatDialog
  ) {
    super(fb, router, route, dialog);
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
        changeStatusAction: this.changeStatus
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
