import { FormBuilder, FormGroup } from '@angular/forms';
import { PageData, BaseHelper } from '../base.helper';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DeleteDialogComponent, DeleteDialogData } from './delete-dialog.component';
import { map, Observable, Subscription } from 'rxjs';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { AbstractBaseDto } from '../api.shared.model';

@Component({
  selector: 'app-abstract-edit',
  template: '',
  styles: [''],
  standalone: false
})
export abstract class AbstractEditComponent<T extends AbstractBaseDto> implements OnInit, OnDestroy {

  protected fb = inject(FormBuilder);

  protected router = inject(Router);

  protected route = inject(ActivatedRoute);

  protected dialog = inject(MatDialog);

  protected mode: string;

  dto?: T; // The main data object being edited

  protected delete?: (dto: T) => Observable<void>;

  protected create?: (dto: T) => Observable<T>;

  protected update?: (dto: T) => Observable<T>;

  pageData: PageData = new PageData();

  protected subscription$: Subscription[] = [];

  private forms!: FormGroup[];

  protected mainForm!: FormGroup; // Will handle main entity fields

  constructor(protected helper: BaseHelper<T>) {
    this.mode = helper.EditMode.VIEW;
  }

  protected abstract buildDtoFromForms(): T;

  protected abstract initializeForms(dto?: T): FormGroup[];

  get isCreateMode(): boolean {
    return this.mode === this.helper.EditMode.CREATE;
  }

  get isEditMode(): boolean {
    return this.mode === this.helper.EditMode.EDIT;
  }

  get isViewMode(): boolean {
    return this.mode === this.helper.EditMode.VIEW;
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

  private addGlobalEventsListeners(): void {
    this.forms.forEach(form => form.valueChanges.subscribe(() => {
      if (form.get('editAction') === null) {
        //form.addControl('editAction', this.fb.control(this.editAction));
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
    this.router.navigate([this.helper.baseRoute]);
  }

  onBack(): void {
    this.router.navigate([this.helper.baseRoute]);
  }

  private _edit(mode: string, dto?: T): void {
    this.helper.getBackedDto(mode, dto)
      .subscribe(backedDto => {
        this.router.navigateByUrl('/', { skipLocationChange: true })
          .then(() => this.router.navigate([this.helper.baseRoute, 'edit', mode], {
            state: { mode, dto: backedDto }
          }));
      });
  }

  onView(): void {
    this._edit(this.helper.EditMode.VIEW, this.dto);
  }

  onCreate(): void {
    this._edit(this.helper.EditMode.CREATE);
  }

  onEdit(): void {
    this._edit(this.helper.EditMode.EDIT, this.dto);
  }

  onDuplicate(): void {
    this._edit(this.helper.EditMode.CREATE, this.dto);
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
        this.router.navigate([this.helper.baseRoute]);
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
      const validModes = [this.helper.EditMode.CREATE, this.helper.EditMode.EDIT, this.helper.EditMode.VIEW];
      if (state.dto && validModes.includes(modeParam)) {
        this.dto = state.dto;
        this.mode = modeParam;
      } else {
        this.router.navigate([this.helper.baseRoute]);
      }

      this.forms = this.initializeForms();
      if (this.isViewMode) {
        this.disableAllForms();
      } else {
        this.enableAllForms();
      }
    });
  }

  onSave(): void {
    if (this.mode === this.helper.EditMode.VIEW) {
      console.warn('Save action is not allowed in view mode');
      return;
    }

    if (this.isInvalidForm) {
      console.warn('Cannot save because some forms are invalid');
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

    } else if (this.isEditMode) {
      if (this.update === undefined) {
        throw new Error('Update function is not defined');
      }
      saveObservable = this.update(dto);
    } else {
      throw new Error('Invalid mode for saving data');
    }


    this.subscription$.push(saveObservable.pipe(map((response: any) => response.data as T)).subscribe({
      next: (savedDto) => {
        this.pageData.loading.set(false);
        this._edit(this.helper.EditMode.VIEW, savedDto);
      },
      error: (err) => {
        console.error('Failed to save data:', err);
        this.pageData.error.set(err.error || 'An error occurred while saving. Please try again.');
        this.pageData.loading.set(false);
      }
    }));
  }
}
