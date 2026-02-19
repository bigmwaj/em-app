import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { RoleService } from '../../service/role.service';
import { RoleDto, HolderTypeLvo, RolePrivilegeDto } from '../../api.platform.model';
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';
import { RoleHelper } from '../../helper/role.helper';
import { I } from '@angular/cdk/keycodes';

@Component({
  selector: 'app-role-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false,
})
export class RoleEditComponent extends AbstractEditComponent<RoleDto> {
  RoleHelper = RoleHelper;

  // Enums for dropdowns
  HolderTypeLvo = HolderTypeLvo;

  constructor(
    protected override fb: FormBuilder,
    protected override router: Router,
    protected override route: ActivatedRoute,
    protected override dialog: MatDialog,
    private service: RoleService,
  ) {
    super(fb, router, route, dialog);

    this.delete = (dto) => this.service.deleteRole(dto);
    this.create = (dto) => this.service.createRole(dto);
    this.update = (dto) => this.service.updateRole(dto);
    this.buildFormData = (dto) => RoleHelper.buildFormData(dto);
  }

  protected override getBaseRoute(): string {
    return '/roles';
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
      name: [this.dto?.name, [Validators.required, Validators.maxLength(32)]],
      description: [this.dto?.description, Validators.maxLength(256)],
      holderType: [this.dto?.holderType, Validators.required],
      privileges: this.fb.array(this.dto?.privileges?.map(priv => this.initializePrivilege(priv)) || [])
    });
    return [this.mainForm];
  }

  private initializePrivilege(priv?: RolePrivilegeDto): FormGroup {
    return this.fb.group({
      roleId: [priv?.roleId],
      privilegeId: [priv?.privilegeId]
    });
  }

  protected populateForms(): void {
    this.mainForm.patchValue({
      id: this.dto?.id,
      name: this.dto?.name,
      description: this.dto?.description,
      holderType: this.dto?.holderType
    });
  }

  protected buildDtoFromForms(): RoleDto {
    const formValue = this.mainForm.value;

    const dto: RoleDto = {
      editAction: formValue.editAction,
      id: formValue.id,
      name: formValue.name,
      description: formValue.description,
      holderType: formValue.holderType
    };

    return dto;
  }
}
