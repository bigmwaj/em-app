package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.Message;
import ca.bigmwaj.emapp.as.api.shared.ResponseMessage;
import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.DataListDto;
import ca.bigmwaj.emapp.as.service.platform.ContactService;
import ca.bigmwaj.emapp.as.validator.shared.ValidDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ca.bigmwaj.emapp.as.validator.shared.ValidDto.*;

@Tag(name = "Contacts API", description = "The Contact API")
@RestController
@RequestMapping("/api/v1/platform/contacts")
public class ContactController extends AbstractBaseAPI {

    private static final String NAMESPACE = "platform/contact";

    private final ContactService service;

    @Autowired
    public ContactController(ContactService service) {
        this.service = service;
    }

    @Operation(description = "Search Contacts by criteria")
    @GetMapping
    public ResponseEntity<DataListDto<ContactDto>> search(
            @Valid @ParameterObject ContactSearchCriteria sr) {
        return ResponseEntity.ok(service.search(sr));
    }

    @Operation(description = "Get contact by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<ContactDto>> findById(
            @Parameter(description = "The contact's ID", required = true)
            @Positive @PathVariable Long id) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(id)));
    }

    @Operation(description = "Create a new contact")
    @PostMapping
    public ResponseEntity<ResponseMessage<ContactDto>> create(
            @Parameter(description = "The contact's payload", required = true)
            @RequestBody @ValidDto(value = NAMESPACE, operation = CREATE) ContactDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @Operation(description = "Update an existing contact")
    @PatchMapping
    public ResponseEntity<ResponseMessage<ContactDto>> update(
            @Parameter(description = "The contact's payload", required = true)
            @RequestBody @ValidDto(value = NAMESPACE, operation = UPDATE) ContactDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.update(dto)));
    }

    @Operation(description = "Delete a contact by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> delete(
            @ParameterObject @ValidDto(value = NAMESPACE, operation = DELETE) ContactDto dto) {
        service.deleteById(dto.getId());
        return ResponseEntity.ok(_success("Contact supprimé avec succès"));
    }
}
