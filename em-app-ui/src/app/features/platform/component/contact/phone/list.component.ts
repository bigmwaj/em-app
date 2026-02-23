import { Component, Input, OnDestroy, OnInit } from "@angular/core";
import { ContactDto, ContactPhoneDto } from "../../../api.platform.model";
import { CommonDataSource } from "../../../../shared/common.datasource";
import { Subject } from "rxjs";

@Component({
  selector: 'app-phone-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
  standalone: false
})
export class PhoneListComponent extends CommonDataSource<ContactPhoneDto> implements OnInit, OnDestroy {
  displayedColumns: string[] = ['phone', 'type', 'actions'];
  private destroy$ = new Subject<void>();
  @Input()
  contact?: ContactDto;

  override getKeyLabel(bean: ContactPhoneDto): string | number {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    if(this.contact?.phones) {
      this.data = this.contact.phones;
    }
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
