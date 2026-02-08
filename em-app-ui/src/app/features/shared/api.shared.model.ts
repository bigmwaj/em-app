import { HttpParams } from "@angular/common/http";

export enum EditActionLvo {
  NONE = 'NONE',
  CREATE = 'CREATE',
  UPDATE = 'UPDATE',
  DELETE = 'DELETE'
}

export enum FilterOperator {
  EQ = 'eq',
  NE = 'ne',
  IN = 'in',
  NI = 'ni',
  BTW = 'btw',
  LT = 'lt',
  LTE = 'lte',
  GT = 'gt',
  GTE = 'gte',
  LIKE = 'like'
}

export enum SortType {
  ASC = 'asc',
  DESC = 'desc'
}

// Base DTOs
export interface BaseDto {

}

export interface BaseHistDto extends BaseDto {
  createdBy: String;
  createdDate: Date;
  updatedBy: String;
  updatedDate?: Date;
}

export interface AbstractClauseBy {
  name: string;
}

export interface FilterBy extends AbstractClauseBy {
  oper: FilterOperator;
  values?: any[];
}

export interface SortBy extends AbstractClauseBy {
  type: SortType;
}

export interface AbstractFilterDto {
  filterByItems?: FilterBy[];
  sortByItems?: SortBy[];
  pageSize?: number;
  pageIndex?: number;
  calculateStatTotal?: boolean;
}

export interface DefaultFilterDto extends AbstractFilterDto {
}

export interface SearchInfos {
  calculateStatTotal: boolean,
  limit: number;
  offset: number;
  pageIndex: number;
  pageSize: number;
  total: number;
}

export interface SearchResult<T> {
  data: T[];
  searchInfos: SearchInfos
}

export function createDefaultFilterDto(): DefaultFilterDto {
  return {
    filterByItems: [],
    sortByItems: [],
    pageSize: 20,
    pageIndex: 0,
    calculateStatTotal: true
  };
}

export function mapDefaultFilterDtoToHttpParams(filter: DefaultFilterDto): HttpParams {

  let params = new HttpParams();

  // Pagination parameters
  if (filter.pageSize !== undefined && filter.pageSize !== null) {
    params = params.set('pageSize', filter.pageSize.toString());
  }
  if (filter.pageIndex !== undefined && filter.pageIndex !== null) {
    params = params.set('pageIndex', filter.pageIndex.toString());
  }
  if (filter.calculateStatTotal !== undefined) {
    params = params.set('calculateStatTotal', filter.calculateStatTotal.toString());
  } else {
    params = params.set('calculateStatTotal', 'true');
  }           

  // Filters
  if (filter.filterByItems && filter.filterByItems.length > 0) {
    filter.filterByItems.forEach(filterBy => {
      params = params.append('filters', JSON.stringify(filterBy));
    });
  }

  // Sorting
  if (filter.sortByItems && filter.sortByItems.length > 0) {
    filter.sortByItems.forEach(sortBy => {
      params = params.append('sortBy', JSON.stringify(sortBy));
    });
  }

  return params;
} 