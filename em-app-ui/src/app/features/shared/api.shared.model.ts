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

export interface AbstractSearchCriteria {
  filterByItems?: FilterBy[];
  sortByItems?: SortBy[];
  pageSize?: number;
  pageIndex?: number;
  calculateStatTotal?: boolean;
}

export interface DefaultSearchCriteria extends AbstractSearchCriteria {
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

export function createDefaultSearchCriteria(): DefaultSearchCriteria {
  return {
    filterByItems: [],
    sortByItems: [],
    pageSize: 20,
    pageIndex: 0,
    calculateStatTotal: true
  };
}

export function mapDefaultSearchCriteriaToHttpParams(searchCriteria: DefaultSearchCriteria): HttpParams {

  let params = new HttpParams();

  // Pagination parameters
  if (searchCriteria.pageSize !== undefined && searchCriteria.pageSize !== null) {
    params = params.set('pageSize', searchCriteria.pageSize.toString());
  }
  if (searchCriteria.pageIndex !== undefined && searchCriteria.pageIndex !== null) {
    params = params.set('pageIndex', searchCriteria.pageIndex.toString());
  }
  if (searchCriteria.calculateStatTotal !== undefined) {
    params = params.set('calculateStatTotal', searchCriteria.calculateStatTotal.toString());
  } else {
    params = params.set('calculateStatTotal', 'true');
  }           

  // Filters
  if (searchCriteria.filterByItems && searchCriteria.filterByItems.length > 0) {
    searchCriteria.filterByItems.forEach(filterBy => {
      params = params.append('filters', JSON.stringify(filterBy));
    });
  }

  // Sorting
  if (searchCriteria.sortByItems && searchCriteria.sortByItems.length > 0) {
    searchCriteria.sortByItems.forEach(sortBy => {
      params = params.append('sortBy', JSON.stringify(sortBy));
    });
  }

  return params;
} 