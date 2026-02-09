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

export interface AbstractClause {
  name: string;
}

export interface WhereClause extends AbstractClause {
  oper: FilterOperator;
  values?: any[];
}

export interface SortByClause extends AbstractClause {
  type: SortType;
}

export interface AbstractSearchCriteria {
  filterByItems?: WhereClause[];
  sortByItems?: SortByClause[];
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

export function mapDefaultSearchCriteriaToHttpParams(sc: DefaultSearchCriteria): HttpParams {

  let params = new HttpParams();

  // Pagination parameters
  if (sc.pageSize !== undefined && sc.pageSize !== null) {
    params = params.set('pageSize', sc.pageSize.toString());
  }
  if (sc.pageIndex !== undefined && sc.pageIndex !== null) {
    params = params.set('pageIndex', sc.pageIndex.toString());
  }
  if (sc.calculateStatTotal !== undefined) {
    params = params.set('calculateStatTotal', sc.calculateStatTotal.toString());
  } else {
    params = params.set('calculateStatTotal', 'true');
  }

  // Filters
  if (sc.filterByItems && sc.filterByItems.length > 0) {
    var filters = sc.filterByItems.map(e =>  e.name + ":" + e.oper + ":" + e.values?.reduce((acc, val) => acc + "," + val))
    .reduce((acc, val) => acc + ";" + val)      
    params = params.append('filters', filters);
  }

  // Sorting
  if (sc.sortByItems && sc.sortByItems.length > 0) {
    var sorts = sc.sortByItems.map(e => e.name + ":" + e.type).reduce((acc, val) => acc + ";" + val)      
    params = params.append('sortBy', sorts);
  }

  return params;
}
