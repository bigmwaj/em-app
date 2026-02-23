package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.Constants;
import ca.bigmwaj.emapp.as.api.shared.Message;
import ca.bigmwaj.emapp.as.api.shared.ResponseMessage;
import ca.bigmwaj.emapp.as.dto.platform.RoleDto;
import ca.bigmwaj.emapp.as.dto.platform.RolePrivilegeDto;
import ca.bigmwaj.emapp.as.dto.platform.RoleSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.RoleUserDto;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByClause;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClauseJoinOp;
import ca.bigmwaj.emapp.as.service.platform.RoleService;
import ca.bigmwaj.emapp.as.validator.shared.SortByClauseSupportedField;
import ca.bigmwaj.emapp.as.validator.shared.ValidSortByClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.ValidWhereClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.WhereClauseSupportedField;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "role", description = "The Role API")
@RestController
@RequestMapping("/api/v1/platform/role")
@Validated
public class RoleController extends AbstractBaseAPI {

    private final RoleService service;

    @Autowired
    public RoleController(RoleService service) {
        this.service = service;
    }

    @Operation(
            summary = "Search Roles by criteria",
            externalDocs = @ExternalDocumentation(
                    description = "Full documentation",
                    url = "https://github.com/bigmwaj/smart-cm-project/tree/main/smart-cm-project-admin/README.md"
            ),
            description = "Search roles with optional filters and pagination",
            tags = {"role"}
    )
    @GetMapping
    public ResponseEntity<SearchResultDto<RoleDto>> search(
            @Positive
            @Parameter(description = "The page size to send")
            @RequestParam(value = "pageSize", required = false)
            Short pageSize,

            @PositiveOrZero
            @Parameter(description = "The page index for filtering")
            @RequestParam(value = "pageIndex", required = false)
            Integer pageIndex,

            @DefaultValue("and")
            @Parameter(description = "Where clause join operator. Default is and")
            @RequestParam(value = "whereClauseJoinOp", required = false)
            WhereClauseJoinOp whereClauseJoinOp,

            @Parameter(description = "Calculate the total corresponding to filters")
            @RequestParam(value = "calculateStatTotal", required = false)
            boolean calculateStatTotal,

            @PositiveOrZero
            Short assignableToGroupId,

            @ValidWhereClausePatterns(
                    supportedFields = {
                            @WhereClauseSupportedField(name = "name", type = String.class),
                            @WhereClauseSupportedField(name = "description", type = String.class),
                            @WhereClauseSupportedField(name = "holderType", type = HolderTypeLvo.class),
                    })
            @Parameter(description = "Filter results based on the following supported filter fields." +
                    "<ul>" +
                    "<li><b>name</b></li>" +
                    "<li><b>description</b></li>" +
                    "<li><b>holderType</b></li>" +
                    "</ul>" +
                    Constants.FILTER_DOC)
            @RequestParam(value = "filters", required = false)
            List<WhereClause> whereClauses,

            @ValidSortByClausePatterns(
                    supportedFields = {
                            @SortByClauseSupportedField(name = "name"),
                            @SortByClauseSupportedField(name = "description"),
                            @SortByClauseSupportedField(name = "holderType"),
                    })
            @RequestParam(value = "sortBy", required = false)
            List<SortByClause> sortByClauses) {

        var builder = RoleSearchCriteria.builder()
                .withCalculateStatTotal(calculateStatTotal)
                .withPageSize(pageSize)
                .withPageIndex(pageIndex)
                .withWhereClauseJoinOp(whereClauseJoinOp)
                .withWhereClauses(whereClauses)
                .withSortByClauses(sortByClauses)
                .withAssignableToGroupId(assignableToGroupId);

        return ResponseEntity.ok(service.search(builder.build()));
    }

    @Operation(
            summary = "Get role by ID",
            description = "",
            tags = {"role"}
    )
    @GetMapping("/id/{roleId}")
    public ResponseEntity<ResponseMessage<RoleDto>> findById(
            @Parameter(description = "The role's ID", required = true)
            @Positive @PathVariable Short roleId) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(roleId)));
    }

    @Operation(
            summary = "Get role privileges by role ID",
            description = "",
            tags = {"role", "privilege"}
    )
    @GetMapping("/id/{roleId}/privileges")
    public ResponseEntity<SearchResultDto<RolePrivilegeDto>> getRolePrivileges(
            @Parameter(description = "The role's ID", required = true)
            @Positive @PathVariable Short roleId) {
        return ResponseEntity.ok(service.findRolePrivileges(roleId));
    }

    @Operation(
            summary = "Get role users by role ID",
            description = "",
            tags = {"role", "user"}
    )
    @GetMapping("/id/{roleId}/users")
    public ResponseEntity<SearchResultDto<RoleUserDto>> getUserRoles(
            @Parameter(description = "The role's ID", required = true)
            @Positive @PathVariable Short roleId) {
        return ResponseEntity.ok(service.findRoleUsers(roleId));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<RoleDto>> create(
            @Parameter(description = "The role's payload", required = true)
            @RequestBody @Valid RoleDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @PatchMapping
    public ResponseEntity<ResponseMessage<RoleDto>> update(
            @Parameter(description = "The role's payload", required = true)
            @RequestBody @Valid RoleDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.update(dto)));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Message> delete(
            @Parameter(description = "The role's ID", required = true)
            @Positive @PathVariable Short roleId) {
        service.deleteById(roleId);
        return ResponseEntity.ok(_success("Role successfully deleted."));
    }
}
