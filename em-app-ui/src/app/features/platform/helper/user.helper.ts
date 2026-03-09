import { HttpParams } from "@angular/common/http";
import { BaseHelper } from "../../shared/base.helper";
import { UserDto, UserSearchCriteria } from "../api.platform.model";
import { ContactHelper } from "./contact.helper";
import { map, Observable, of } from "rxjs";
import { UserService } from "../service/user.service";
import { AbstractSearchCriteria, SearchResult } from "../../shared/api.shared.model";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class UserHelper extends BaseHelper<UserDto> {

  constructor(private service: UserService, private contactHelper: ContactHelper) {
    super();
  }

  override get baseRoute(): string {
    return '/platform/users';
  }

  override search(searchCriteria: AbstractSearchCriteria): Observable<SearchResult<UserDto>> {
    let params = new HttpParams();
    if (searchCriteria) {
      params = this.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    }
    return this.service.getUsers(params);
  }

  override getBackedDto(editMode: string, dto?: UserDto): Observable<UserDto> {
    var observable: Observable<UserDto>;
    if (editMode === this.EditMode.CREATE && (!dto || !dto.id)) {
      observable = of({} as UserDto);
    } else if (!dto || !dto.id) {
      throw new Error('DTO with id is required!');
    } else {
      observable = this.service.getUser(dto.id)
        .pipe(map((fetchedDto: any) => fetchedDto.data));
    }
    return observable.pipe(
      map((fetchedDto: UserDto) => {
        switch (editMode) {
          case this.EditMode.CREATE:
            delete fetchedDto.id;
            fetchedDto.New = true;
            break;
            
          case this.EditMode.CHANGE_STATUS:
            for (let key in fetchedDto) {
              if (key !== "id") {
                delete fetchedDto[key as keyof UserDto];
              }
            }
            break;
        }
        return fetchedDto;
      })
    );
  }

  buildFormData(user?: UserDto): UserDto {
    if (!user) {
      user = {} as UserDto;
    }
    // Create a deep copy of the user
    const duplicatedUser: UserDto = JSON.parse(JSON.stringify(user));

    // Clear identifier fields
    delete duplicatedUser.id;

    // Clear IDs from nested contact object
    if (duplicatedUser.contact) {
      delete duplicatedUser.contact.id;

      const defaultEmail = this.contactHelper.getDefaultContactEmail(duplicatedUser.contact);
      if (defaultEmail) {
        delete defaultEmail.id;
      }

      const defaultPhone = this.contactHelper.getDefaultContactPhone(duplicatedUser.contact);
      if (defaultPhone) {
        delete defaultPhone.id;
      }

      const defaultAddress = this.contactHelper.getDefaultContactAddress(duplicatedUser.contact);
      if (defaultAddress) {
        delete defaultAddress.id;
      }
    }

    return duplicatedUser;
  }

  createUserSearchCriteria(): UserSearchCriteria {
    return {
      ...super.createDefaultSearchCriteria(),
      assignableToRoleId: undefined,
      assignableToGroupId: undefined
    };
  }

  mapUserSearchCriteriaToHttpParams(searchCriteria: UserSearchCriteria): HttpParams {
    let params = super.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    if (searchCriteria.assignableToRoleId !== undefined) {
      params = params.set('assignableToRoleId', searchCriteria.assignableToRoleId.toString());
    }
    if (searchCriteria.assignableToGroupId !== undefined) {
      params = params.set('assignableToGroupId', searchCriteria.assignableToGroupId.toString());
    }
    return params;
  }

}