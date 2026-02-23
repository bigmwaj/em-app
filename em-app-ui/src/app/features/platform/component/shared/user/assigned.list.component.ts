import { AfterViewInit, Component, EventEmitter, Input, Output, ViewChild } from "@angular/core";
import { UserAssignableDto } from "../../../api.platform.model";
import { Observable, of } from "rxjs";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { Router } from "@angular/router";
import { MatDialog } from "@angular/material/dialog";
import { EditActionLvo, SearchInfos, SearchResult } from "../../../../shared/api.shared.model";
import { ContactHelper } from "../../../helper/contact.helper";
import { MatPaginator, PageEvent } from "@angular/material/paginator";

@Component({
  selector: 'app-shared-user-assigned-list',
  templateUrl: './assigned.list.component.html',
  styleUrls: ['./assigned.list.component.scss'],
  standalone: false
})
export class SharedUserAssignedListComponent extends AbstractIndexComponent<UserAssignableDto>  implements AfterViewInit{

  displayedColumns: string[] = ['select', 'username', 'actions', 'fullName', 'defaultEmail', 'defaultPhone', 'defaultAddress'];

  @Output() onUserRemoved = new EventEmitter<UserAssignableDto>();

  @Input()
  ownerId?: number;

  @Input()
  isViewMode = false;

  @Input()
  searchEndPoint?: (ownerId: number) => Observable<SearchResult<UserAssignableDto>>;

  ContactHelper = ContactHelper;

  @ViewChild(MatPaginator) currentPaginator!: MatPaginator;

  constructor(
    protected override router: Router,
    protected override dialog: MatDialog) {
    super(router, dialog);
  }

  ngAfterViewInit() {
    this.paginator = this.currentPaginator;
  }

  override search(): Observable<SearchResult<UserAssignableDto>> {
    if (this.ownerId && this.searchEndPoint) {
      return this.searchEndPoint(this.ownerId);
    }

    return of({ data: [], searchInfos: {} as SearchInfos } as SearchResult<UserAssignableDto>);
  }

  protected override getBaseRoute(): string {
    throw new Error("Method not implemented.");
  }

  protected override duplicateDto(dto: UserAssignableDto): UserAssignableDto {
    throw new Error("Method not implemented.");
  }

  override getKeyLabel(dto: UserAssignableDto): string | number {
    return dto?.user?.id || '';
  }

  override equals(dto1: UserAssignableDto, dto2: UserAssignableDto): boolean {
    return dto1 === dto2 || (dto1?.user?.id === dto2?.user?.id);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    if (this.isViewMode) {
      this.displayedColumns = this.displayedColumns.filter(col => col !== 'select' && col !== 'actions');
    }
  }

  isDeleted(rp: UserAssignableDto): boolean {
    return rp.editAction === EditActionLvo.DELETE;
  }

  isCreated(rp: UserAssignableDto): boolean {
    return rp.editAction === EditActionLvo.CREATE;
  }

  /**
   * Call by SharedUserAssignListComponent when a user is unchecked from the assign list.
   * @param user to remove from the role's roleUsers list and deselect from assignUsersTable
   */
  removeUser(ur: UserAssignableDto) {
    if (ur.editAction === EditActionLvo.CREATE) {
      this.onUserRemoved.emit(ur);
    } else {
      ur.editAction = ur.editAction === EditActionLvo.DELETE ? EditActionLvo.NONE : EditActionLvo.DELETE;
    }
  }

  override  handlePageEvent(e: PageEvent) {
    this.searchCriteria.pageIndex = e.pageIndex;
    this.searchCriteria.pageSize = e.pageSize;
    //this.loadData();
  }

  override setData(data: UserAssignableDto[]): void {
    super.setData(data);
    this.searchResult.searchInfos.total = data.length;
  }
}
