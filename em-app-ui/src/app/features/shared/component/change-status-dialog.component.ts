import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { UserDto, UserStatusLvo } from '../../platform/api.platform.model';
import { UserService } from '../../platform/service/user.service';
import { ChangeStatusDelegateDto, EditActionLvo } from '../api.shared.model';

export interface ChangeStatusDialogData<T> {  
  user: UserDto;
  dto: ChangeStatusDelegateDto<T>;
}

@Component({
  selector: 'app-change-status-dialog',
  templateUrl: './change-status-dialog.component.html',
  styleUrls: ['./change-status-dialog.component.scss'],
  standalone: false
})
export class ChangeStatusDialogComponent implements OnInit, OnDestroy {
  userStatuses = Object.values(UserStatusLvo);

  form!: FormGroup;
  user!: UserDto;
  loading = false;
  private destroy$ = new Subject<void>();
  error: string | null = null;

  constructor(
    private userService: UserService,
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ChangeStatusDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ChangeStatusDialogData<any>
  ) {
    this.user = data.user;
    this.userStatuses = this.userStatuses.filter(e => e !== this.user.status);
  }

  ngOnInit(): void {
    this.initializeForms();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForms(): void {
    const defaultStatus = this.userStatuses.length > 0 ? this.userStatuses[0] : null;
    this.form = this.fb.group({
      status: [defaultStatus, Validators.required],
      statusDate: [new Date()],
      statusReason: []
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirm(): void {
    if (this.form.invalid) {
      this.error = 'Please fill in all required fields in User Change Status form.';
      return;
    }
    this.loading = true;
    this.error = null;

    this.userService.updateUser(this.buildUserDto()).pipe(takeUntil(this.destroy$)).subscribe({
      next: (userDto) => {
        this.loading = false;
        this.dialogRef.close(userDto);
      },
      error: (err) => {
        console.error('Failed to change the user status:', err);
        this.error = 'Failed to change user status. Please try again.';
        this.loading = false;
      }
    });

  }

  buildUserDto(): UserDto {
    const formValue = this.form.value;
    const userDto = {
      id: this.user.id,
      key: this.user.key,
      username: this.user.username,
      usernameType: this.user.usernameType,
      provider: this.user.provider,
      picture: this.user.picture,
      holderType: this.user.holderType,
      status: formValue.status,
      editAction: EditActionLvo.CHANGE_STATUS,
      statusDate: formValue.statusDate,
      statusReason: formValue.statusReason
    } as UserDto;

    if (formValue.statusDate) {
      userDto.statusDate = formValue.statusDate;
    }
    if (formValue.statusReason) {
      userDto.statusReason = formValue.statusReason;
    }
    return userDto;
  }
}
