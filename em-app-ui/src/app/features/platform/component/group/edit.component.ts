import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { GroupService } from '../../service/group.service';
import { GroupDto, GroupRoleDto, GroupUserDto, HolderTypeLvo, RoleDto, UserDto } from '../../api.platform.model';
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';
import { GroupHelper } from '../../helper/group.helper';
import { GroupRoleAssignListComponent } from './role/assign.list.component';
import { GroupRoleAssignedListComponent } from './role/assigned.list.component';
import { SelectionChange } from '@angular/cdk/collections';
import { EditActionLvo } from '../../../shared/api.shared.model';
import { SharedUserAssignedListComponent } from '../shared/user/assigned.list.component';
import { SharedUserAssignListComponent } from '../shared/user/assign.list.component';

@Component({
  selector: 'app-group-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false,
})
export class GroupEditComponent extends AbstractEditComponent<GroupDto> implements AfterViewInit {
  GroupHelper = GroupHelper;

  // Enums for dropdowns
  HolderTypeLvo = HolderTypeLvo;
  
  searchGroupUsersEndPoint = (ownerId: number) => this.service.getGroupUsers(ownerId);

  @ViewChild(GroupRoleAssignedListComponent)
  private assignedRolesTable!: GroupRoleAssignedListComponent;

  @ViewChild(GroupRoleAssignListComponent)
  private assignRolesTable?: GroupRoleAssignListComponent;

  @ViewChild(SharedUserAssignedListComponent)
  private assignedUsersTable!: SharedUserAssignedListComponent;

  @ViewChild(SharedUserAssignListComponent)
  private assignUsersTable?: SharedUserAssignListComponent;

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

  ngAfterViewInit(): void {
    this.setupSyncBetweenTables();
  }

  private setupSyncBetweenTables() {
    if (this.assignRolesTable && this.assignRolesTable.selection) {
      this.subscription$.push(this.assignRolesTable.selection.changed
        .subscribe((sc: SelectionChange<RoleDto>) => {
        sc.added.map(p => this.mapRole(p)).forEach(p => this.roleChecked(p));
        sc.removed.map(p => this.mapRole(p)).forEach(p => this.roleUnchecked(p));
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

  private mapRole(role: RoleDto): GroupRoleDto {
    return {
      role: role,
      groupId: this.dto?.id,
      editAction: EditActionLvo.CREATE
    } as GroupRoleDto;
  }

  private mapUser(user: UserDto): GroupUserDto {
    return {
      user: user,
      groupId: this.dto?.id,
      editAction: EditActionLvo.CREATE
    } as GroupUserDto;
  }

  public roleChecked(gr: GroupRoleDto) {
    const refRp = this.assignedRolesTable.getItemReference(gr);
    if (!refRp) {
      this.assignedRolesTable.appendItem(gr);
    } else {
      refRp.editAction = EditActionLvo.NONE;
    }
  }

  public roleUnchecked(gr: GroupRoleDto) {
    const refRp = this.assignedRolesTable.getItemReference(gr);
    if (refRp) {
      if (refRp.editAction === EditActionLvo.CREATE) {
        this.assignedRolesTable.removeItem(refRp);
      } else {
        refRp.editAction = EditActionLvo.DELETE;
      }
    } else {
      this.assignedRolesTable.removeItem(gr);
    }
  }

  public roleRemoved(gr: GroupRoleDto) {
    if (!this.assignRolesTable?.selection || !gr.role) return;
    this.assignRolesTable.selection.deselect(gr.role);
  }

  public userChecked(gu: GroupUserDto) {
    const refGu = this.assignedUsersTable.getItemReference(gu);
    if (!refGu) {
      this.assignedUsersTable.appendItem(gu);
    } else {
      refGu.editAction = EditActionLvo.NONE;
    }
  }

  public userUnchecked(gu: GroupUserDto) {
    const refGu = this.assignedUsersTable.getItemReference(gu);
    if (refGu) {
      if (refGu.editAction === EditActionLvo.CREATE) {
        this.assignedUsersTable.removeItem(refGu);
      } else {
        refGu.editAction = EditActionLvo.DELETE;
      }
    } else {
      this.assignedUsersTable.removeItem(gu);
    }
  }

  public userRemoved(gu: GroupUserDto) {
    if (!this.assignUsersTable?.selection || !gu.user) return;
    this.assignUsersTable.selection.deselect(gu.user);
  }


  protected override getBaseRoute(): string {
    return '/groups';
  }

  protected override initializeForms(): FormGroup[] {
    this.mainForm = this.fb.group({
      id: [this.dto?.id],
      name: [this.dto?.name, Validators.required],
      description: [this.dto?.description],
      holderType: [this.dto?.holderType, Validators.required]
    });
    this.bindFormEvents();
    return [this.mainForm];
  }

  protected buildDtoFromForms(): GroupDto {
    const formValue = this.mainForm.value;

    const dto: GroupDto = {
      editAction: this.editAction,
      id: formValue.id,
      name: formValue.name,
      description: formValue.description,
      holderType: formValue.holderType,
      groupRoles: this.assignedRolesTable.data,
      groupUsers: this.assignedUsersTable.data
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
