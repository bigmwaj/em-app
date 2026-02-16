import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { GroupService } from '../../service/group.service';
import { GroupDto, HolderTypeLvo } from '../../api.platform.model';
import { GroupDeleteDialogComponent } from './delete-dialog.component';
import { SharedHelper } from '../../../shared/shared.helper';
import { AbstractEditComponent } from '../../../shared/component/abstract-edit.component';

@Component({
  selector: 'app-group-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
  standalone: false
})
export class GroupEditComponent extends AbstractEditComponent implements OnInit {

  groupForm!: FormGroup;
  group?: GroupDto;
  loading = false;
  error: string | null = null;

  // Enums for dropdowns
  EditMode = SharedHelper.EditMode;
  HolderTypeLvo = HolderTypeLvo;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private groupService: GroupService,
    private dialog: MatDialog
  ) {
    super();
  }

  get isInvalidForm(): boolean {
    return this.groupForm.invalid;
  }

  ngOnInit(): void {
    this.initializeForm();
    this.loadGroupData();
  }

  private initializeForm(): void {
    this.groupForm = this.fb.group({
      id: [null],
      name: ['', Validators.required],
      description: [''],
      holderType: [HolderTypeLvo.CORPORATE, Validators.required]
    });
  }

  private loadGroupData(): void {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state || history.state;

    this.route.params.subscribe(params => {
      const modeParam = params['mode'] || state.mode;

      if (modeParam === 'create') {
        this.mode = SharedHelper.EditMode.CREATE;
        this.group = state.group;
        if (this.group) {
          this.populateForm(this.group);
        }
      } else if (modeParam === 'view') {
        this.mode = SharedHelper.EditMode.VIEW;
        this.group = state.group;
        if (this.group) {
          this.populateForm(this.group);
          this.groupForm.disable();
        }
      } else if (modeParam === 'edit') {
        this.mode = SharedHelper.EditMode.EDIT;
        this.group = state.group;
        if (this.group) {
          this.populateForm(this.group);
        }
      }
    });
  }

  private populateForm(group: GroupDto): void {
    this.groupForm.patchValue({
      id: group.id,
      name: group.name,
      description: group.description,
      holderType: group.holderType
    });
  }

  onSave(): void {
    if (this.groupForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = null;

    const groupData: GroupDto = this.groupForm.value;

    const saveObservable = this.mode === SharedHelper.EditMode.CREATE
      ? this.groupService.createGroup(groupData)
      : this.groupService.updateGroup(groupData);

    saveObservable.subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/groups']);
      },
      error: (err) => {
        console.error('Failed to save group:', err);
        this.error = 'Failed to save group. Please try again.';
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/groups']);
  }

  onEdit(): void {
    this.mode = SharedHelper.EditMode.EDIT;
    this.groupForm.enable();
  }

  onDelete(): void {
    if (this.group?.id === undefined) {
      this.error = 'Group ID is missing. Cannot delete group.';
      return;
    }

    const dialogRef = this.dialog.open(GroupDeleteDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Group Deletion',
        warningMessage: `Are you sure you want to delete group "${this.group.name}"? This action cannot be undone.`,
        group: this.group
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.router.navigate(['/groups']);
      }
    });
  }
}
