export class SharedHelper {
    static AccountEditMode = {
        CREATE: 'create',
        EDIT: 'edit',
        VIEW: 'view'
    }

}

export class PageData {
    error?: string| null;
    loading: boolean = true;
    message?: string | null;
}
