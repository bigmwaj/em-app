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
