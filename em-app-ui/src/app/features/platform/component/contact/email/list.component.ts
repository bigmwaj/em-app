import { Component, Input, OnDestroy, OnInit } from "@angular/core";
import { ContactDto, ContactEmailDto } from "../../../api.platform.model";
import { CommonDataSource } from "../../../../shared/common.datasource";
import { Subject } from "rxjs";

@Component({
  selector: 'app-email-list',
  templateUrl: './list.component.html',
  standalone: false
})
export class EmailListComponent extends CommonDataSource<ContactEmailDto> implements OnInit, OnDestroy {
  displayedColumns: string[] = ['email', 'type', 'actions'];
  private destroy$ = new Subject<void>();
  @Input()
  contact?: ContactDto;

  constructor(
  ) {
    super();
  }

  override getKeyLabel(bean: ContactEmailDto): string | number {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    if (this.contact?.emails) {
      this.data = this.contact.emails;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
