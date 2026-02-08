package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.*;
import ca.bigmwaj.emapp.as.api.shared.search.FilterBySupportedField;
import ca.bigmwaj.emapp.as.api.shared.search.SortBySupportedField;
import ca.bigmwaj.emapp.as.api.shared.search.ValidFilterByPatterns;
import ca.bigmwaj.emapp.as.api.shared.search.ValidSortByPatterns;
import ca.bigmwaj.emapp.as.dto.platform.AccountFilterDto;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.common.DefaultFilterDto;
import ca.bigmwaj.emapp.as.dto.shared.search.FilterBy;
import ca.bigmwaj.emapp.as.dto.shared.search.SortBy;
import ca.bigmwaj.emapp.as.service.platform.AccountService;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountStatusLvo;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "account", description = "The Account API")
@RestController
@RequestMapping("/api/v1/platform/account")
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

            @Positive
            @Parameter(description = "The page index for filtering")
            @RequestParam(value = "pageIndex", required = false)
            Integer pageIndex,

            @Parameter(description = "Calculate the total corresponding to filters")
            @RequestParam(value = "calculateStatTotal", required = false)
            boolean calculateStatTotal,

            @DefaultValue("true")
            @Parameter(description = "Include the main contact in the response")
            @RequestParam(value = "includeMainContact", required = false)
            boolean includeMainContact,

            @Parameter(description = "Include the contact roles in the response")
            @RequestParam(value = "includeContactRoles", required = false)
            boolean includeContactRoles,

            @ValidFilterByPatterns(
                    supportedFields = {
                            @FilterBySupportedField(name = "id", type = Long.class),
                            @FilterBySupportedField(name = "name", type = String.class),
                            @FilterBySupportedField(name = "status", type = AccountStatusLvo.class)
                    })
            @Parameter(description = "Filter results based on the following supported filter fields." +
                    "<ul>" +
                    "<li><b>id</b></li>" +
                    "<li><b>name</b></li>" +
                    "<li><b>status</b></li>" +
                    "</ul>" +
                    Constants.FILTER_DOC)
            @RequestParam(value = "filters", required = false)
            List<FilterBy> filterByItems,

            @ValidSortByPatterns(
                    supportedFields = {
                            @SortBySupportedField(name = "status"),
                            @SortBySupportedField(name = "name"),
                            @SortBySupportedField(name = "status"),
                    })
            @RequestParam(value = "sortBy", required = false)
            List<SortBy> sortByItems) {

        var builder = AccountFilterDto.builder()
                .withCalculateStatTotal(calculateStatTotal)
                .withPageSize(pageSize)
                .withPageIndex(pageIndex)
                .withFilterByItems(filterByItems)
                .withIncludeContactRoles(includeContactRoles)
                .withIncludeMainContact(includeMainContact);
        return ResponseEntity.ok(service.search(builder.build()));
    }

    @Operation(
            summary = "Get account by ID",
            description = "",
            tags = {"account"}
    )
    @GetMapping("/account-id/{accountId}")
    public ResponseEntity<ResponseMessage<AccountDto>> findById(
            @Parameter(description = "The account's ID", required = true)
            @Positive @PathVariable Long accountId) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(accountId)));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<AccountDto>> create(
            @Parameter(description = "The account's payload", required = true)
            @RequestBody @Validated AccountDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @PatchMapping
    public ResponseEntity<ResponseMessage<AccountDto>> update(
            @Parameter(description = "The account's payload", required = true)
            @RequestBody @Validated AccountDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.update(dto)));
    }

    @DeleteMapping("/account-id/{accountId}")
    public ResponseEntity<Message> delete(
            @Parameter(description = "The account's ID", required = true)
            @Positive @PathVariable Long accountId) {
        service.deleteById(accountId);
        return ResponseEntity.ok(_success(MessageConstants.MSG0013));
    }
}
