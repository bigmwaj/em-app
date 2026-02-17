import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AbstractIndexComponent } from './abstract-index.component';
import { Observable } from 'rxjs';
import { ChangeStatusDialogComponent, ChangeStatusDialogData } from './change-status-dialog.component';
import { AbstractStatusTrackingDto } from '../api.shared.model';

@Component({
  selector: 'app-account-index',
  template: '',
  styles: [''],
  standalone: false
})
export abstract class AbstractIndexWithStatusComponent<S, T> extends AbstractIndexComponent<T> {

  protected changeStatus?: (dto: AbstractStatusTrackingDto<S>) => Observable<AbstractStatusTrackingDto<S>>;

  constructor(protected override router: Router, protected override dialog: MatDialog) {
    super(router, dialog);
  }

  changeStatusAction(dto: T): void {
    if (this.changeStatus == null) {
      throw new Error('Change status action is not implemented');
    }

    const dialogRef = this.dialog.open(ChangeStatusDialogComponent<S>, {
      width: '400px',
      data: {
        dto: dto,
        changeStatusAction: this.changeStatus
      } as ChangeStatusDialogData<S>
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadData();
      }
    });
  }

}
