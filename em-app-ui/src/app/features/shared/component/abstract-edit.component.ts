import { FormBuilder } from '@angular/forms';
import { SharedHelper } from '../shared.helper';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DeleteDialogComponent, DeleteDialogData } from './delete-dialog.component';
import { Observable, Subject } from 'rxjs';
import { Component, OnDestroy, OnInit } from '@angular/core';

@Component({
  selector: 'app-account-edit',
  template: '',
  styles: [''],
  standalone: false
})
export abstract class AbstractEditComponent<T> implements OnInit, OnDestroy {

  mode = SharedHelper.EditMode.VIEW;
  dto?: T;
  protected delete?: (dto: T) => Observable<void>;
  protected create?: (dto: T) => Observable<T>;
  protected update?: (dto: T) => Observable<T>;
  loading = false;
  error: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    protected fb: FormBuilder,
    protected router: Router,
    protected route: ActivatedRoute,
    protected dialog: MatDialog
  ) { }

  protected abstract get isInvalidForm(): boolean;

  protected abstract getBaseRoute(): string;

  protected abstract enableAllForms(): void;

  protected abstract disableAllForms(): void;

  protected abstract duplicate(): T;

  protected abstract populateForms(dto: T): void;

  protected abstract setupCreateMode(): void;

  protected abstract buildDtoFromForms(): T;

  get isCreateMode(): boolean {
    return this.mode === SharedHelper.EditMode.CREATE;
  }

  get isEditMode(): boolean {
    return this.mode === SharedHelper.EditMode.EDIT;
  }

  get isViewMode(): boolean {
    return this.mode === SharedHelper.EditMode.VIEW;
  }

  get showCancelButton(): boolean {
    return this.isCreateMode || this.isEditMode;
  }

  get showCreateButton(): boolean {
    return this.isEditMode || this.isViewMode;
  }

  get showEditButton(): boolean {
    return this.isViewMode;
  }

  get showDuplicateButton(): boolean {
    return this.isViewMode;
  }

  get showDeleteButton(): boolean {
    return this.isViewMode;
  }

  get showSaveButton(): boolean {
    return this.isCreateMode || this.isEditMode;
  }

  protected abstract initializeForms(): void;

  ngOnInit(): void {
    this.initializeForms();
    this.loadFormData();
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onCancel(): void {
    this.router.navigate([this.getBaseRoute()]);
  }

  onBack(): void {
    this.router.navigate([this.getBaseRoute()]);
  }

  onCreate(): void {
    this.router.navigate([this.getBaseRoute(), 'edit', 'create'], {
      state: { mode: 'create' }
    });
  }

  onEdit(): void {
    if (this.isViewMode) {
      this.mode = SharedHelper.EditMode.EDIT;
      this.enableAllForms();
    }
  }

  onDuplicate(): void {
    if (!this.dto) {
      return;
    }

    // Create a deep copy of the account
    const duplicated = this.duplicate();

    // Navigate to create mode with duplicated data
    this.router.navigate([this.getBaseRoute(), 'edit', 'create'], {
      state: { mode: 'create', dto: duplicated }
    });
  }

  onDelete(): void {
    if (!this.dto) {
      return;
    }

    const dialogRef = this.dialog.open(DeleteDialogComponent, {
      width: '400px',
      data: {
        dto: this.dto,
        deleteAction: this.delete
      } as DeleteDialogData<T>
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Navigate back to accounts list
        this.router.navigate([this.getBaseRoute()]);
      }
    });
  }

  protected loadFormData(): void {
    // Get navigation state data
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state || history.state;

    // Get mode from route params or state
    this.route.params.subscribe(params => {
      const modeParam = params['mode'] || state.mode;

      if (modeParam === 'create') {
        this.mode = SharedHelper.EditMode.CREATE;
        // Check if we have a duplicated account to populate
        if (state.dto) {
          this.dto = state.dto;
          if (this.dto) {
            this.populateForms(this.dto);
          }
        } else {
          this.setupCreateMode();
        }
        this.enableAllForms();
      } else if (modeParam === 'edit' && state.dto) {
        this.mode = SharedHelper.EditMode.EDIT;
        this.dto = state.dto;
        if (this.dto) {
          this.populateForms(this.dto);
        }
      } else if (modeParam === 'view' && state.dto) {
        this.mode = SharedHelper.EditMode.VIEW;
        this.dto = state.dto;
        if (this.dto) {
          this.populateForms(this.dto);
        }
        this.disableAllForms();
      } else {
        // Invalid state - redirect back to index
        this.router.navigate([this.getBaseRoute()]);
      }
    });
  }

  onSave(): void {
    if (this.mode === SharedHelper.EditMode.VIEW) {
      return;
    }

    if (this.isInvalidForm) {
      this.error = 'Please fill in all required fields before saving';
      return;
    }

    this.loading = true;
    this.error = null;

    const data = this.buildDtoFromForms();

    if (this.isCreateMode) {

      if (this.create === undefined) {
        throw new Error('Create function is not defined');
      }

      this.create(data).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate([this.getBaseRoute()]);
        },
        error: (err) => {
          console.error('Failed to create account:', err);
          this.error = 'Failed to create account. Please try again.';
          this.loading = false;
        }
      });
    } else if (this.isEditMode && this.dto) {
      if (this.update === undefined) {
        throw new Error('Update function is not defined');
      }

      this.update(data).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate([this.getBaseRoute()]);
        },
        error: (err) => {
          console.error('Failed to update account:', err);
          this.error = 'Failed to update account. Please try again.';
          this.loading = false;
        }
      });
    }
  }

}
