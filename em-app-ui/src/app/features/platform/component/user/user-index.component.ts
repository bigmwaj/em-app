import { Component, OnInit } from '@angular/core';
import { UserService } from '../../service/user.service';
import { UserDto } from '../../api.platform.model';
import { SearchResult } from '../../../shared/api.shared.model';

@Component({
  selector: 'app-user-index',
  templateUrl: './user-index.component.html',
  styleUrls: ['./user-index.component.scss'],
  standalone: false
})
export class UserIndexComponent implements OnInit {
  searchResult: SearchResult<UserDto> = {} as SearchResult<UserDto>;
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
  editUser(user: UserDto): void {
    console.log('Edit user:', user);
    // TODO: Implement edit dialog
  }

  /**
   * Placeholder for delete functionality
   */
  deleteUser(user: UserDto): void {
    console.log('Delete user:', user);
    // TODO: Implement delete confirmation
  }
}
