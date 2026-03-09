import { Component, Input } from "@angular/core";
import { UserDto, UserSearchCriteria } from "../../../api.platform.model";
import { AbstractIndexComponent } from "../../../../shared/component/abstract-index.component";
import { UserHelper } from "../../../helper/user.helper";
import { ContactHelper } from "../../../helper/contact.helper";

@Component({
  selector: 'app-shared-user-assign-list',
  templateUrl: './assign.list.component.html',
  styleUrls: ['./assign.list.component.scss'],
  standalone: false
})
export class SharedUserAssignListComponent extends AbstractIndexComponent<UserDto> {

  displayedColumns: string[] = ['select', 'fullName', 'username', 'defaultEmail', 'defaultPhone', 'defaultAddress'];

  @Input()
  ownerId?: number;

  @Input()
  ownerType?: "group" | "role";

  constructor(
    public contactHelper: ContactHelper,
    protected override helper: UserHelper) {
    super(helper);
    this.searchCriteria = this.helper.createUserSearchCriteria() 
  }

  override getKeyLabel(dto: UserDto): string | number {
    return dto.id || '';
  }

  override equals(dto1: UserDto, dto2: UserDto): boolean {
    return dto1 === dto2 || (dto1.id === dto2.id);
  }

  protected override loadData(): void {
    const sc = this.searchCriteria as UserSearchCriteria;
    if (this.ownerType === "group") {
      sc.assignableToGroupId = this.ownerId;
    } else if (this.ownerType === "role") {
      sc.assignableToRoleId = this.ownerId;
    }
    super.loadData();
  }
}
