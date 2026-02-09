import { Component, OnInit } from '@angular/core';
import { UserService } from '../../service/user.service';
import { UserDto, UserSearchCriteria, createUserSearchCriteria } from '../../api.platform.model';
import { SearchResult, WhereClause, FilterOperator } from '../../../shared/api.shared.model';
import { CommonDataSource } from '../../../shared/common.datasource';

@Component({
  selector: 'app-user-index',
  templateUrl: './user-index.component.html',
  styleUrls: ['./user-index.component.scss'],
  standalone: false
})
export class UserIndexComponent extends CommonDataSource<UserDto> implements OnInit {
  searchResult: SearchResult<UserDto> = {} as SearchResult<UserDto>;
  loading = true;
  error: string | null = null;
  searchCriteria: UserSearchCriteria = createUserSearchCriteria();
  searchTerm: string = '';

  constructor(private userService: UserService) {
    super();
    this.searchCriteria.includeContact = true;
  }

  override getKeyLabel(bean: UserDto): string | number {
    return bean.id || bean.username;
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  /**
   * Loads users from the API
   */
  loadUsers(): void {
    this.loading = true;
    this.error = null;

    // Apply search filter if search term exists
    if (this.searchTerm && this.searchTerm.trim()) {
      const whereClause: WhereClause = {
        name: 'username',
        oper: FilterOperator.LIKE,
        values: [this.searchTerm.trim()]
      };
      this.searchCriteria.filterByItems = [whereClause];
    } else {
      this.searchCriteria.filterByItems = [];
    }

    this.userService.getUsers(this.searchCriteria).subscribe({
      next: (searchResult) => {
        this.searchResult = searchResult;
        this.loading = false;
        this.setData(searchResult.data);
      },
      error: (err) => {
        console.error('Failed to load users:', err);
        this.error = 'Failed to load users. Please try again.';
        this.loading = false;
      }
    });
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.searchCriteria.filterByItems = [];
    this.loadUsers();
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
