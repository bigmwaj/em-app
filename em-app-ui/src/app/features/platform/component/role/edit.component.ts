import { Component, ViewChild, AfterViewInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { RoleService } from '../../service/role.service';
import { RoleDto, HolderTypeLvo, RolePrivilegeDto, PrivilegeDto, RoleUserDto, UserDto } from '../../api.platform.model';
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';
import { RoleHelper } from '../../helper/role.helper';
import { RolePrivilegeAssignListComponent } from './privilege/assign.list.component';
import { RolePrivilegeAssignedListComponent } from './privilege/assigned.list.component';
import { SelectionChange } from '@angular/cdk/collections';
import { EditActionLvo } from '../../../shared/api.shared.model';
import { RoleUserAssignedListComponent } from './user/assigned.list.component';
import { RoleUserAssignListComponent } from './user/assign.list.component';

@Component({
  selector: 'app-role-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false,
})
export class RoleEditComponent extends AbstractEditComponent<RoleDto> implements AfterViewInit {
  RoleHelper = RoleHelper;

  // Enums for dropdowns
  HolderTypeLvo = HolderTypeLvo;

  @ViewChild(RolePrivilegeAssignedListComponent)
  private assignedPrivilegesTable!: RolePrivilegeAssignedListComponent;

  @ViewChild(RolePrivilegeAssignListComponent)
  private assignPrivilegesTable?: RolePrivilegeAssignListComponent;

  @ViewChild(RoleUserAssignedListComponent)
  private assignedUsersTable!: RoleUserAssignedListComponent;

  @ViewChild(RoleUserAssignListComponent)
  private assignUsersTable?: RoleUserAssignListComponent;

  constructor(
    protected override fb: FormBuilder,
    protected override router: Router,
    protected override route: ActivatedRoute,
    protected override dialog: MatDialog,
    private service: RoleService) {

    super(fb, router, route, dialog);

    this.delete = (dto) => this.service.deleteRole(dto);
    this.create = (dto) => this.service.createRole(dto);
    this.update = (dto) => this.service.updateRole(dto);
    this.buildFormData = (dto) => RoleHelper.buildFormData(dto);
  }
  
  ngAfterViewInit(): void {
    this.setupSyncBetweenTables();
  }

  private setupSyncBetweenTables() {
    if (this.assignPrivilegesTable && this.assignPrivilegesTable.selection) {
      this.assignPrivilegesTable.selection.changed.subscribe((sc: SelectionChange<PrivilegeDto>) => {
        sc.added.map(p => this.mapPrivilege(p)).forEach(p => this.privilegeChecked(p));
        sc.removed.map(p => this.mapPrivilege(p)).forEach(p => this.privilegeUnchecked(p));
      });
    }

    if (this.assignUsersTable && this.assignUsersTable.selection) {
      this.assignUsersTable.selection.changed.subscribe((sc: SelectionChange<UserDto>) => {
        sc.added.map(u => this.mapUser(u)).forEach(u => this.userChecked(u));
        sc.removed.map(u => this.mapUser(u)).forEach(u => this.userUnchecked(u));
      });
    }
  }

  private mapPrivilege(privilege: PrivilegeDto): RolePrivilegeDto {
    return {
      privilege: privilege,
      roleId: this.dto?.id,
      editAction: EditActionLvo.CREATE
    } as RolePrivilegeDto;
  }

  private mapUser(user: UserDto): RoleUserDto {
    return {
      user: user,
      roleId: this.dto?.id,
      editAction: EditActionLvo.CREATE
    } as RoleUserDto;
  }

  public privilegeChecked(rp: RolePrivilegeDto) {
    const refRp = this.assignedPrivilegesTable.getItemReference(rp);
    if (!refRp) {
      this.assignedPrivilegesTable.appendItem(rp);
    } else {
      refRp.editAction = EditActionLvo.NONE;
    }
  }

  public privilegeUnchecked(rp: RolePrivilegeDto) {
    const refRp = this.assignedPrivilegesTable.getItemReference(rp);
    if (refRp) {
      if (refRp.editAction === EditActionLvo.CREATE) {
        this.assignedPrivilegesTable.removeItem(refRp);
      } else {
        refRp.editAction = EditActionLvo.DELETE;
      }
    } else {
      this.assignedPrivilegesTable.removeItem(rp);
    }
  }

  public privilegeRemoved(rp: RolePrivilegeDto) {
    if (!this.assignPrivilegesTable?.selection || !rp.privilege)  return;
    this.assignPrivilegesTable.selection.deselect(rp.privilege);
  }

  public userChecked(ru: RoleUserDto) {
    const refRu = this.assignedUsersTable.getItemReference(ru);
    if (!refRu) {
      this.assignedUsersTable.appendItem(ru);
    } else {
      refRu.editAction = EditActionLvo.NONE;
    }
  }

  public userUnchecked(ru: RoleUserDto) {
    const refRu = this.assignedUsersTable.getItemReference(ru);
    if (refRu) {
      if (refRu.editAction === EditActionLvo.CREATE) {
        this.assignedUsersTable.removeItem(refRu);
      } else {
        refRu.editAction = EditActionLvo.DELETE;
      }
    } else {
      this.assignedUsersTable.removeItem(ru);
    }
  }

  public userRemoved(rp: RoleUserDto) {
    if (!this.assignUsersTable?.selection || !rp.user) return;
    this.assignUsersTable.selection.deselect(rp.user);
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
      rolePrivileges: this.fb.array(this.dto?.rolePrivileges?.map(rp => this.initializePrivilege(rp)) || [])
    });
    return [this.mainForm];
  }

  private initializePrivilege(rp?: RolePrivilegeDto): FormGroup {
    return this.fb.group({
      roleId: [rp?.roleId],
      //privilegeId: [rp?.privilegeId]
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
      editAction: this.editAction,
      id: formValue.id,
      name: formValue.name,
      description: formValue.description,
      holderType: formValue.holderType,
      rolePrivileges: this.assignedPrivilegesTable.data,
      roleUsers: this.assignedUsersTable.data
    };
    return dto;
  }
}
