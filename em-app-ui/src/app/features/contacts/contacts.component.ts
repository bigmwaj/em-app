import { Component } from '@angular/core';

@Component({
  selector: 'app-contacts',
  templateUrl: './contacts.component.html',
  styleUrls: ['./contacts.component.scss']
})
export class ContactsComponent {
  contacts = [
    { id: '1', name: 'John Doe', email: 'john@example.com', phone: '555-1234' },
    { id: '2', name: 'Jane Smith', email: 'jane@example.com', phone: '555-5678' },
    { id: '3', name: 'Bob Johnson', email: 'bob@example.com', phone: '555-9012' }
  ];
}
