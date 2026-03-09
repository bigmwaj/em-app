import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { BaseHelper } from "../../shared/base.helper";
import { DeadLetterDto, DeadLetterStatusLvo } from "../api.platform.model";
import { DeadLetterService } from "../service/dead-letter.service";
import { HttpParams } from "@angular/common/http";
import { AbstractSearchCriteria, SearchResult } from "../../shared/api.shared.model";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class DeadLetterHelper extends BaseHelper<DeadLetterDto> {

  constructor(private service: DeadLetterService) {
    super();
  }

  override get baseRoute(): string {
    return '/platform/dead-letters';
  }

  override search(searchCriteria: AbstractSearchCriteria): Observable<SearchResult<DeadLetterDto>> {
    let params = new HttpParams();
    if (searchCriteria) {
      params = this.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    }
    return this.service.getDeadLetters(params);
  }

  override getBackedDto(editMode: string, dto?: DeadLetterDto): Observable<DeadLetterDto> {
    let observable: Observable<DeadLetterDto>;
    if (editMode === this.EditMode.CREATE) {
      throw new Error('Create mode is not supported for DeadLetter.');
    }

    if (!dto || !dto.id) {
      throw new Error('DTO with id is required!');
    } else {
      observable = this.service.getDeadLetter(dto.id)
        .pipe(map((fetchedDto: any) => fetchedDto.data));
    }

    return observable.pipe(
      map((fetchedDto: DeadLetterDto) => {
        switch (editMode) {
          case this.EditMode.CHANGE_STATUS:
            for (let key in fetchedDto) {
              if (key !== "id") {
                delete fetchedDto[key as keyof DeadLetterDto];
              }
            }
            if (dto.status !== DeadLetterStatusLvo.RETRY) {
              fetchedDto.status = DeadLetterStatusLvo.RETRY; // The default
            }
            break;
        }
        return fetchedDto;
      })
    );
  }
}