import { Component, Input } from '@angular/core';
import { PageData } from '../shared.helper';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss'],
  standalone: false
})
export class MessageComponent {
  @Input()
  pageData!: PageData;
}
