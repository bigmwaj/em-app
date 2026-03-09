package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.Message;
import ca.bigmwaj.emapp.as.api.shared.ResponseMessage;
import ca.bigmwaj.emapp.as.dto.common.DefaultSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.RoleDto;
import ca.bigmwaj.emapp.as.dto.platform.RolePrivilegeDto;
import ca.bigmwaj.emapp.as.dto.platform.RoleSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.RoleUserDto;
import ca.bigmwaj.emapp.as.dto.shared.DataListDto;
import ca.bigmwaj.emapp.as.service.platform.RoleService;
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

@Tag(name = "Roles API", description = "The Role API")
@RestController
@RequestMapping("/api/v1/platform/roles")
@Validated
public class RoleController extends AbstractBaseAPI {

    private static final String NAMESPACE = "platform/role";

    private final RoleService service;

    @Autowired
    public RoleController(RoleService service) {
        this.service = service;
    }

    @Operation(description = "Search Roles by criteria")
    @GetMapping
    public ResponseEntity<DataListDto<RoleDto>> search(
            @Valid @ParameterObject RoleSearchCriteria sr) {
        return ResponseEntity.ok(service.search(sr));
    }

    @Operation(description = "Get role by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<RoleDto>> findById(
            @Parameter(description = "The role's ID", required = true)
            @Positive @PathVariable Short id) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(id)));
    }

    @Operation(description = "Get role privileges by role ID")
    @GetMapping("/{id}/role-privileges")
    public ResponseEntity<DataListDto<RolePrivilegeDto>> getRolePrivileges(
            @Parameter(description = "The role's ID", required = true)
            @Positive @PathVariable Short id,
            @Valid @ParameterObject DefaultSearchCriteria sc) {
        return ResponseEntity.ok(service.findRolePrivileges(id, sc));
    }

    @Operation(description = "Get role users by role ID")
    @GetMapping("/{id}/role-users")
    public ResponseEntity<DataListDto<RoleUserDto>> getUserRoles(
            @Parameter(description = "The role's ID", required = true)
            @Positive @PathVariable Short id,
            @Valid @ParameterObject DefaultSearchCriteria sc) {
        return ResponseEntity.ok(service.findRoleUsers(id, sc));
    }

    @Operation(description = "Create a new role")
    @PostMapping
    public ResponseEntity<ResponseMessage<RoleDto>> create(
            @Parameter(description = "The role's payload", required = true)
            @RequestBody @ValidDto(value = NAMESPACE, operation = CREATE) RoleDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @Operation(description = "Update an existing role")
    @PatchMapping
    public ResponseEntity<ResponseMessage<RoleDto>> update(
            @Parameter(description = "The role's payload", required = true)
            @RequestBody @ValidDto(value = NAMESPACE, operation = UPDATE) RoleDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.update(dto)));
    }

    @Operation(description = "Delete a role by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> delete(
            @ParameterObject @ValidDto(value = NAMESPACE, operation = DELETE) RoleDto dto) {
        service.deleteById(dto.getId());
        return ResponseEntity.ok(_success("Role successfully deleted."));
    }
}
