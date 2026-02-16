import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { GroupService } from '../../service/group.service';
import { GroupDto } from '../../api.platform.model';
import { DefaultSearchCriteria, SearchResult } from '../../../shared/api.shared.model';
import { CommonDataSource } from '../../../shared/common.datasource';
import { PageEvent } from '@angular/material/paginator';
import { Subject, takeUntil } from 'rxjs';
import { SharedHelper } from '../../../shared/shared.helper';
import { GroupDeleteDialogComponent } from './delete-dialog.component';

@Component({
  selector: 'app-group-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class GroupIndexComponent extends CommonDataSource<GroupDto> implements OnInit, OnDestroy {
  searchResult: SearchResult<GroupDto> = {} as SearchResult<GroupDto>;
  loading = true;
  error: string | null = null;
  searchCriteria: DefaultSearchCriteria = SharedHelper.createDefaultSearchCriteria();
  displayedColumns: string[] = ['name', 'description', 'holderType', 'actions'];
  searchText = '';
  private destroy$ = new Subject<void>();

  constructor(
    private groupService: GroupService,
    private router: Router,
    private dialog: MatDialog
  ) {
    super();
    this.searchCriteria.pageSize = 10;
  }

  override getKeyLabel(bean: GroupDto): string | number {
    return bean.name;
  }

  ngOnInit(): void {
    this.loadGroups();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  handlePageEvent(e: PageEvent) {
    this.searchCriteria.pageIndex = e.pageIndex;
    this.searchCriteria.pageSize = e.pageSize;
    this.loadGroups();
  }

  /**
   * Loads groups from the API
   */
  loadGroups(): void {
    this.loading = true;
    this.error = null;

    this.groupService.getGroups(this.searchCriteria).pipe(takeUntil(this.destroy$)).subscribe({
      next: (searchResult) => {
        this.searchResult = searchResult;
        this.loading = false;
        this.setData(searchResult.data);
      },
      error: (err) => {
        console.error('Failed to load groups:', err);
        this.error = 'Failed to load groups. Please try again.';
        this.loading = false;
      }
    });
  }

  /**
   * Navigate to create group page
   */
  createGroup(): void {
    this.router.navigate(['/groups/edit', 'create'], {
      state: { mode: 'create' }
    });
  }

  /**
   * Navigate to view group page
   */
  viewGroup(group: GroupDto): void {
    this.router.navigate(['/groups/edit', 'view'], {
      state: { mode: 'view', group: group }
    });
  }

  /**
   * Navigate to edit group page
   */
  editGroup(group: GroupDto): void {
    this.router.navigate(['/groups/edit', 'edit'], {
      state: { mode: 'edit', group: group }
    });
  }

  /**
   * Open delete confirmation dialog
   */
  deleteGroup(group: GroupDto): void {
    if (group.id === undefined) {
      this.error = 'Group ID is missing. Cannot delete group.';
      return;
    }

    const dialogRef = this.dialog.open(GroupDeleteDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Group Deletion',
        warningMessage: `Are you sure you want to delete group "${group.name}"? This action cannot be undone.`,
        group: group
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadGroups();
      }
    });
  }
}
