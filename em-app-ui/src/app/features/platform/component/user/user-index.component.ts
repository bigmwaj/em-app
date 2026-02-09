import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { UserService } from '../../service/user.service';
import { UserDto, UserSearchCriteria, createUserSearchCriteria } from '../../api.platform.model';
import { SearchResult, FilterOperator, WhereClause } from '../../../shared/api.shared.model';
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
  message = "";
  error: string | null = null;
  searchCriteria: UserSearchCriteria = createUserSearchCriteria();
  displayedColumns: string[] = ['username', 'firstName', 'lastName', 'email', 'status', 'actions'];
  
  searchForm = new FormGroup({
    username: new FormControl(''),
    firstName: new FormControl(''),
    lastName: new FormControl(''),
    email: new FormControl('')
  });

  constructor(private userService: UserService) { 
    super();
    this.searchCriteria.includeContact = true;
  }


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

  onSearch(): void {
    const filters: WhereClause[] = [];
    
    if (this.searchForm.value.username) {
      filters.push({ 
        name: 'username', 
        oper: FilterOperator.LIKE, 
        values: [this.searchForm.value.username] 
      });
    }
    
    if (this.searchForm.value.firstName) {
      filters.push({ 
        name: 'firstName', 
        oper: FilterOperator.LIKE, 
        values: [this.searchForm.value.firstName] 
      });
    }
    
    if (this.searchForm.value.lastName) {
      filters.push({ 
        name: 'lastName', 
        oper: FilterOperator.LIKE, 
        values: [this.searchForm.value.lastName] 
      });
    }
    
    if (this.searchForm.value.email) {
      filters.push({ 
        name: 'email', 
        oper: FilterOperator.LIKE, 
        values: [this.searchForm.value.email] 
      });
    }
    
    this.searchCriteria.filterByItems = filters;
    this.loadUsers();
  }

  resetSearch(): void {
    this.searchForm.reset();
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
