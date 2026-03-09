import { map, Observable } from "rxjs";
import { AbstractSearchCriteria, SearchResult } from "../../shared/api.shared.model";
import { BaseHelper } from "../../shared/base.helper";
import { ContactAddressDto, ContactDto, ContactEmailDto, ContactPhoneDto } from "../api.platform.model";
import { ContactService } from "../service/contact.service";
import { HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class ContactHelper extends BaseHelper<ContactDto> {


  constructor(private service: ContactService) {
    super();
  }

  override get baseRoute(): string {
    return '/platform/contacts';
  }

  override getBackedDto(editMode: string, dto?: ContactDto): Observable<ContactDto> {
    var observable: Observable<ContactDto>;
    if (editMode === this.EditMode.CREATE && (!dto || !dto.id)) { // create from scratch
      observable = new Observable<ContactDto>((observer) => {
        observer.next({} as ContactDto);
        observer.complete();
      }); 
    } else if (!dto || !dto.id) {
      throw new Error('DTO with id is required!');
    } else {
      observable = this.service.getContact(dto.id)
      .pipe(map((fetchedDto: any) => fetchedDto.data));
    }   
    return observable.pipe(
      map((fetchedDto: ContactDto) => {
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
  
  override search(searchCriteria: AbstractSearchCriteria): Observable<SearchResult<ContactDto>> {
    let params = new HttpParams();

    if (searchCriteria) {
      params = this.mapDefaultSearchCriteriaToHttpParams(searchCriteria);
    }
    return this.service.getContacts(params);
  }

  buildFormData(contact?: ContactDto): ContactDto {
    if (!contact) {
      contact = {} as ContactDto;
    }

    // Create a deep copy of the contact
    const duplicatedContact: ContactDto = JSON.parse(JSON.stringify(contact));

    // Clear identifier fields
    delete duplicatedContact.id;

    // Clear IDs from nested objects
    const defaultEmail = this.getDefaultContactEmail(duplicatedContact);
    if (defaultEmail) {
      delete defaultEmail.id;
    }

    const defaultPhone = this.getDefaultContactPhone(duplicatedContact);
    if (defaultPhone) {
      delete defaultPhone.id;
    }

    const defaultAddress = this.getDefaultContactAddress(duplicatedContact);
    if (defaultAddress) {
      delete defaultAddress.id;
    }

    return duplicatedContact;
  }

  getFullName(contact: ContactDto | null): string | null {
    if (!contact) {
      return null;
    }
    return `${contact.firstName || ''} ${contact.lastName || ''}`.trim() || null;
  }

  getDefaultContactEmail(contact: ContactDto | null): ContactEmailDto | null {
    return contact?.emails?.[0] || null;
  }

  getDefaultContactPhone(contact: ContactDto | null): ContactPhoneDto | null {
    return contact?.phones?.[0] || null;
  }

  getDefaultContactAddress(contact: ContactDto | null): ContactAddressDto | null {
    return contact?.addresses?.[0] || null;
  }
}