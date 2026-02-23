import { FormBuilder, FormGroup } from '@angular/forms';
import { PageData, SharedHelper } from '../shared.helper';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DeleteDialogComponent, DeleteDialogData } from './delete-dialog.component';
import { Observable, Subscription } from 'rxjs';
import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { EditActionLvo } from '../api.shared.model';

@Component({
  selector: 'app-abstract-edit',
  template: '',
  styles: [''],
  standalone: false
})
export abstract class AbstractEditComponent<T> implements OnInit, OnDestroy {

  protected mode = SharedHelper.EditMode.VIEW;

  dto?: T;

  protected delete?: (dto: T) => Observable<void>;
  protected create?: (dto: T) => Observable<T>;
  protected update?: (dto: T) => Observable<T>;
  protected buildFormData?: (dto?: T) => T;

  pageData: PageData = new PageData();

  protected subscription$: Subscription[] = [];

  private forms!: FormGroup[];
  protected mainForm!: FormGroup; // Will handle main entity fields

  constructor(
    protected fb: FormBuilder,
    protected router: Router,
    protected route: ActivatedRoute,
    protected dialog: MatDialog
  ) { }

  protected abstract getBaseRoute(): string;

  protected abstract buildDtoFromForms(): T;

  protected get editAction(): EditActionLvo {
    return this.mode === SharedHelper.EditMode.EDIT ? EditActionLvo.UPDATE : EditActionLvo.CREATE;
  }

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

  private disableAllForms(): void {
    if (this.forms) {
      this.forms.forEach(form => form.disable());
    }
  }

  private enableAllForms(): void {
    if (this.forms) {
      this.forms.forEach(form => form.enable());
    }
  }

  get isInvalidForm(): boolean {
    return this.forms.some(form => form.invalid);
  }

  protected abstract initializeForms(dto?: T): FormGroup[];

  private addGlobalEventsListeners(): void {
    this.forms.forEach(form => form.valueChanges.subscribe(() => {
      if (form.get('editAction') === null) {
        form.addControl('editAction', this.fb.control(this.editAction));
      }

      if (form === this.mainForm) {
        form.patchValue({ editAction: this.editAction }, { emitEvent: false });
      } else {
        if (this.isCreateMode) {
          form.patchValue({ editAction: EditActionLvo.CREATE }, { emitEvent: false });
        } else if (this.isEditMode) {
          if (form.value.key === undefined) {
            form.patchValue({ editAction: EditActionLvo.CREATE }, { emitEvent: false });
          } else {
            form.patchValue({ editAction: EditActionLvo.UPDATE }, { emitEvent: false });
          }
        }
      }
    }));
  }

  ngOnInit(): void {
    this.setup();
    this.addGlobalEventsListeners();
  }

  ngOnDestroy(): void {
    this.subscription$.forEach(sub => sub.unsubscribe());
  }

  onCancel(): void {
    this.router.navigate([this.getBaseRoute()]);
  }

  onBack(): void {
    this.router.navigate([this.getBaseRoute()]);
  }

  onCreate(): void {
    this.router.navigateByUrl('/', { skipLocationChange: true })
    .then(() => this.router.navigate([this.getBaseRoute(), 'edit', SharedHelper.EditMode.CREATE], {
      state: { mode: SharedHelper.EditMode.CREATE }
    }));
  }

  onEdit(): void {
    this.router.navigateByUrl('/', { skipLocationChange: true })
    .then(() => this.router.navigate([this.getBaseRoute(), 'edit', SharedHelper.EditMode.EDIT], {
      state: { mode: SharedHelper.EditMode.EDIT, dto: this.dto }
    }));
  }

  onDuplicate(): void {
    if (!this.dto) {
      return;
    }

    if (!this.buildFormData) {
      throw new Error("BuildFormData function is not defined");
    }

    // Create a deep copy of the account

    const duplicated = this.buildFormData(this.dto);

    this.router.navigateByUrl('/', { skipLocationChange: true })
    .then(() => this.router.navigate([this.getBaseRoute(), 'edit', SharedHelper.EditMode.CREATE], {
      state: { mode: SharedHelper.EditMode.CREATE, dto: duplicated }
    }));
    
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

  protected setup(): void {
    // Get navigation state data
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state || history.state;

    // Get mode from route params or state
    this.route.params.subscribe(params => {
      const modeParam = params['mode'] || state.mode;

      if (modeParam === SharedHelper.EditMode.CREATE) {
        this.mode = SharedHelper.EditMode.CREATE;
        // Check if we have a duplicated account to populate
        if (state.dto) {
          this.dto = state.dto;
        } else {
          if (!this.buildFormData) {
            throw new Error("BuildFormData function is not defined");
          }
          this.dto = this.buildFormData()
        }
        this.forms = this.initializeForms();
        this.enableAllForms();

      } else if (modeParam === SharedHelper.EditMode.EDIT && state.dto) {
        this.mode = SharedHelper.EditMode.EDIT;
        this.dto = state.dto;
        this.forms = this.initializeForms();
        this.enableAllForms();
      } else if (modeParam === SharedHelper.EditMode.VIEW && state.dto) {
        this.mode = SharedHelper.EditMode.VIEW;
        this.dto = state.dto;
        this.forms = this.initializeForms();
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
      this.pageData.error.set('Please fill in all required fields before saving');
      return;
    }

    this.pageData.loading.set(true);
    this.pageData.error.set(null);

    const dto = this.buildDtoFromForms();

    let saveObservable: Observable<T>;

    if (this.isCreateMode) {
      if (this.create === undefined) {
        throw new Error('Create function is not defined');
      }
      saveObservable = this.create(dto);

    } else if (this.isEditMode && this.dto) {
      if (this.update === undefined) {
        throw new Error('Update function is not defined');
      }
      saveObservable = this.update(dto);
    } else {
      throw new Error('Invalid mode or missing data');
    }

    this.subscription$.push(saveObservable.subscribe({
      next: (savedDto) => {
        this.pageData.loading.set(false);
        this.router.navigate([this.getBaseRoute()], {
          state: { mode: SharedHelper.EditMode.VIEW, dto: savedDto }
        });
      },
      error: (err) => {
        console.error('Failed to create data:', err);
        this.pageData.error.set(err.error || 'An error occurred while saving. Please try again.');
        this.pageData.loading.set(false);
      }
    }));
  }
}
