import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { GroupDto } from '../../api.platform.model';
import { GroupService } from '../../service/group.service';
import { Subject, takeUntil } from 'rxjs';

export interface GroupDeleteDialogData {
  group: GroupDto;
  title?: string;
  warningMessage?: string;
}

@Component({
  selector: 'app-group-delete-dialog',
  templateUrl: './delete-dialog.component.html',
  styleUrls: ['./delete-dialog.component.scss'],
  standalone: false
})
export class GroupDeleteDialogComponent implements OnDestroy {
  loading = false;
  error: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<GroupDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GroupDeleteDialogData,
    private groupService: GroupService
  ) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirmDelete(): void {
    if (!this.data.group.id) {
      this.error = 'Group ID is missing';
      return;
    }

    this.loading = true;
    this.error = null;

    this.groupService.deleteGroup(this.data.group.id).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.loading = false;
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.error('Failed to delete group:', err);
        this.error = 'Failed to delete group. Please try again.';
        this.loading = false;
      }
    });
  }
}
