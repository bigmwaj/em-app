package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.Constants;
import ca.bigmwaj.emapp.as.api.shared.Message;
import ca.bigmwaj.emapp.as.api.shared.ResponseMessage;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByClause;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClauseJoinOp;
import ca.bigmwaj.emapp.as.service.platform.AccountService;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import ca.bigmwaj.emapp.as.validator.shared.SortByClauseSupportedField;
import ca.bigmwaj.emapp.as.validator.shared.ValidSortByClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.ValidWhereClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.WhereClauseSupportedField;
import ca.bigmwaj.emapp.as.lvo.platform.AccountStatusLvo;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "account", description = "The Account API")
@RestController
@RequestMapping("/api/v1/platform/accounts")
public class AccountController extends AbstractBaseAPI {

    @Autowired
    private AccountService service;

    @Operation(
            summary = "Search Accounts by criteria",
            externalDocs = @ExternalDocumentation(
                    description = "Full documentation",
                    url = "https://github.com/bigmwaj/smart-cm-project/tree/main/smart-cm-project-admin/README.md"
            ),
            description = "Must of the time, you must encode in base64 and pass it as query param with name q",
            tags = {"account"}
    )
    @GetMapping
    public ResponseEntity<SearchResultDto<AccountDto>> search(
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

            @DefaultValue("true")
            @Parameter(description = "Include the main contact in the response")
            @RequestParam(value = "includeMainContact", required = false)
            boolean includeMainContact,

            @Parameter(description = "Include the contact roles in the response")
            @RequestParam(value = "includeContactRoles", required = false)
            boolean includeContactRoles,

            @ValidWhereClausePatterns(
                    supportedFields = {
                            @WhereClauseSupportedField(name = "id", type = Long.class),
                            @WhereClauseSupportedField(name = "name", type = String.class),
                            @WhereClauseSupportedField(name = "status", type = AccountStatusLvo.class),
                            @WhereClauseSupportedField(name = "firstName", type = String.class, rootEntityName = "c"),
                            @WhereClauseSupportedField(name = "lastName", type = String.class, rootEntityName = "c"),
                            @WhereClauseSupportedField(name = "phone", type = String.class, rootEntityName = "cp"),
                            @WhereClauseSupportedField(name = "email", type = String.class, rootEntityName = "ce"),
                            @WhereClauseSupportedField(name = "address", type = String.class, rootEntityName = "ca"),

                    })
            @Parameter(description = "Filter results based on the following supported filter fields." +
                    "<ul>" +
                    "<li><b>id</b></li>" +
                    "<li><b>name</b></li>" +
                    "<li><b>status</b></li>" +
                    "<li><b>firstName</b></li>" +
                    "<li><b>lastName</b></li>" +
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
                            @SortByClauseSupportedField(name = "status"),
                            @SortByClauseSupportedField(name = "name"),
                            @SortByClauseSupportedField(name = "firstName", rootEntityName = "c"),
                            @SortByClauseSupportedField(name = "lastName", rootEntityName = "c"),
                            @SortByClauseSupportedField(name = "phone", rootEntityName = "cp"),
                            @SortByClauseSupportedField(name = "email", rootEntityName = "ce"),
                            @SortByClauseSupportedField(name = "address", rootEntityName = "ca"),
                    })
            @RequestParam(value = "sortBy", required = false)
            List<SortByClause> sortByClauses) {

        var builder = AccountSearchCriteria.builder()
                .withCalculateStatTotal(calculateStatTotal)
                .withPageSize(pageSize)
                .withPageIndex(pageIndex)
                .withWhereClauseJoinOp(whereClauseJoinOp)
                .withWhereClauses(whereClauses)
                .withSortByClauses(sortByClauses)
                .withIncludeAccountContacts(includeContactRoles)
                .withIncludeMainContact(includeMainContact);
        return ResponseEntity.ok(service.search(builder.build()));
    }

    @Operation(
            summary = "Get account by ID",
            description = "",
            tags = {"account"}
    )
    @GetMapping("/{accountId}")
    public ResponseEntity<ResponseMessage<AccountDto>> findById(
            @Parameter(description = "The account's ID", required = true)
            @Positive @PathVariable Short accountId) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(accountId)));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<AccountDto>> create(
            @Parameter(description = "The account's payload", required = true)
            @RequestBody @Valid AccountDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @PatchMapping
    public ResponseEntity<ResponseMessage<AccountDto>> update(
            @Parameter(description = "The account's payload", required = true)
            @RequestBody @Valid AccountDto dto) {
        switch (dto.getEditAction()) {
            case UPDATE:
                dto = service.update(dto);
                break;
            case CHANGE_STATUS:
                dto = service.changeStatus(dto);
                break;
            default:
                throw new IllegalArgumentException("Unsupported edit action: " + dto.getEditAction());
        }
        return ResponseEntity.ok(new ResponseMessage<>(dto));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Message> delete(
            @Parameter(description = "The account's ID", required = true)
            @Positive @PathVariable Short accountId) {
        service.deleteById(accountId);
        return ResponseEntity.ok(_success(MessageConstants.MSG0013));
    }
}
