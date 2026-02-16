import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { RoleDto } from '../../api.platform.model';
import { RoleService } from '../../service/role.service';
import { Subject, takeUntil } from 'rxjs';

export interface RoleDeleteDialogData {
  role: RoleDto;
  title?: string;
  warningMessage?: string;
}

@Component({
  selector: 'app-role-delete-dialog',
  templateUrl: './delete-dialog.component.html',
  styleUrls: ['./delete-dialog.component.scss'],
  standalone: false
})
export class RoleDeleteDialogComponent implements OnDestroy {
  loading = false;
  error: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<RoleDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: RoleDeleteDialogData,
    private roleService: RoleService
  ) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirmDelete(): void {
    if (!this.data.role.id) {
      this.error = 'Role ID is missing';
      return;
    }

    this.loading = true;
    this.error = null;

    this.roleService.deleteRole(this.data.role.id).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.loading = false;
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.error('Failed to delete role:', err);
        this.error = 'Failed to delete role. Please try again.';
        this.loading = false;
      }
    });
  }
}
