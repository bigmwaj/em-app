import { SharedHelper } from '../shared.helper';

export abstract class AbstractEditComponent {
  
  mode = SharedHelper.EditMode.VIEW;

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

  get showChangeStatusButton(): boolean {
    return this.isViewMode;
  }

  get showDeleteButton(): boolean {
    return this.isViewMode;
  }
  
  get showSaveButton(): boolean {
    return this.isCreateMode || this.isEditMode;
  }

}
