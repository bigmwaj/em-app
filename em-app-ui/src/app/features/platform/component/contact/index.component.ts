import { Component } from '@angular/core';
import { AbstractIndexComponent } from '../../../shared/component/abstract-index.component';
import { ContactDto } from '../../api.platform.model';
import { ContactHelper } from '../../helper/contact.helper';
import { ContactService } from '../../service/contact.service';

@Component({
  selector: 'app-contact-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss'],
  standalone: false
})
export class ContactIndexComponent extends AbstractIndexComponent<ContactDto> {
  displayedColumns: string[] = ['fullName', 'defaultEmail', 'defaultPhone', 'defaultAddress', 'actions'];

  constructor(
    public contactHelper: ContactHelper,
    override helper: ContactHelper,
    private service: ContactService ) {
      
    super(helper);

    this.delete = (dto) => this.service.deleteContact(dto);
  }
}
