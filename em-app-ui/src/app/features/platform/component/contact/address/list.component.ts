import { Component, Input, OnDestroy, OnInit } from "@angular/core";
import { Subject } from "rxjs";
import { CommonDataSource } from "../../../../shared/common.datasource";
import { ContactAddressDto, ContactDto } from "../../../api.platform.model";

@Component({
  selector: 'app-address-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
  standalone: false
})
export class AddressListComponent extends CommonDataSource<ContactAddressDto> implements OnInit, OnDestroy {
  displayedColumns: string[] = ['address', 'type', 'country', 'region', 'city', 'actions'];
  private destroy$ = new Subject<void>();
  @Input()
  contact?: ContactDto;

  override getKeyLabel(bean: ContactAddressDto): string | number {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    if(this.contact?.addresses) {
      this.data = this.contact.addresses;
    }
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
