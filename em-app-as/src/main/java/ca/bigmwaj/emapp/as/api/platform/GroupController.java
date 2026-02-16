package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.*;
import ca.bigmwaj.emapp.as.validator.shared.WhereClauseSupportedField;
import ca.bigmwaj.emapp.as.validator.shared.SortByClauseSupportedField;
import ca.bigmwaj.emapp.as.validator.shared.ValidWhereClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.ValidSortByClausePatterns;
import ca.bigmwaj.emapp.as.dto.common.DefaultSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.platform.GroupDto;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByClause;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClauseJoinOp;
import ca.bigmwaj.emapp.as.service.platform.GroupService;
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

@Tag(name = "group", description = "The Group API")
@RestController
@RequestMapping("/api/v1/platform/group")
@Validated
public class GroupController extends AbstractBaseAPI {

    private final GroupService service;

    @Autowired
    public GroupController(GroupService service) {
        this.service = service;
    }

    @Operation(
            summary = "Search Groups by criteria",
            externalDocs = @ExternalDocumentation(
                    description = "Full documentation",
                    url = "https://github.com/bigmwaj/smart-cm-project/tree/main/smart-cm-project-admin/README.md"
            ),
            description = "Search groups with optional filters and pagination",
            tags = {"group"}
    )
    @GetMapping
    public ResponseEntity<SearchResultDto<GroupDto>> search(
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
            List<SortByClause> sortByClauses
    ) {

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
            summary = "Get group by ID",
            description = "",
            tags = {"group"}
    )
    @GetMapping("/id/{groupId}")
    public ResponseEntity<ResponseMessage<GroupDto>> findById(
            @Parameter(description = "The group's ID", required = true)
            @Positive @PathVariable Short groupId) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(groupId)));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<GroupDto>> create(
            @Parameter(description = "The group's payload", required = true)
            @RequestBody @Valid GroupDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @PatchMapping
    public ResponseEntity<ResponseMessage<GroupDto>> update(
            @Parameter(description = "The group's payload", required = true)
            @RequestBody @Valid GroupDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.update(dto)));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Message> delete(
            @Parameter(description = "The group's ID", required = true)
            @Positive @PathVariable Short groupId) {
        service.deleteById(groupId);
        return ResponseEntity.ok(_success("Group successfully deleted."));
    }
}
