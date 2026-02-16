import { AbstractSearchCriteria, SortType } from "./api.shared.model";

export class SharedHelper {
    static AccountEditMode = {
        CREATE: 'create',
        EDIT: 'edit',
        VIEW: 'view'
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
    error?: string | null;
    loading: boolean = true;
    message?: string | null;
}
