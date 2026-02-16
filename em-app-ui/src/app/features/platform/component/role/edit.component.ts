import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormRole, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { RoleService } from '../../service/role.service';
import { RoleDto, HolderTypeLvo } from '../../api.platform.model';
import { RoleDeleteDialogComponent } from './delete-dialog.component';
import { SharedHelper } from '../../../shared/shared.helper';
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';

@Component({
  selector: 'app-role-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false
})
export class RoleEditComponent extends AbstractEditComponent implements OnInit {

  roleForm!: FormRole;
  role?: RoleDto;
  loading = false;
  error: string | null = null;

  // Enums for dropdowns
  EditMode = SharedHelper.EditMode;
  HolderTypeLvo = HolderTypeLvo;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private roleService: RoleService,
    private dialog: MatDialog
  ) {
    super();
  }

  get isInvalidForm(): boolean {
    return this.roleForm.invalid;
  }

  ngOnInit(): void {
    this.initializeForm();
    this.loadRoleData();
  }

  private initializeForm(): void {
    this.roleForm = this.fb.role({
      id: [null],
      name: ['', Validators.required],
      description: [''],
      holderType: [HolderTypeLvo.CORPORATE, Validators.required]
    });
  }

  private loadRoleData(): void {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state || history.state;

    this.route.params.subscribe(params => {
      const modeParam = params['mode'] || state.mode;

      if (modeParam === 'create') {
        this.mode = SharedHelper.EditMode.CREATE;
        this.role = state.role;
        if (this.role) {
          this.populateForm(this.role);
        }
      } else if (modeParam === 'view') {
        this.mode = SharedHelper.EditMode.VIEW;
        this.role = state.role;
        if (this.role) {
          this.populateForm(this.role);
          this.roleForm.disable();
        }
      } else if (modeParam === 'edit') {
        this.mode = SharedHelper.EditMode.EDIT;
        this.role = state.role;
        if (this.role) {
          this.populateForm(this.role);
        }
      }
    });
  }

  private populateForm(role: RoleDto): void {
    this.roleForm.patchValue({
      id: role.id,
      name: role.name,
      description: role.description,
      holderType: role.holderType
    });
  }

  onSave(): void {
    if (this.roleForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = null;

    const roleData: RoleDto = this.roleForm.value;

    const saveObservable = this.mode === SharedHelper.EditMode.CREATE
      ? this.roleService.createRole(roleData)
      : this.roleService.updateRole(roleData);

    saveObservable.subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/roles']);
      },
      error: (err) => {
        console.error('Failed to save role:', err);
        this.error = 'Failed to save role. Please try again.';
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/roles']);
  }

  onEdit(): void {
    this.mode = SharedHelper.EditMode.EDIT;
    this.roleForm.enable();
  }

  onDelete(): void {
    if (this.role?.id === undefined) {
      this.error = 'Role ID is missing. Cannot delete role.';
      return;
    }

    const dialogRef = this.dialog.open(RoleDeleteDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Role Deletion',
        warningMessage: `Are you sure you want to delete role "${this.role.name}"? This action cannot be undone.`,
        role: this.role
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.router.navigate(['/roles']);
      }
    });
  }
}
