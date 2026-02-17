import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { RoleService } from '../../service/role.service';
import { RoleDto, HolderTypeLvo } from '../../api.platform.model';
import { PlatformHelper } from '../../platform.helper';
import { SharedHelper } from '../../../shared/shared.helper';
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';

@Component({
  selector: 'app-role-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false,
})
export class RoleEditComponent extends AbstractEditComponent<RoleDto> {

  roleForm!: FormGroup;

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
  }

  get isInvalidForm(): boolean {
    return this.roleForm.invalid;
  }

  protected override getBaseRoute(): string {
    return '/roles';
  }

  protected duplicate(): RoleDto {
    if (!this.dto) {
      throw new Error('No role data to duplicate');
    }
    return PlatformHelper.duplicateRole(this.dto)
  }

  protected disableAllForms(): void {
    this.roleForm.disable();
  }

  protected enableAllForms(): void {
    this.roleForm.enable();
  }

  protected override setupCreateMode(): void {
    // Initialize with default values for create mode
    this.roleForm.patchValue({
      holderType: HolderTypeLvo.ACCOUNT
    });
  }

  protected initializeForms(): void {
    this.roleForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      holderType: [HolderTypeLvo.ACCOUNT, Validators.required]
    });
  }

  protected populateForms(): void {
    this.roleForm.patchValue({
      name: this.dto!.name,
      description: this.dto!.description,
      holderType: this.dto!.holderType
    });
  }

  protected buildDtoFromForms(): RoleDto {
    const formValue = this.roleForm.value;

    const roleDto: RoleDto = {
      name: formValue.name,
      description: formValue.description,
      holderType: formValue.holderType
    };

    // Add ID if editing
    if (this.mode === SharedHelper.EditMode.EDIT && this.dto?.id) {
      roleDto.id = this.dto.id;
    }

    return roleDto;
  }
}
