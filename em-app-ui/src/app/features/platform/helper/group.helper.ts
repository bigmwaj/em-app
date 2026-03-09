import { HttpParams } from "@angular/common/http";
import { AbstractSearchCriteria, SearchResult } from "../../shared/api.shared.model";
import { BaseHelper } from "../../shared/base.helper";
import { GroupDto } from "../api.platform.model";
import { GroupService } from "../service/group.service";
import { map, Observable, of } from "rxjs";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class GroupHelper extends BaseHelper<GroupDto> {

  constructor(private service: GroupService) {
    super();
  }

  override get baseRoute(): string {
    return '/platform/groups';
  }

  override search(searchCriteria: AbstractSearchCriteria): Observable<SearchResult<GroupDto>> {
    let params = new HttpParams();
    if (searchCriteria) {
      params = this.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    }
    return this.service.getGroups(params);
  }

  override getBackedDto(editMode: string, dto?: GroupDto): Observable<GroupDto> {
    var observable: Observable<GroupDto>;
    if (editMode === this.EditMode.CREATE && (!dto || !dto.id)) { // create from scratch
      observable = of({} as GroupDto);
    } else if (!dto || !dto.id) {
      throw new Error('DTO with id is required!');
    } else {
      observable = this.service.getGroup(dto.id)
      .pipe(map((fetchedDto: any) => fetchedDto.data));
    }
    return observable.pipe(
      map((fetchedDto: GroupDto) => {
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

  buildFormData(group?: GroupDto): GroupDto {
    if (!group) {
      group = {} as GroupDto;
    }
    // Create a deep copy of the group
    const duplicatedGroup: GroupDto = JSON.parse(JSON.stringify(group));

    // Clear identifier fields
    delete duplicatedGroup.id;
    return duplicatedGroup;
  }
}