import { Component, EventEmitter, Input, Output } from '@angular/core';

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

  private _showAdvancedSearchButton = false;

  get showAdvancedSearchButton(): boolean {
    return this._showAdvancedSearchButton;
  }

  @Input()
  set showAdvancedSearchButton(value: boolean) {
    this._showAdvancedSearchButton = value;
  }

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
