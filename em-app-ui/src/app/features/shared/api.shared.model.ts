import { HttpParams } from "@angular/common/http";

export enum WhereClauseJoinOp {
  AND = 'AND',
  OR = 'OR'
}

export enum EditActionLvo {
  NONE = 'NONE',
  CREATE = 'CREATE',
  UPDATE = 'UPDATE',
  DELETE = 'DELETE',
  CHANGE_STATUS = 'CHANGE_STATUS'
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
export interface AbstractBaseDto {
  key?: any;
  editAction?: EditActionLvo;
}

export interface AbstractChangeTrackingDto extends AbstractBaseDto {
  createdBy?: String;
  createdDate?: Date;
  updatedBy?: String;
  updatedDate?: Date;
}

export interface AbstractStatusTrackingDto<S> extends AbstractChangeTrackingDto {
  status?: S;
  statusDate?: Date;
  statusReason?: string;
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
  whereClauseJoinOp?: WhereClauseJoinOp;
  whereClauses?: WhereClause[];
  sortByClauses?: SortByClause[];
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
