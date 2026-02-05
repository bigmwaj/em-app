package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.*;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountFilterDto;
import ca.bigmwaj.emapp.as.dto.shared.search.FilterItem;
import ca.bigmwaj.emapp.as.service.platform.AccountService;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountStatusLvo;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
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

            @ValidFilterPatterns(
                    supportedFields = {
                            @FilterSupportedField(name = "id", type = Long.class),
                            @FilterSupportedField(name = "name", type = String.class),
                            @FilterSupportedField(name = "status", type = AccountStatusLvo.class),
                            @FilterSupportedField(name = "firstName", type = String.class, rootEntityName = "c"),
                            @FilterSupportedField(name = "lastName", type = String.class, rootEntityName = "c"),
                            @FilterSupportedField(name = "phone", type = String.class, rootEntityName = "cp"),
                            @FilterSupportedField(name = "email", type = String.class, rootEntityName = "ce"),
                            @FilterSupportedField(name = "address", type = String.class, rootEntityName = "ca"),
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
            List<FilterItem> filterItems) {

        var builder = AccountFilterDto.builder()
                .withCalculateStatTotal(calculateStatTotal)
                .withPageSize(pageSize)
                .withPageIndex(pageIndex)
                .withFilterItems(filterItems);
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
