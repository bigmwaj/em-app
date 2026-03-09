import { AfterViewInit, Component, EventEmitter, Input, Output, ViewChild } from "@angular/core";
import { UserAssignableDto } from "../../../api.platform.model";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { ContactHelper } from "../../../helper/contact.helper";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { UserAssignableHelper } from "../../../helper/user-assignable.helper";

@Component({
  selector: 'app-shared-user-assigned-list',
  templateUrl: './assigned.list.component.html',
  styleUrls: ['./assigned.list.component.scss'],
  standalone: false
})
export class SharedUserAssignedListComponent extends AbstractIndexComponent<UserAssignableDto> implements AfterViewInit {

  displayedColumns: string[] = ['select', 'username', 'actions', 'fullName', 'defaultEmail', 'defaultPhone', 'defaultAddress'];

  @Output() onUserRemoved = new EventEmitter<UserAssignableDto>();

  @Input()
  ownerId?: number;

  @Input()
  isViewMode = false;

  @Input()
  ownerType?: "group" | "role";

  @ViewChild(MatPaginator) currentPaginator!: MatPaginator;

  constructor(
    public contactHelper: ContactHelper,
    protected override helper: UserAssignableHelper) {

    super(helper);

  }

  ngAfterViewInit() {
    this.paginator = this.currentPaginator;
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

  override loadData(): void {    
    this.searchCriteria.variables = { ownerId: this.ownerId, ownerType: this.ownerType };
    super.loadData();
  }

  isDeleted(rp: UserAssignableDto): boolean {
    return rp.retired === true;
  }

  isCreated(rp: UserAssignableDto): boolean {
    return rp.retired === false;
  }

  /**
   * Call by SharedUserAssignListComponent when a user is unchecked from the assign list.
   * @param user to remove from the role's roleUsers list and deselect from assignUsersTable
   */
  removeUser(ur: UserAssignableDto) {
    if (ur.retired === false) {
      this.onUserRemoved.emit(ur);
    } else {
      ur.retired = !ur.retired;
    }
  }

  override handlePageEvent(e: PageEvent) {
    this.searchCriteria.pageIndex = e.pageIndex;
    this.searchCriteria.pageSize = e.pageSize;
    //this.loadData();
  }

  override setData(data: UserAssignableDto[]): void {
    super.setData(data);
    this.searchResult.searchInfos.total = data.length;
  }
}
