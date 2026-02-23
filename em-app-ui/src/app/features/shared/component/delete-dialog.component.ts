import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Observable, Subject, takeUntil } from 'rxjs';
import { PageData } from '../shared.helper';

export interface DeleteDialogData<T> {
  title?: string;
  warningMessage?: string;
  deleteAction?: (dto: T) => Observable<void>; // Optional custom delete action
  dto: T; // Keep dto for backward compatibility, can be used in custom deleteAction
}

@Component({
  selector: 'app-delete-dialog',
  templateUrl: './delete-dialog.component.html',
  styleUrls: ['./delete-dialog.component.scss'],
  standalone: false
})
export class DeleteDialogComponent<T> implements OnDestroy {
  title = 'Confirm Deletion';
  warningMessage = 'Are you sure you want to delete this element? This action cannot be undone.';
  deleteAction?: (dto: T) => Observable<void>;

  pageData = new PageData();
  dto!: T; // Keep dto for backward compatibility, can be used in custom deleteAction
  private destroy$ = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<DeleteDialogComponent<T>>,
    @Inject(MAT_DIALOG_DATA) public data: DeleteDialogData<T>
  ) {
    if (data.title) {
      this.title = data.title;
    }
    if (data.warningMessage) {
      this.warningMessage = data.warningMessage;
    }
    if (data.deleteAction) {
      this.deleteAction = data.deleteAction;
    }
    if (data.dto) {
      this.dto = data.dto;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirmDelete(): void {
    if (!this.data.dto) {
      this.pageData.error.set('DTO is missing');
      return;
    }

    this.pageData.loading.set(true);
    this.pageData.error.set(null);

    if (!this.deleteAction) {
      throw new Error('Delete action is not provided');
    }

    this.deleteAction(this.data.dto).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.pageData.loading.set(false);
        this.dialogRef.close(true); // Return success
      },
      error: (err) => {
        console.error('Failed to delete element:', err);
        this.pageData.error.set('Failed to delete element. Please try again.');
        this.pageData.loading.set(false);
      }
    });
  }
}
