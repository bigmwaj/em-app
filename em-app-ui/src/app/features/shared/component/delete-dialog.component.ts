import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Observable, Subject, takeUntil } from 'rxjs';
import { UserDto } from '../../platform/api.platform.model';
import { UserService } from '../../platform/service/user.service';

export interface DeleteDialogData {
  title?: string;
  warningMessage?: string;
  deleteAction?: Observable<void>; // Optional custom delete action
  user: UserDto; // Keep user for backward compatibility, can be used in custom deleteAction
}

@Component({
  selector: 'app-delete-dialog',
  templateUrl: './delete-dialog.component.html',
  styleUrls: ['./delete-dialog.component.scss'],
  standalone: false
})
export class DeleteDialogComponent implements OnDestroy {
  title = 'Confirm Deletion';
  warningMessage = 'Are you sure you want to delete this element? This action cannot be undone.';
  deleteAction?: Observable<void>;
  loading = false;
  error: string | null = null;
  user!: UserDto; // Keep user for backward compatibility, can be used in custom deleteAction
  private destroy$ = new Subject<void>();

  constructor(
    private userService: UserService,
    public dialogRef: MatDialogRef<DeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DeleteDialogData
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
    if (data.user) {
      this.user = data.user;
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
    if (!this.data.user.id) {
      this.error = 'User ID is missing';
      return;
    }

    this.loading = true;
    this.error = null;
/*
    if (!this.deleteAction) {
      throw new Error('Delete action is not provided');
    }*/

    this.userService.deleteUser(this.data.user.id).subscribe({
      next: () => {
        this.loading = false;
        this.dialogRef.close(true); // Return success
      },
      error: (err) => {
        console.error('Failed to delete user:', err);
        this.error = 'Failed to delete user. Please try again.';
        this.loading = false;
      }
    });
  }
}
