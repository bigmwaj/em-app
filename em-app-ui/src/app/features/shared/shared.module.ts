import { HttpParams } from "@angular/common/http";
import { AbstractSearchCriteria, DefaultSearchCriteria, SortType, WhereClauseJoinOp } from "./api.shared.model";
import { signal } from "@angular/core";

export class SharedHelper {

    static EditMode = {
        CREATE: 'create',
        EDIT: 'edit',
        VIEW: 'view'
    }

    static createDefaultSearchCriteria(): DefaultSearchCriteria {
        return {
            whereClauses: [],
            sortByClauses: [],
            pageSize: 5,
            pageIndex: 0,
            calculateStatTotal: true
        };
    }

    static mapDefaultSearchCriteriaToHttpParams(sc: DefaultSearchCriteria): HttpParams {

        let params = new HttpParams();

        // Where clause join operator
        if (sc.whereClauseJoinOp !== undefined) {
            params = params.set('whereClauseJoinOp', sc.whereClauseJoinOp);
        } else {
            params = params.set('whereClauseJoinOp', WhereClauseJoinOp.OR);
        }

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

        // Where Clauses
        if (sc.whereClauses && sc.whereClauses.length > 0) {
            var filters = sc.whereClauses.map(e => e.name + ":" + e.oper + ":" + e.values?.reduce((acc, val) => acc + "," + val))
                .reduce((acc, val) => acc + ";" + val)
            params = params.append('filters', filters);
        }

        // Sorting
        if (sc.sortByClauses && sc.sortByClauses.length > 0) {
            var sorts = sc.sortByClauses.map(e => e.name + ":" + e.type).reduce((acc, val) => acc + ";" + val)
            params = params.append('sortBy', sorts);
        }

        return params;
    }

    static getFieldSortIcon(searchCriteria: AbstractSearchCriteria, fieldName: string): string {
        const sortByClause = searchCriteria.sortByClauses?.find(c => c.name === fieldName);
        if (sortByClause) {
            return sortByClause.type === SortType.ASC ? 'arrow_upward' : 'arrow_downward';
        }
        return 'sort';
    }

    static setSortBy(searchCriteria: AbstractSearchCriteria, fieldName: string): void {
        if (!searchCriteria.sortByClauses) {
            searchCriteria.sortByClauses = [];
        }
        const existingClause = searchCriteria.sortByClauses.find(c => c.name === fieldName);
        if (existingClause) {
            existingClause.type = existingClause.type === SortType.ASC ? SortType.DESC : SortType.ASC;
        } else {
            searchCriteria.sortByClauses.push({ name: fieldName, type: SortType.ASC });
        }
    }
}

export class PageData {
    constructor(loading:boolean = false)    {
        this.loading.set(loading);
    }
    error = signal<string | null>(null);
    loading = signal(false);
    message = signal<string | null>(null);
    loadingMessage = signal<string>('Loading...');
}
