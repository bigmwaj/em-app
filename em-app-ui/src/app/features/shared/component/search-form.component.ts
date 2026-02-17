import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-search-form',
  templateUrl: './search-form.component.html',
  styleUrls: ['./search-form.component.scss'],
  standalone: false
})
export class SearchFormComponent {
  searchText = '';
  @Output() clearSearch = new EventEmitter<void>();
  @Output() initAdvancedSearch = new EventEmitter<void>();
  @Output() search = new EventEmitter<string>();

  onClearSearch(): void {
    this.clearSearch.emit();
  }

  onInitAdvancedASearch(): void {
    this.initAdvancedSearch.emit();
  }

  onSearch(): void {
    this.search.emit(this.searchText);
  }
}
