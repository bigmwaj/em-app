package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.*;
import ca.bigmwaj.emapp.as.dto.platform.UserSearchCriteria;
import ca.bigmwaj.emapp.as.validator.shared.WhereClauseSupportedField;
import ca.bigmwaj.emapp.as.validator.shared.SortByClauseSupportedField;
import ca.bigmwaj.emapp.as.validator.shared.ValidWhereClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.ValidSortByClausePatterns;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByClause;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClauseJoinOp;
import ca.bigmwaj.emapp.as.service.platform.UserService;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import ca.bigmwaj.emapp.as.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.as.lvo.platform.OwnerTypeLvo;
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

@Tag(name = "user", description = "The User API")
@RestController
@RequestMapping("/api/v1/platform/users")
@Validated
public class UserController extends AbstractBaseAPI {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(
            summary = "Search Users by criteria",
            externalDocs = @ExternalDocumentation(
                    description = "Full documentation",
                    url = "https://github.com/bigmwaj/smart-cm-project/tree/main/smart-cm-project-admin/README.md"
            ),
            description = "Must of the time, you must encode in base64 and pass it as query param with name q",
            tags = {"user"}
    )
    @GetMapping
    public ResponseEntity<SearchResultDto<UserDto>> search(
            @Positive
            @Parameter(description = "The page total to send")
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
            Short assignableToRoleId,

            @PositiveOrZero
            Short assignableToGroupId,

            @ValidWhereClausePatterns(
                    supportedFields = {
                            @WhereClauseSupportedField(name = "status", type = UserStatusLvo.class),
                            @WhereClauseSupportedField(name = "ownerType", type = OwnerTypeLvo.class),
                            @WhereClauseSupportedField(name = "username", type = String.class),
                            @WhereClauseSupportedField(name = "firstName", type = String.class, rootEntityName = "c"),
                            @WhereClauseSupportedField(name = "lastName", type = String.class, rootEntityName = "c"),
                            @WhereClauseSupportedField(name = "phone", type = String.class, rootEntityName = "cp"),
                            @WhereClauseSupportedField(name = "email", type = String.class, rootEntityName = "ce"),
                            @WhereClauseSupportedField(name = "address", type = String.class, rootEntityName = "ca"),
                    })
            @Parameter(description = "Filter results based on the following supported filter fields." +
                    "<ul>" +
                    "<li><b>status</b></li>" +
                    "<li><b>ownerType</b></li>" +
                    "<li><b>username</b></li>" +
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
                            @SortByClauseSupportedField(name = "status"),
                            @SortByClauseSupportedField(name = "ownerType"),
                            @SortByClauseSupportedField(name = "username"),
                            @SortByClauseSupportedField(name = "firstName", rootEntityName = "c"),
                            @SortByClauseSupportedField(name = "lastName", rootEntityName = "c"),
                            @SortByClauseSupportedField(name = "phone", rootEntityName = "cp"),
                            @SortByClauseSupportedField(name = "email", rootEntityName = "ce"),
                            @SortByClauseSupportedField(name = "address", rootEntityName = "ca"),
                    })
            @RequestParam(value = "sortBy", required = false)
            List<SortByClause> sortByClauses
    ) {

        var builder = UserSearchCriteria.builder()
                .withCalculateStatTotal(calculateStatTotal)
                .withPageSize(pageSize)
                .withPageIndex(pageIndex)
                .withWhereClauseJoinOp(whereClauseJoinOp)
                .withWhereClauses(whereClauses)
                .withSortByClauses(sortByClauses)
                .withAssignableToRoleId(assignableToRoleId)
                .withAssignableToGroupId(assignableToGroupId);


        return ResponseEntity.ok(service.search(builder.build()));
    }

    @Operation(
            summary = "Get user by ID",
            description = "",
            tags = {"user"}
    )
    @GetMapping("/id/{userId}")
    public ResponseEntity<ResponseMessage<UserDto>> findById(
            @Parameter(description = "The user's ID", required = true)
            @Positive @PathVariable Short userId) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(userId)));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<UserDto>> create(
            @Parameter(description = "The user's payload", required = true)
            @RequestBody @Valid UserDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @PatchMapping
    public ResponseEntity<ResponseMessage<UserDto>> update(
            @Parameter(description = "The user's payload", required = true)
            @RequestBody @Valid UserDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.update(dto)));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Message> delete(
            @Parameter(description = "The user's ID", required = true)
            @Positive @PathVariable Short userId) {
        service.deleteById(userId);
        return ResponseEntity.ok(_success(MessageConstants.MSG0012));
    }
}
