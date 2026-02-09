import { Component, OnInit } from '@angular/core';
import { UserService } from '../../service/user.service';
import { UserDto } from '../../api.platform.model';
import { createDefaultSearchCriteria, DefaultSearchCriteria, SearchResult, FilterOperator } from '../../../shared/api.shared.model';
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
  searchCriteria: DefaultSearchCriteria = createDefaultSearchCriteria();
  displayedColumns: string[] = ['firstName', 'lastName', 'mainEmail', 'mainPhone', 'mainAddress', 'actions'];
  searchText = '';

  constructor(private userService: UserService) { super(); }


  override getKeyLabel(bean: UserDto): string | number {
    throw new Error('Method not implemented.');
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

  onSearch(): void {
    this.searchCriteria = createDefaultSearchCriteria();
    
    if (this.searchText && this.searchText.trim()) {
      this.searchCriteria.filterByItems = [{
        name: 'firstName',
        oper: FilterOperator.LIKE,
        values: [this.searchText.trim()]
      }];
    }
    
    this.loadUsers();
  }

  onClearSearch(): void {
    this.searchText = '';
    this.searchCriteria = createDefaultSearchCriteria();
    this.loadUsers();
  }
}
