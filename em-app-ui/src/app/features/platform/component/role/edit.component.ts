import { Component, ViewChild, AfterViewInit } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { RoleService } from '../../service/role.service';
import { RoleDto, OwnerTypeLvo, RolePrivilegeDto, PrivilegeDto, RoleUserDto, UserDto } from '../../api.platform.model';
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';
import { RoleHelper } from '../../helper/role.helper';
import { RolePrivilegeAssignListComponent } from './privilege/assign.list.component';
import { RolePrivilegeAssignedListComponent } from './privilege/assigned.list.component';
import { SelectionChange } from '@angular/cdk/collections';
import { SharedUserAssignedListComponent } from '../shared/user/assigned.list.component';
import { SharedUserAssignListComponent } from '../shared/user/assign.list.component';

@Component({
  selector: 'app-role-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false,
})
export class RoleEditComponent extends AbstractEditComponent<RoleDto> implements AfterViewInit {
  RoleHelper = RoleHelper;

  // Enums for dropdowns
  OwnerTypeLvo = OwnerTypeLvo;

  @ViewChild(RolePrivilegeAssignedListComponent)
  private assignedPrivilegesTable!: RolePrivilegeAssignedListComponent;

  @ViewChild(RolePrivilegeAssignListComponent)
  private assignPrivilegesTable?: RolePrivilegeAssignListComponent;

  @ViewChild(SharedUserAssignedListComponent)
  private assignedUsersTable!: SharedUserAssignedListComponent;

  @ViewChild(SharedUserAssignListComponent)
  private assignUsersTable?: SharedUserAssignListComponent;

  searchRoleUsersEndPoint = (ownerId: number) => this.service.getRoleUsers(ownerId);

  constructor(
    protected override helper: RoleHelper,
    private service: RoleService) {

    super(helper);

    this.delete = (dto) => this.service.deleteRole(dto);
    this.create = (dto) => this.service.createRole(dto);
    this.update = (dto) => this.service.updateRole(dto);
  }

  ngAfterViewInit(): void {
    this.setupSyncBetweenTables();
  }

  private setupSyncBetweenTables() {
    if (this.assignPrivilegesTable && this.assignPrivilegesTable.selection) {
      this.subscription$.push(this.assignPrivilegesTable.selection.changed
        .subscribe((sc: SelectionChange<PrivilegeDto>) => {
        sc.added.map(p => this.mapPrivilege(p)).forEach(p => this.privilegeChecked(p));
        sc.removed.map(p => this.mapPrivilege(p)).forEach(p => this.privilegeUnchecked(p));
      }));
    }

    if (this.assignUsersTable && this.assignUsersTable.selection) {
      this.subscription$.push(this.assignUsersTable.selection.changed
        .subscribe((sc: SelectionChange<UserDto>) => {
        sc.added.map(u => this.mapUser(u)).forEach(u => this.userChecked(u));
        sc.removed.map(u => this.mapUser(u)).forEach(u => this.userUnchecked(u));
      }));
    }
  }

  private mapPrivilege(privilege: PrivilegeDto): RolePrivilegeDto {
    return {
      privilege: privilege,
      roleId: this.dto?.id,
    } as RolePrivilegeDto;
  }

  private mapUser(user: UserDto): RoleUserDto {
    return {
      user: user,
      roleId: this.dto?.id,
    } as RoleUserDto;
  }

  public privilegeChecked(rp: RolePrivilegeDto) {
    const refRp = this.assignedPrivilegesTable.getItemReference(rp);
    if (!refRp) {
      rp.New = true;
      this.assignedPrivilegesTable.appendItem(rp);
    } else {
      refRp.checked = false;
    }
  }

  public privilegeUnchecked(rp: RolePrivilegeDto) {
    const refRp = this.assignedPrivilegesTable.getItemReference(rp);
    if (refRp) {
      if (refRp.checked === true) {
        this.assignedPrivilegesTable.removeItem(refRp);
      } else {
        refRp.checked = false;
      }
    } else {
      this.assignedPrivilegesTable.removeItem(rp);
    }
  }

  public privilegeRemoved(rp: RolePrivilegeDto) {
    if (!this.assignPrivilegesTable?.selection || !rp.privilege) return;
    this.assignPrivilegesTable.selection.deselect(rp.privilege);
  }

  public userChecked(ru: RoleUserDto) {
    const refRu = this.assignedUsersTable.getItemReference(ru);
    if (!refRu) {
      ru.New = true;
      this.assignedUsersTable.appendItem(ru);
    } else {
      refRu.checked = false;
    }
  }

  public userUnchecked(ru: RoleUserDto) {
    const refRu = this.assignedUsersTable.getItemReference(ru);
    if (refRu) {
      if (refRu.checked === true) {
        this.assignedUsersTable.removeItem(refRu);
      } else {
        refRu.checked = false;
      }
    } else {
      this.assignedUsersTable.removeItem(ru);
    }
  }

  public userRemoved(rp: RoleUserDto) {
    if (!this.assignUsersTable?.selection || !rp.user) return;
    this.assignUsersTable.selection.deselect(rp.user);
  }

  protected override initializeForms(): FormGroup[] {
    this.mainForm = this.fb.group({
      id: [this.dto?.id],
      name: [this.dto?.name, [Validators.required, Validators.maxLength(32)]],
      description: [this.dto?.description, Validators.maxLength(256)],
      ownerType: [this.dto?.ownerType, Validators.required]
    });
    this.bindFormEvents();
    return [this.mainForm];
  }

  protected buildDtoFromForms(): RoleDto {
    const formValue = this.mainForm.value;

    const dto: RoleDto = {
      id: formValue.id,
      name: formValue.name,
      description: formValue.description,
      ownerType: formValue.ownerType,
      rolePrivileges: this.assignedPrivilegesTable.data,
      roleUsers: this.assignedUsersTable.data
    };
    return dto;
  }

  private bindFormEvents() {
    // The role name should always be uppercase, so we listen to changes and transform the value before saving it in the form control
    this.subscription$.push(
      this.mainForm.get('name')?.valueChanges.subscribe(value => {
        const upperCaseValue = value ? value.toUpperCase() : value;
        if (value !== upperCaseValue) {
          this.mainForm.get('name')?.setValue(upperCaseValue, { emitEvent: false });
        }
      }) as any
    );
  }
}
