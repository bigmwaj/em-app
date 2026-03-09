import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable, Subject, takeUntil } from 'rxjs';
import { AbstractStatusTrackingDto } from '../api.shared.model';
import { PageData } from '../base.helper';

export interface ChangeStatusDialogData<S> {
  dto: AbstractStatusTrackingDto<S>;
  title?: string;
  statusOptions?: S[];
  changeStatusAction?: (dto: AbstractStatusTrackingDto<S>) => Observable<AbstractStatusTrackingDto<S>>;
}

@Component({
  selector: 'app-change-status-dialog',
  templateUrl: './change-status-dialog.component.html',
  styleUrls: ['./change-status-dialog.component.scss'],
  standalone: false
})
export class ChangeStatusDialogComponent<S> implements OnInit, OnDestroy {
  
  title = 'Change Status';

  changeStatusAction?: (dto: AbstractStatusTrackingDto<S>) => Observable<AbstractStatusTrackingDto<S>>;

  statusOptions?: S[];

  pageData = new PageData();

  form!: FormGroup;

  dto!: AbstractStatusTrackingDto<S>;

  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ChangeStatusDialogComponent<S>>,
    @Inject(MAT_DIALOG_DATA) public data: ChangeStatusDialogData<S>) {

    this.dto = data.dto;
    this.statusOptions = data.statusOptions;
    if (data.title) {
      this.title = data.title;
    }
    if (data.changeStatusAction) {
      this.changeStatusAction = data.changeStatusAction;
    }
  }

  ngOnInit(): void {
    this.initializeForms();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForms(): void {
    this.form = this.fb.group({
      status: [this.dto.status, Validators.required],
      statusDate: [new Date()],
      statusReason: []
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirm(): void {
    if (this.form.invalid) {
      this.pageData.error.set('Please fill in all required fields in User Change Status form.');
      return;
    }
    this.pageData.loading.set(true);
    this.pageData.error.set(null);
    if (!this.changeStatusAction) {
      throw new Error('Change status action is not provided');
    }

    this.changeStatusAction(this.buildUserDto()).pipe(takeUntil(this.destroy$)).subscribe({
      next: (dto) => {
        this.pageData.loading.set(false);
        this.dialogRef.close(dto);
      },
      error: (err) => {
        console.error('Failed to change the user status:', err);
        this.pageData.error.set('Failed to change user status. Please try again.');
        this.pageData.loading.set(false);
      }
    });
  }

  buildUserDto(): AbstractStatusTrackingDto<S> {
    const formValue = this.form.value;
    this.dto.status = formValue.status;

    if (formValue.statusDate) {
      this.dto.statusDate = formValue.statusDate;
    }

    if (formValue.statusReason) {
      this.dto.statusReason = formValue.statusReason;
    }
    return this.dto;
  }
}
