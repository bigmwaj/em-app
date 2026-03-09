package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.Message;
import ca.bigmwaj.emapp.as.api.shared.ResponseMessage;
import ca.bigmwaj.emapp.as.dto.common.DefaultSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.GroupDto;
import ca.bigmwaj.emapp.as.dto.platform.GroupRoleDto;
import ca.bigmwaj.emapp.as.dto.platform.GroupSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.GroupUserDto;
import ca.bigmwaj.emapp.as.dto.shared.DataListDto;
import ca.bigmwaj.emapp.as.service.platform.GroupService;
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

@Tag(name = "Groups API", description = "The Group API")
@RestController
@RequestMapping("/api/v1/platform/groups")
@Validated
public class GroupController extends AbstractBaseAPI {

    private static final String NAMESPACE = "platform/group";

    private final GroupService service;

    @Autowired
    public GroupController(GroupService service) {
        this.service = service;
    }

    @Operation(description = "Search Groups by criteria")
    @GetMapping
    public ResponseEntity<DataListDto<GroupDto>> search(
            @Valid @ParameterObject GroupSearchCriteria sr) {
        return ResponseEntity.ok(service.search(sr));
    }

    @Operation(description = "Get group by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<GroupDto>> findById(
            @Parameter(description = "The group's ID", required = true)
            @Positive @PathVariable Short id) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(id)));
    }

    @Operation(description = "Get group roles by group ID")
    @GetMapping("/{id}/group-roles")
    public ResponseEntity<DataListDto<GroupRoleDto>> getGroupRoles(
            @Parameter(description = "The group's ID", required = true)
            @Positive @PathVariable Short id,
            @Valid @ParameterObject DefaultSearchCriteria sc) {
        return ResponseEntity.ok(service.findGroupRoles(id, sc));
    }

    @Operation(description = "Get group users by group ID")
    @GetMapping("/{id}/group-users")
    public ResponseEntity<DataListDto<GroupUserDto>> getGroupUsers(
            @Parameter(description = "The group's ID", required = true)
            @Positive @PathVariable Short id,
            @Valid @ParameterObject DefaultSearchCriteria sc) {
        return ResponseEntity.ok(service.findGroupUsers(id, sc));
    }

    @Operation(description = "Create a new group")
    @PostMapping
    public ResponseEntity<ResponseMessage<GroupDto>> create(
            @Parameter(description = "The group's payload", required = true)
            @RequestBody @ValidDto(value = NAMESPACE, operation = CREATE) GroupDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @Operation(description = "Update an existing group")
    @PatchMapping
    public ResponseEntity<ResponseMessage<GroupDto>> update(
            @Parameter(description = "The group's payload", required = true)
            @RequestBody @ValidDto(value = NAMESPACE, operation = UPDATE) GroupDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.update(dto)));
    }

    @Operation(description = "Delete a group by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> delete(
            @Parameter(description = "The group's ID", required = true)
            @ParameterObject @ValidDto(value = NAMESPACE, operation = DELETE) GroupDto dto) {
        service.deleteById(dto.getId());
        return ResponseEntity.ok(_success("Group successfully deleted."));
    }
}
