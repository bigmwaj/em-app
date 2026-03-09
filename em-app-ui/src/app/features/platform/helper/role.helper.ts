import { map, Observable, of } from "rxjs";
import { BaseHelper } from "../../shared/base.helper";
import { RoleDto, RoleSearchCriteria } from "../api.platform.model";
import { RoleService } from "../service/role.service";
import { AbstractSearchCriteria, SearchResult } from "../../shared/api.shared.model";
import { HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class RoleHelper extends BaseHelper<RoleDto> {

  constructor(private service: RoleService) {
    super();
  }

  override get baseRoute(): string {
    return '/platform/roles';
  }

  override search(searchCriteria: AbstractSearchCriteria): Observable<SearchResult<RoleDto>> {
    let params = new HttpParams();
    if (searchCriteria) {
      params = this.mapRoleSearchCriteriaToHttpParams(searchCriteria);
    }
    return this.service.getRoles(params);
  }

  override getBackedDto(editMode: string, dto?: RoleDto): Observable<RoleDto> {
    let observable: Observable<RoleDto>;
    if (editMode === this.EditMode.CREATE && (!dto || !dto.id)) { // create from scratch
      observable = of({} as RoleDto);
    } else if (!dto || !dto.id) {
      throw new Error('DTO with id is required!');
    } else {
      observable = this.service.getRole(dto.id)
        .pipe(map((fetchedDto: any) => fetchedDto.data));
    }
    return observable.pipe(
      map((fetchedDto: RoleDto) => {
        switch (editMode) {
          case "create":
            delete fetchedDto.id;
            fetchedDto.New = true;
            break;
        }
        return fetchedDto;
      })
    );
  }

  buildFormData(role?: RoleDto): RoleDto {
    if (!role) {
      role = {} as RoleDto;
    }
    // Create a deep copy of the role
    const duplicatedRole: RoleDto = JSON.parse(JSON.stringify(role));

    // Clear identifier fields
    delete duplicatedRole.id;
    return duplicatedRole;
  }

  createRoleSearchCriteria(): RoleSearchCriteria {
    return {
      ...super.createDefaultSearchCriteria(),
      assignableToGroupId: undefined
    };
  }

  mapRoleSearchCriteriaToHttpParams(searchCriteria: RoleSearchCriteria): HttpParams {
    let params = super.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
  
    if (searchCriteria.assignableToGroupId !== undefined) {
      params = params.set('assignableToGroupId', searchCriteria.assignableToGroupId.toString());
    }
    return params;
  }
}