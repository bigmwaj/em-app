import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../../service/platform/user.service';
import { User } from '../../../models/api.platform.model';
import { SearchResult } from '../../../models/api.shared.model';
import { DeleteDialogComponent } from '../../../../shared/dialogs/delete-dialog/delete-dialog.component';
import { UserFormDialogComponent } from './user-form-dialog/user-form-dialog.component';

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
    private dialog: MatDialog
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
    const dialogRef = this.dialog.open(UserFormDialogComponent, {
      width: '700px',
      data: { mode: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.userService.createUser(result).subscribe({
          next: () => {
            this.loadUsers();
          },
          error: (err) => {
            console.error('Failed to create user:', err);
            this.error = 'Failed to create user. Please try again.';
          }
        });
      }
    });
  }

  /**
   * Opens dialog to edit an existing user
   */
  editUser(user: User): void {
    const dialogRef = this.dialog.open(UserFormDialogComponent, {
      width: '700px',
      data: { user, mode: 'edit' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && user.id) {
        this.userService.updateUser(user.id, result).subscribe({
          next: () => {
            this.loadUsers();
          },
          error: (err) => {
            console.error('Failed to update user:', err);
            this.error = 'Failed to update user. Please try again.';
          }
        });
      }
    });
  }

  /**
   * Opens confirmation dialog and deletes user
   */
  deleteUser(user: User): void {
    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '450px',
      data: {
        title: 'Delete User',
        message: 'Are you sure you want to delete this user?',
        itemName: user.name || user.username
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed && user.id) {
        this.userService.deleteUser(user.id).subscribe({
          next: () => {
            this.loadUsers();
          },
          error: (err) => {
            console.error('Failed to delete user:', err);
            this.error = 'Failed to delete user. Please try again.';
          }
        });
      }
    });
  }
}
