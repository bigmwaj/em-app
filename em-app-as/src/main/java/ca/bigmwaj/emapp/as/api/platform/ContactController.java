package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.*;
import ca.bigmwaj.emapp.as.validator.shared.WhereClauseSupportedField;
import ca.bigmwaj.emapp.as.validator.shared.SortByClauseSupportedField;
import ca.bigmwaj.emapp.as.validator.shared.ValidWhereClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.ValidSortByClausePatterns;
import ca.bigmwaj.emapp.as.dto.common.DefaultSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByClause;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClauseJoinOp;
import ca.bigmwaj.emapp.as.service.platform.ContactService;
import ca.bigmwaj.emapp.dm.lvo.platform.OwnerTypeLvo;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
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

            @PositiveOrZero
            @Parameter(description = "The page index for filtering")
            @RequestParam(value = "pageIndex", required = false)
            Integer pageIndex,

            @Parameter(description = "Calculate the total corresponding to filters")
            @RequestParam(value = "calculateStatTotal", required = false)
            boolean calculateStatTotal,

            @DefaultValue("and")
            @Parameter(description = "Where clause join operator. Default is and")
            @RequestParam(value = "whereClauseJoinOp", required = false)
            WhereClauseJoinOp whereClauseJoinOp,

            @ValidWhereClausePatterns(
                    supportedFields = {
                            @WhereClauseSupportedField(name = "id", type = Long.class),
                            @WhereClauseSupportedField(name = "ownerType", type = OwnerTypeLvo.class),
                            @WhereClauseSupportedField(name = "firstName", type = String.class),
                            @WhereClauseSupportedField(name = "lastName", type = String.class),
                            @WhereClauseSupportedField(name = "birthDate", type = LocalDate.class),
                            @WhereClauseSupportedField(name = "phone", type = String.class, rootEntityName = "cp"),
                            @WhereClauseSupportedField(name = "email", type = String.class, rootEntityName = "ce"),
                            @WhereClauseSupportedField(name = "address", type = String.class, rootEntityName = "ca"),
                    })
            @Parameter(description = "Filter results based on the following supported filter fields." +
                    "<ul>" +
                    "<li><b>id</b></li>" +
                    "<li><b>ownerType</b></li>" +
                    "<li><b>firstName</b></li>" +
                    "<li><b>lastName</b></li>" +
                    "<li><b>birthDate</b></li>" +
                    "<li><b>phone</b></li>" +
                    "<li><b>email</b></li>" +
                    "<li><b>address</b></li>" +
                    "</ul>" +
                    Constants.FILTER_DOC)
            @RequestParam(value = "filters", required = false)
            List<WhereClause> whereClauses,

            @ValidSortByClausePatterns(
                    supportedFields = {
                            @SortByClauseSupportedField(name = "id"),
                            @SortByClauseSupportedField(name = "ownerType"),
                            @SortByClauseSupportedField(name = "firstName"),
                            @SortByClauseSupportedField(name = "lastName"),
                            @SortByClauseSupportedField(name = "phone", rootEntityName = "cp"),
                            @SortByClauseSupportedField(name = "email", rootEntityName = "ce"),
                            @SortByClauseSupportedField(name = "address", rootEntityName = "ca"),
                    })
            @RequestParam(value = "sortBy", required = false)
            List<SortByClause> sortByClauses) {

        var builder = DefaultSearchCriteria.builder()
                .withCalculateStatTotal(calculateStatTotal)
                .withPageSize(pageSize)
                .withPageIndex(pageIndex)
                .withWhereClauseJoinOp(whereClauseJoinOp)
                .withWhereClauses(whereClauses)
                .withSortByClauses(sortByClauses);
        return ResponseEntity.ok(service.search(builder.build()));
    }

    @Operation(
            summary = "Get contact by ID",
            description = "",
            tags = {"contact"}
    )
    @GetMapping("/{contactId}")
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

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Message> delete(
            @Parameter(description = "The contact's ID", required = true)
            @Positive @PathVariable Long contactId) {
        service.deleteById(contactId);
        return ResponseEntity.ok(_success("Contact supprimé avec succès"));
    }
}
