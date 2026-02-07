import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../service/platform/user.service';
import { User } from '../../../models/api.platform.model';
import { SearchResult } from '../../../models/api.shared.model';

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

  constructor(private userService: UserService) {}

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
   * Placeholder for edit functionality
   */
  editUser(user: User): void {
    console.log('Edit user:', user);
    // TODO: Implement edit dialog
  }

  /**
   * Placeholder for delete functionality
   */
  deleteUser(user: User): void {
    console.log('Delete user:', user);
    // TODO: Implement delete confirmation
  }
}
