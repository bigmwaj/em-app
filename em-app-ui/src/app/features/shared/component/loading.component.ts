import { Component, Input } from '@angular/core';
import { PageData } from '../shared.helper';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.scss'],
  standalone: false
})
export class LoadingComponent {
  @Input()
  pageData!: PageData;
}
