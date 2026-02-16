import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { RoleService } from '../../service/role.service';
import { RoleDto } from '../../api.platform.model';
import { DefaultSearchCriteria, SearchResult } from '../../../shared/api.shared.model';
import { CommonDataSource } from '../../../shared/common.datasource';
import { PageEvent } from '@angular/material/paginator';
import { Subject, takeUntil } from 'rxjs';
import { SharedHelper } from '../../../shared/shared.helper';
import { RoleDeleteDialogComponent } from './delete-dialog.component';

@Component({
  selector: 'app-role-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class RoleIndexComponent extends CommonDataSource<RoleDto> implements OnInit, OnDestroy {
  searchResult: SearchResult<RoleDto> = {} as SearchResult<RoleDto>;
  loading = true;
  error: string | null = null;
  searchCriteria: DefaultSearchCriteria = SharedHelper.createDefaultSearchCriteria();
  displayedColumns: string[] = ['name', 'description', 'holderType', 'actions'];
  searchText = '';
  private destroy$ = new Subject<void>();

  constructor(
    private roleService: RoleService,
    private router: Router,
    private dialog: MatDialog
  ) {
    super();
    this.searchCriteria.pageSize = 10;
  }

  override getKeyLabel(bean: RoleDto): string | number {
    return bean.name;
  }

  ngOnInit(): void {
    this.loadRoles();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  handlePageEvent(e: PageEvent) {
    this.searchCriteria.pageIndex = e.pageIndex;
    this.searchCriteria.pageSize = e.pageSize;
    this.loadRoles();
  }

  /**
   * Loads roles from the API
   */
  loadRoles(): void {
    this.loading = true;
    this.error = null;

    this.roleService.getRoles(this.searchCriteria).pipe(takeUntil(this.destroy$)).subscribe({
      next: (searchResult) => {
        this.searchResult = searchResult;
        this.loading = false;
        this.setData(searchResult.data);
      },
      error: (err) => {
        console.error('Failed to load roles:', err);
        this.error = 'Failed to load roles. Please try again.';
        this.loading = false;
      }
    });
  }

  /**
   * Navigate to create role page
   */
  createRole(): void {
    this.router.navigate(['/roles/edit', 'create'], {
      state: { mode: 'create' }
    });
  }

  /**
   * Navigate to view role page
   */
  viewRole(role: RoleDto): void {
    this.router.navigate(['/roles/edit', 'view'], {
      state: { mode: 'view', role: role }
    });
  }

  /**
   * Navigate to edit role page
   */
  editRole(role: RoleDto): void {
    this.router.navigate(['/roles/edit', 'edit'], {
      state: { mode: 'edit', role: role }
    });
  }

  /**
   * Open delete confirmation dialog
   */
  deleteRole(role: RoleDto): void {
    if (role.id === undefined) {
      this.error = 'Role ID is missing. Cannot delete role.';
      return;
    }

    const dialogRef = this.dialog.open(RoleDeleteDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Role Deletion',
        warningMessage: `Are you sure you want to delete role "${role.name}"? This action cannot be undone.`,
        role: role
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadRoles();
      }
    });
  }
}
