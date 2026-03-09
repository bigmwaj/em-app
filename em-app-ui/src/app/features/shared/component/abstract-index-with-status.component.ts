import { Component } from '@angular/core';
import { AbstractIndexComponent } from './abstract-index.component';
import { Observable } from 'rxjs';
import { ChangeStatusDialogComponent, ChangeStatusDialogData } from './change-status-dialog.component';
import { AbstractStatusTrackingDto } from '../api.shared.model';
import { BaseHelper } from '../base.helper';

@Component({
  selector: 'app-abstract-index-with-status',
  template: '',
  styles: [''],
  standalone: false
})
export abstract class AbstractIndexWithStatusComponent<S, T extends AbstractStatusTrackingDto<S>> extends AbstractIndexComponent<T> {
  statusOptions: S[] = [];
  
  protected changeStatus?: (dto: AbstractStatusTrackingDto<S>) => Observable<AbstractStatusTrackingDto<S>>;

  constructor(
    protected override helper: BaseHelper<T>) {
    super(helper);
  }

  changeStatusAction(dto: T): void {
    if (this.changeStatus == null) {
      throw new Error('Change status action is not implemented');
    }

    const statusOptions = this.statusOptions ? this.statusOptions.filter(e => e !== dto.status) : [];

    this.helper.getBackedDto(this.helper.EditMode.CHANGE_STATUS, dto)
    .subscribe(fetchedDto => {
      const dialogRef = this.dialog.open(ChangeStatusDialogComponent<S>, {
        width: '400px',
        data: {
          dto: fetchedDto,
          changeStatusAction: this.changeStatus,
          statusOptions: statusOptions
        } as ChangeStatusDialogData<S>
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.loadData();
        }
      });
    });
  }
}
