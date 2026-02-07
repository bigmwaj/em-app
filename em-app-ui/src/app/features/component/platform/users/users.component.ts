import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../../../service/platform/user.service';
import { User } from '../../../models/api.platform.model';
import { SearchResult } from '../../../models/api.shared.model';
import { UserDialogComponent } from './user-dialog/user-dialog.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
  standalone: false
})
export class UsersComponent implements OnInit {
  searchResult: SearchResult<User> = {} as SearchResult<User>;
  loading = true;
  error: string | null = null;

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  /**
   * Loads users from the API
   */
  loadUsers(): void {
    this.loading = true;
    this.error = null;

    this.userService.getUsers().subscribe({
      next: (searchResult) => {
        this.searchResult = searchResult;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load users:', err);
        this.error = 'Failed to load users. Please try again.';
        this.loading = false;
      }
    });
  }

  /**
   * Opens dialog to create a new user
   */
  createUser(): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '600px',
      data: { mode: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.userService.createUser(result).subscribe({
          next: () => {
            this.snackBar.open('User created successfully', 'Close', { duration: 3000 });
            this.loadUsers();
          },
          error: (err) => {
            console.error('Failed to create user:', err);
            this.snackBar.open('Failed to create user', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }

  /**
   * Opens dialog to edit a user
   */
  editUser(user: User): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '600px',
      data: { user, mode: 'edit' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.id) {
        this.userService.updateUser(result.id, result).subscribe({
          next: () => {
            this.snackBar.open('User updated successfully', 'Close', { duration: 3000 });
            this.loadUsers();
          },
          error: (err) => {
            console.error('Failed to update user:', err);
            this.snackBar.open('Failed to update user', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }

  /**
   * Opens confirmation dialog and deletes user if confirmed
   */
  deleteUser(user: User): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Delete User',
        message: `Are you sure you want to delete user "${user.name}"? This action cannot be undone.`,
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed && user.id) {
        this.userService.deleteUser(user.id).subscribe({
          next: () => {
            this.snackBar.open('User deleted successfully', 'Close', { duration: 3000 });
            this.loadUsers();
          },
          error: (err) => {
            console.error('Failed to delete user:', err);
            this.snackBar.open('Failed to delete user', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }
}
