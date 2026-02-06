import { Component, OnInit } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
  standalone: false
})
export class UsersComponent implements OnInit {
  users: User[] = [];
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
      next: (users) => {
        this.users = users;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load users:', err);
        this.error = 'Failed to load users. Please try again.';
        this.loading = false;
        // Use mock data for demonstration if API fails
        this.users = this.getMockUsers();
      }
    });
  }

  /**
   * Mock users for demonstration when API is not available
   */
  private getMockUsers(): User[] {
    return [
      { id: 1, email: 'john.doe@example.com', name: 'John Doe', provider: 'google' },
      { id: 2, email: 'jane.smith@example.com', name: 'Jane Smith', provider: 'github' },
      { id: 3, email: 'bob.wilson@example.com', name: 'Bob Wilson', provider: 'facebook' }
    ];
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
