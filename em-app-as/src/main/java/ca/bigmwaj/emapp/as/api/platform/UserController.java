package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.Message;
import ca.bigmwaj.emapp.as.api.shared.ResponseMessage;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.dto.platform.UserSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.DataListDto;
import ca.bigmwaj.emapp.as.service.platform.UserService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static ca.bigmwaj.emapp.as.validator.shared.ValidDto.*;

@Tag(name = "Users API", description = "The User API")
@RestController
@RequestMapping("/api/v1/platform/users")
@Validated
public class UserController extends AbstractBaseAPI {

    private static final String NAMESPACE = "platform/user";

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(description = "Search Users by criteria")
    @GetMapping
    public ResponseEntity<DataListDto<UserDto>> search(
            @Valid @ParameterObject UserSearchCriteria sr) {
        return ResponseEntity.ok(service.search(sr));
    }

    @Operation(description = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<UserDto>> findById(
            @Parameter(description = "The user's ID", required = true)
            @Positive @PathVariable Short id) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(id)));
    }

    @Operation(description = "Create a new user")
    @PostMapping
    public ResponseEntity<ResponseMessage<UserDto>> create(
            @Parameter(description = "The user's payload", required = true)
            @RequestBody @ValidDto(value = NAMESPACE, operation = CREATE) UserDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @Operation(description = "Update an existing user")
    @PatchMapping
    public ResponseEntity<ResponseMessage<UserDto>> update(
            @Parameter(description = "The user's payload", required = true)
            @RequestBody @ValidDto(value = NAMESPACE, operation = UPDATE) UserDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.update(dto)));
    }

    @Operation(description = "Delete a user by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> delete(@ParameterObject @ValidDto(value = NAMESPACE, operation = DELETE) UserDto dto) {
        service.deleteById(dto.getId());
        return ResponseEntity.ok(_success(MessageConstants.MSG0012));
    }

    @Operation(description = "Change the status of a user")
    @PostMapping("/{id}/change-status/{status}")
    public ResponseEntity<ResponseMessage<UserDto>> changeStatus(
            @ParameterObject @ValidDto(value = NAMESPACE, operation = CHANGE_STATUS) UserDto dto) {
        service.deleteById(dto.getId());
        return ResponseEntity.ok(new ResponseMessage<>(service.changeStatus(dto)));
    }

    @Operation(description = "Change the password of a user")
    @PostMapping("/{id}/change-password")
    public ResponseEntity<ResponseMessage<UserDto>> changePassword(
            @ParameterObject @ValidDto(value = NAMESPACE, operation = "change-password") UserDto dto) {
        service.deleteById(dto.getId());
        return ResponseEntity.ok(new ResponseMessage<>(service.changePassword(dto)));
    }
}
