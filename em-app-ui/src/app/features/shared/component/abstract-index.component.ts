import { PageEvent } from '@angular/material/paginator';
import { AbstractBaseDto, AbstractSearchCriteria, FilterOperator, SearchResult } from '../api.shared.model';
import { CommonDataSource } from '../common.datasource';
import { PageData, BaseHelper } from '../base.helper';
import { Observable, Subscription } from 'rxjs';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DeleteDialogComponent, DeleteDialogData } from './delete-dialog.component';

@Component({
  selector: 'app-abstract-index',
  template: '',
  styles: [''],
  standalone: false
})
export abstract class AbstractIndexComponent<T extends AbstractBaseDto> extends CommonDataSource<T> implements OnInit, OnDestroy {

  searchResult: SearchResult<T> = {} as SearchResult<T>;

  searchCriteria: AbstractSearchCriteria;

  searchText = '';

  pageData: PageData = new PageData(true);

  protected sortableFieldMap: Map<string, string> = new Map<string, string>();

  protected textSearchableFields: string[] = [];

  protected delete?: (dto: T) => Observable<void>;

  private subscriptions$: Subscription[] = [];

  protected dialog = inject(MatDialog);

  protected router = inject(Router);

  constructor(
    protected helper: BaseHelper<T>) {
    super();
    this.searchCriteria = this.helper.createDefaultSearchCriteria();
  }

  override getKeyLabel(dto: T): string | number {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    this.loadData();
  }

  ngOnDestroy(): void {
    this.subscriptions$.forEach(sub => sub.unsubscribe());
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
    return this.helper.getFieldSortIcon(this.searchCriteria, this.getSortField(fieldName));
  }

  sortBy(fieldName: string): void {
    this.helper.setSortBy(this.searchCriteria, this.getSortField(fieldName));
    this.loadData();
  }

  handlePageEvent(e: PageEvent) {
    this.searchCriteria.pageIndex = e.pageIndex;
    this.searchCriteria.pageSize = e.pageSize;
    this.loadData();
  }

  protected search?: (sc: AbstractSearchCriteria) => Observable<SearchResult<T>>;

  protected loadData(): void {

    this.pageData.loading.set(true);

    let search: Observable<SearchResult<T>>;
    if (this.search) {
      search = this.search(this.searchCriteria);
    } else {
      search = this.helper.search(this.searchCriteria);
    }

    this.subscriptions$.push(search.subscribe({
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
    }));
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
    this.helper.getBackedDto(this.helper.EditMode.CREATE).subscribe({
      next: (dto) => {
        this.router.navigate([this.helper.baseRoute + '/edit', this.helper.EditMode.CREATE], {
          state: { mode: this.helper.EditMode.CREATE, dto: dto }
        });
      }
    });
  }

  editAction(dto: T): void {
    this.helper.getBackedDto(this.helper.EditMode.EDIT, dto).subscribe({
      next: (fetchedDto) => {
        console.log('Navigating to edit page with DTO:', fetchedDto);
        this.router.navigate([this.helper.baseRoute + '/edit', this.helper.EditMode.EDIT], {
          state: { mode: this.helper.EditMode.EDIT, dto: fetchedDto }
        });
      }
    });
  }

  viewAction(dto: T): void {
    this.helper.getBackedDto(this.helper.EditMode.VIEW, dto).subscribe({
      next: (fetchedDto) => {
        this.router.navigate([this.helper.baseRoute + '/edit', this.helper.EditMode.VIEW], {
          state: { mode: this.helper.EditMode.VIEW, dto: fetchedDto }
        });
      }
    });
  }

  duplicateAction(dto: T): void {
    this.helper.getBackedDto(this.helper.EditMode.CREATE, dto).subscribe({
      next: (fetchedDto) => {
        this.router.navigate([this.helper.baseRoute + '/edit', this.helper.EditMode.CREATE], {
          state: { mode: this.helper.EditMode.CREATE, dto: fetchedDto }
        });
      }
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
