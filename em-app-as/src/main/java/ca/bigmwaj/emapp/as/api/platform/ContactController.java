package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.*;
import ca.bigmwaj.emapp.as.api.shared.search.FilterBySupportedField;
import ca.bigmwaj.emapp.as.api.shared.search.ValidFilterByPatterns;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactFilterDto;
import ca.bigmwaj.emapp.as.dto.shared.search.FilterBy;
import ca.bigmwaj.emapp.as.service.platform.ContactService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "contact", description = "The Contact API")
@RestController
@RequestMapping("/api/v1/platform/contact")
public class ContactController extends AbstractBaseAPI {

    @Autowired
    private ContactService service;
    @Operation(
            summary = "Search Contacts by criteria",
            externalDocs = @ExternalDocumentation(
                    description = "Full documentation",
                    url = "https://github.com/bigmwaj/smart-cm-project/tree/main/smart-cm-project-admin/README.md"
            ),
            description = "Must of the time, you must encode in base64 and pass it as query param with name q",
            tags = {"contact"}
    )
    @GetMapping
    public ResponseEntity<SearchResultDto<ContactDto>> search(
            @Positive
            @Parameter(description = "The page total to send")
            @RequestParam(value = "pageSize", required = false)
            Short pageSize,

            @Positive
            @Parameter(description = "The page index for filtering")
            @RequestParam(value = "pageIndex", required = false)
            Integer pageIndex,

            @Parameter(description = "Calculate the total corresponding to filters")
            @RequestParam(value = "calculateStatTotal", required = false)
            boolean calculateStatTotal,

            @ValidFilterByPatterns(
                    supportedFields = {
                            @FilterBySupportedField(name = "id", type = Long.class),
                            @FilterBySupportedField(name = "firstName", type = String.class),
                            @FilterBySupportedField(name = "lastName", type = String.class),
                            @FilterBySupportedField(name = "birthDate", type = LocalDate.class),
                            @FilterBySupportedField(name = "phone", type = String.class, rootEntityName = "cp"),
                            @FilterBySupportedField(name = "email", type = String.class, rootEntityName = "ce"),
                            @FilterBySupportedField(name = "address", type = String.class, rootEntityName = "ca"),
                    })
            @Parameter(description = "Filter results based on the following supported filter fields." +
                    "<ul>" +
                    "<li><b>id</b></li>" +
                    "<li><b>firstName</b></li>" +
                    "<li><b>lastName</b></li>" +
                    "<li><b>birthDate</b></li>" +
                    "<li><b>phone</b></li>" +
                    "<li><b>email</b></li>" +
                    "<li><b>address</b></li>" +
                    "</ul>" +
                    Constants.FILTER_DOC)
            @RequestParam(value = "filters", required = false)
            List<FilterBy> filterBIES) {

        var builder = ContactFilterDto.builder()
                .withCalculateStatTotal(calculateStatTotal)
                .withPageSize(pageSize)
                .withPageIndex(pageIndex)
                .withFilterBIES(filterBIES);
        return ResponseEntity.ok(service.search(builder.build()));
    }

    @Operation(
            summary = "Get contact by ID",
            description = "",
            tags = {"contact"}
    )
    @GetMapping("/contact-id/{contactId}")
    public ResponseEntity<ResponseMessage<ContactDto>> findById(
            @Parameter(description = "The contact's ID", required = true)
            @Positive @PathVariable Long contactId) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(contactId)));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<ContactDto>> create(
            @Parameter(description = "The contact's payload", required = true)
            @RequestBody @Validated ContactDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @PatchMapping
    public ResponseEntity<ResponseMessage<ContactDto>> update(
            @Parameter(description = "The contact's payload", required = true)
            @RequestBody @Validated ContactDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.update(dto)));
    }

    @DeleteMapping("/contact-id/{contactId}")
    public ResponseEntity<Message> delete(
            @Parameter(description = "The contact's ID", required = true)
            @Positive @PathVariable Long contactId) {
        service.deleteById(contactId);
        return ResponseEntity.ok(_success("Contact supprimé avec succès"));
    }
}
