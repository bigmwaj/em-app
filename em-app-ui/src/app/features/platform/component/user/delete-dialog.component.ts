import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserDto } from '../../api.platform.model';
import { UserService } from '../../service/user.service';
import { Subject, takeUntil } from 'rxjs';

export interface UserDeleteDialogData {
  user: UserDto;
}

@Component({
  selector: 'app-user-delete-dialog',
  templateUrl: './delete-dialog.component.html',
  styleUrls: ['./delete-dialog.component.scss'],
  standalone: false
})
export class UserDeleteDialogComponent implements OnDestroy {
  loading = false;
  error: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<UserDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserDeleteDialogData,
    private userService: UserService
  ) {}

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

    this.userService.deleteUser(this.data.user.id).pipe(takeUntil(this.destroy$)).subscribe({
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
