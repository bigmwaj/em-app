package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.Constants;
import ca.bigmwaj.emapp.as.api.shared.Message;
import ca.bigmwaj.emapp.as.dto.platform.PrivilegeDto;
import ca.bigmwaj.emapp.as.dto.platform.PrivilegeSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByClause;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClause;
import ca.bigmwaj.emapp.as.dto.shared.search.WhereClauseJoinOp;
import ca.bigmwaj.emapp.as.service.platform.PrivilegeService;
import ca.bigmwaj.emapp.as.validator.shared.SortByClauseSupportedField;
import ca.bigmwaj.emapp.as.validator.shared.ValidSortByClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.ValidWhereClausePatterns;
import ca.bigmwaj.emapp.as.validator.shared.WhereClauseSupportedField;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "privilege", description = "The Privilege API")
@RestController
@RequestMapping("/api/v1/platform/privileges")
@Validated
public class PrivilegeController extends AbstractBaseAPI {

    private final PrivilegeService service;

    @Autowired
    public PrivilegeController(PrivilegeService service) {
        this.service = service;
    }

    @Operation(
            summary = "Search Privileges by criteria",
            externalDocs = @ExternalDocumentation(
                    description = "Full documentation",
                    url = "https://github.com/bigmwaj/smart-cm-project/tree/main/smart-cm-project-admin/README.md"
            ),
            description = "Search privileges with optional filters and pagination",
            tags = {"privilege"}
    )
    @GetMapping
    public ResponseEntity<SearchResultDto<PrivilegeDto>> search(
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
            Short assignableToRoleId,

            @ValidWhereClausePatterns(
                    supportedFields = {
                            @WhereClauseSupportedField(name = "name", type = String.class),
                            @WhereClauseSupportedField(name = "description", type = String.class),
                    })
            @Parameter(description = "Filter results based on the following supported filter fields." +
                    "<ul>" +
                    "<li><b>name</b></li>" +
                    "<li><b>description</b></li>" +
                    "</ul>" +
                    Constants.FILTER_DOC)
            @RequestParam(value = "filters", required = false)
            List<WhereClause> whereClauses,

            @ValidSortByClausePatterns(
                    supportedFields = {
                            @SortByClauseSupportedField(name = "name"),
                            @SortByClauseSupportedField(name = "description"),
                    })
            @RequestParam(value = "sortBy", required = false)
            List<SortByClause> sortByClauses
    ) {

        var builder = PrivilegeSearchCriteria.builder()
                .withCalculateStatTotal(calculateStatTotal)
                .withPageSize(pageSize)
                .withPageIndex(pageIndex)
                .withWhereClauseJoinOp(whereClauseJoinOp)
                .withWhereClauses(whereClauses)
                .withSortByClauses(sortByClauses)
                .withAssignableToRoleId(assignableToRoleId);

        return ResponseEntity.ok(service.search(builder.build()));
    }

    @GetMapping("sync")
    public ResponseEntity<Message> sync() {
        return ResponseEntity.ok(_success("Total added privileges: %d".formatted(service.syncPrivileges())));
    }
}
