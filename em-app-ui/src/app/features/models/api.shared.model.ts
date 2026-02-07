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