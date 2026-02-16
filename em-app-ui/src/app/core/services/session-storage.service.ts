import { Injectable } from '@angular/core';
import { AuthUserInfo } from '../model/user.model';

@Injectable({
    providedIn: 'root'
})
export class SessionStorageService {

    private static USER_INFOS_KEY = 'user_info';

    private static TOKEN_KEY = 'token_key';

    private static TOKEN_EXPIRATION_DATE_KEY = 'token_expiration_date_key';

    private static CSRF_TOKEN_KEY = 'csrf_token_key';

    private static SIDENAV_OPENED_KEY = 'sidenav_opened_key';

    private static PLATFORM_MENU_EXPANDED_KEY = 'platform_menu_expanded_key';

    clear(): void {
        window.localStorage.clear()
    }

    private setItem<T>(key: string, value: T, mapper: (t: T) => string) {
        window.localStorage.setItem(key, mapper(value));
    }

    private getItem<T>(key: string, mapper: (t: string) => T): T | null {
        const val = window.localStorage.getItem(key);
        if (val != null) {
            return mapper(val)
        }
        return null;
    }

    set userInfos(value: AuthUserInfo) {
        this.setItem<AuthUserInfo>(SessionStorageService.USER_INFOS_KEY, value, v => JSON.stringify(v))
    }

    get userInfos(): AuthUserInfo | null {
        return this.getItem<AuthUserInfo>(SessionStorageService.USER_INFOS_KEY, v => JSON.parse(v))
    }

    get token(): string | null {
        return this.getItem<string>(SessionStorageService.TOKEN_KEY, v => v)
    }

    set token(value: string) {
        this.setItem<string>(SessionStorageService.TOKEN_KEY, value, v => v)
    }


    get sidenavOpened(): string | null {
        return this.getItem<string>(SessionStorageService.SIDENAV_OPENED_KEY, v => v)
    }

    set sidenavOpened(value: string) {
        this.setItem<string>(SessionStorageService.SIDENAV_OPENED_KEY, value, v => v)
    }

    get platformMenuExpanded(): string | null {
        return this.getItem<string>(SessionStorageService.PLATFORM_MENU_EXPANDED_KEY, v => v)
    }

    set platformMenuExpanded(value: string) {
        this.setItem<string>(SessionStorageService.PLATFORM_MENU_EXPANDED_KEY, value, v => v)
    }

    removeToken() {
        const exists = localStorage.getItem(SessionStorageService.TOKEN_KEY) !== null
        if (exists) {
            window.localStorage.removeItem(SessionStorageService.TOKEN_KEY)
        }
    }

    get csrf(): string | null {
        return this.getItem<string>(SessionStorageService.CSRF_TOKEN_KEY, v => v)
    }

    set csrf(value: string) {
        this.setItem<string>(SessionStorageService.CSRF_TOKEN_KEY, value, v => v)
    }

    set tokenExpirationDate(value: number) {
        const now = new Date().getTime()
        this.setItem<number>(SessionStorageService.TOKEN_EXPIRATION_DATE_KEY, value, v => (v + now).toString())
    }
}
