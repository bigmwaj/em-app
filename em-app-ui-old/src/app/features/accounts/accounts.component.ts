import { Component } from '@angular/core';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.scss']
})
export class AccountsComponent {
  accounts = [
    { id: '1', name: 'Account A', type: 'Business', status: 'Active' },
    { id: '2', name: 'Account B', type: 'Personal', status: 'Active' },
    { id: '3', name: 'Account C', type: 'Business', status: 'Inactive' }
  ];
}
