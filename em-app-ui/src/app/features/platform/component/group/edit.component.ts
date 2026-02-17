import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { GroupService } from '../../service/group.service';
import { GroupDto, HolderTypeLvo } from '../../api.platform.model';
import { PlatformHelper } from '../../platform.helper';
import { SharedHelper } from '../../../shared/shared.helper';
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';

@Component({
  selector: 'app-group-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false,
})
export class GroupEditComponent extends AbstractEditComponent<GroupDto> {

  groupForm!: FormGroup;

  // Enums for dropdowns
  HolderTypeLvo = HolderTypeLvo;

  constructor(
    protected override fb: FormBuilder,
    protected override router: Router,
    protected override route: ActivatedRoute,
    protected override dialog: MatDialog,
    private service: GroupService,
  ) {
    super(fb, router, route, dialog);

    this.delete = (dto) => this.service.deleteGroup(dto);
    this.create = (dto) => this.service.createGroup(dto);
    this.update = (dto) => this.service.updateGroup(dto);
  }

  get isInvalidForm(): boolean {
    return this.groupForm.invalid;
  }

  protected override getBaseRoute(): string {
    return '/groups';
  }

  protected duplicate(): GroupDto {
    if (!this.dto) {
      throw new Error('No group data to duplicate');
    }
    return PlatformHelper.duplicateGroup(this.dto)
  }

  protected disableAllForms(): void {
    this.groupForm.disable();
  }

  protected enableAllForms(): void {
    this.groupForm.enable();
  }

  protected override setupCreateMode(): void {
    // Initialize with default values for create mode
    this.groupForm.patchValue({
      holderType: HolderTypeLvo.ACCOUNT
    });
  }

  protected initializeForms(): void {
    this.groupForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      holderType: [HolderTypeLvo.ACCOUNT, Validators.required]
    });
  }

  protected populateForms(): void {
    this.groupForm.patchValue({
      name: this.dto!.name,
      description: this.dto!.description,
      holderType: this.dto!.holderType
    });
  }

  protected buildDtoFromForms(): GroupDto {
    const formValue = this.groupForm.value;

    const groupDto: GroupDto = {
      name: formValue.name,
      description: formValue.description,
      holderType: formValue.holderType
    };

    // Add ID if editing
    if (this.mode === SharedHelper.EditMode.EDIT && this.dto?.id) {
      groupDto.id = this.dto.id;
    }

    return groupDto;
  }
}
