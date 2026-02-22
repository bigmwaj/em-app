import { PageEvent } from '@angular/material/paginator';
import { AbstractSearchCriteria, FilterOperator, SearchResult } from '../api.shared.model';
import { CommonDataSource } from '../common.datasource';
import { PageData, SharedHelper } from '../shared.helper';
import { Observable, Subscription } from 'rxjs';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DeleteDialogComponent, DeleteDialogData } from './delete-dialog.component';

@Component({
  selector: 'app-abstract-index',
  template: '',
  styles: [''],
  standalone: false
})
export abstract class AbstractIndexComponent<T> extends CommonDataSource<T> implements OnInit, OnDestroy {

  SharedHelper = SharedHelper;

  searchResult: SearchResult<T> = {} as SearchResult<T>;

  searchCriteria: AbstractSearchCriteria = SharedHelper.createDefaultSearchCriteria();

  searchText = '';

  pageData: PageData = new PageData(true);

  protected sortableFieldMap: Map<string, string> = new Map<string, string>();

  protected textSearchableFields: string[] = [];

  abstract search(): Observable<SearchResult<T>>;

  protected delete?: (dto: T) => Observable<void>;

  protected abstract getBaseRoute(): string;

  private destroy$? : Subscription;

  constructor(protected router: Router, protected dialog: MatDialog) {
    super();
  }

  override getKeyLabel(dto: T): string | number {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    this.loadData();
  }

  ngOnDestroy(): void {
    if( this.destroy$ ) {
      this.destroy$.unsubscribe();
    }
  }

  isSortable(fieldName: string): boolean {
    return this.sortableFieldMap.has(fieldName);
  }

  private getSortField(fieldName: string): string {
    if (!this.sortableFieldMap.has(fieldName)) {
      throw new Error(`Field "${fieldName}" is not defined in sortableFieldMap.`);
    }
    return this.sortableFieldMap.get(fieldName) as string;
  }

  getFieldSortIcon(fieldName: string): string {
    return SharedHelper.getFieldSortIcon(this.searchCriteria, this.getSortField(fieldName));
  }

  sortBy(fieldName: string): void {
    SharedHelper.setSortBy(this.searchCriteria, this.getSortField(fieldName));
    this.loadData();
  }

  handlePageEvent(e: PageEvent) {
    this.searchCriteria.pageIndex = e.pageIndex;
    this.searchCriteria.pageSize = e.pageSize;
    this.loadData();
  }

  protected loadData(): void {

    this.destroy$ = this.search().subscribe({
      next: (searchResult) => {
        this.searchResult = searchResult;
        this.pageData.loading.set(false);
        this.setData(searchResult.data);
      },
      error: (err) => {
        console.error('Failed to load data:', err);
        this.pageData.error.set('Failed to load data. Please try again.');
        this.pageData.loading.set(false);
      },
      complete: () => {
        this.pageData.loading.set(false);
      },
    })
  }

  onSearch($event?: string): void {
    if ($event) {
      this.searchText = $event;
    }
    if (this.searchText && this.searchText.trim()) {
      this.searchCriteria.whereClauses = this.textSearchableFields
        .map(field => {
          return {
            name: field,
            oper: FilterOperator.LIKE,
            values: [this.searchText.trim()]
          };
        });
    }

    this.loadData();
  }

  onClearSearch(): void {
    this.searchText = '';
    this.searchCriteria.pageIndex = 0;
    this.searchCriteria.whereClauses = [];
    this.searchCriteria.sortByClauses = [];
    this.loadData();
  }

  onInitAdvancedSearch(): void {
    alert('Advanced search is not implemented yet.');
  }

  createAction(): void {
    this.router.navigate([this.getBaseRoute() + '/edit', 'create'], {
      state: { mode: 'create' }
    });
  }

  protected prepareEdit(dto: T): T {
    return dto;
  }

  editAction(dto: T): void {
    this.router.navigate([this.getBaseRoute() + '/edit', 'edit'], {
      state: { mode: 'edit', dto: this.prepareEdit(dto) }
    });
  }

  protected prepareView(dto: T): T {
    return dto;
  }

  viewAction(dto: T): void {
    this.router.navigate([this.getBaseRoute() + '/edit', 'view'], {
      state: { mode: 'view', dto: this.prepareView(dto) }
    });
  }

  protected abstract duplicateDto(dto: T): T;

  duplicateAction(dto: T): void {
    const duplicatedDto = this.duplicateDto(dto);

    // Navigate to create mode with duplicated data
    this.router.navigate([this.getBaseRoute() + '/edit', 'create'], {
      state: { mode: 'create', dto: duplicatedDto }
    });
  }

  deleteAction(dto: T): void {

    if (this.delete == null) {
      throw new Error('Delete action is not implemented');
    }

    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '400px',
      data: {
        dto: dto,
        deleteAction: this.delete
      } as DeleteDialogData<T>
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Refresh the list after successful delete
        this.loadData();
      }
    });
  }
}
