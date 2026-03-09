package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.Message;
import ca.bigmwaj.emapp.as.api.shared.ResponseMessage;
import ca.bigmwaj.emapp.as.dto.platform.AccountContactDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.DataListDto;
import ca.bigmwaj.emapp.as.service.platform.AccountService;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
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

import java.util.Collections;

import static ca.bigmwaj.emapp.as.validator.shared.ValidDto.*;

@Tag(name = "Accounts API", description = "The Account API")
@RestController
@RequestMapping("/api/v1/platform/accounts")
public class AccountController extends AbstractBaseAPI {

    private static final String NAMESPACE = "platform/account";

    private final AccountService service;

    @Autowired
    public AccountController(AccountService service) {
        this.service = service;
    }

    @Operation(description = "Search Accounts by criteria")
    @GetMapping
    public ResponseEntity<DataListDto<AccountDto>> search(
            @Valid @ParameterObject AccountSearchCriteria sr) {
        return ResponseEntity.ok(service.search(sr));
    }

    @Operation(description = "Get account by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<AccountDto>> findById(
            @Parameter(description = "The account's ID", required = true)
            @Positive @PathVariable Short id) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(id)));
    }

    @Operation(description = "Change the status of the account")
    @PostMapping("/{id}/change-status/{status}")
    public ResponseEntity<ResponseMessage<AccountDto>> changeStatus(
            @ParameterObject @ValidDto(value = NAMESPACE, operation = CHANGE_STATUS) AccountDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.changeStatus(dto)));
    }

    @Operation(description = "Change the status of the account")
    @GetMapping("/{id}/account-contacts")
    public ResponseEntity<DataListDto<AccountContactDto>> getAccountContacts(
            @Valid @ParameterObject AccountDto dto) {
        return ResponseEntity.ok(new DataListDto<>(Collections.emptyList()));
    }

    @Operation(description = "Create a new account")
    @PostMapping
    public ResponseEntity<ResponseMessage<AccountDto>> create(
            @Parameter(description = "The account's payload", required = true)
            @RequestBody @ValidDto(value = NAMESPACE, operation = CREATE) AccountDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @Operation(description = "Update an existing account")
    @PatchMapping
    public ResponseEntity<ResponseMessage<AccountDto>> update(
            @Parameter(description = "The account's payload", required = true)
            @RequestBody @ValidDto(value = NAMESPACE, operation = UPDATE) AccountDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.update(dto)));
    }

    @Operation(description = "Delete an account by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> delete(
            @Parameter(description = "The account's ID", required = true)
            @ParameterObject @ValidDto(value = NAMESPACE, operation = DELETE) AccountDto dto) {
        service.deleteById(dto.getId());
        return ResponseEntity.ok(_success(MessageConstants.MSG0013));
    }
}
