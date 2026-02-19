import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { GroupService } from '../../service/group.service';
import { GroupDto, HolderTypeLvo } from '../../api.platform.model';
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';
import { GroupHelper } from '../../helper/group.helper';

@Component({
  selector: 'app-group-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false,
})
export class GroupEditComponent extends AbstractEditComponent<GroupDto> {
  GroupHelper = GroupHelper;

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
    this.buildFormData = (dto) => GroupHelper.buildFormData(dto);
  }

  protected override getBaseRoute(): string {
    return '/groups';
  }

  protected override setupCreateMode(): void {
    // Initialize with default values for create mode
    this.mainForm.patchValue({
      holderType: HolderTypeLvo.ACCOUNT
    });
  }

  protected override initializeForms(): FormGroup[] {    
    this.mainForm = this.fb.group({
      id: [this.dto?.id],
      name: [this.dto?.name, Validators.required],
      description: [this.dto?.description],
      holderType: [this.dto?.holderType, Validators.required]
    });
    return [this.mainForm];
  }

  protected populateForms(): void {
    this.mainForm.patchValue({
      id: this.dto!.id,
      name: this.dto!.name,
      description: this.dto!.description,
      holderType: this.dto!.holderType
    });
  }

  protected buildDtoFromForms(): GroupDto {
    const formValue = this.mainForm.value;

    const groupDto: GroupDto = {
      id: formValue.id,
      editAction: this.editAction,
      name: formValue.name,
      description: formValue.description,
      holderType: formValue.holderType
    };

    return groupDto;
  }
}
