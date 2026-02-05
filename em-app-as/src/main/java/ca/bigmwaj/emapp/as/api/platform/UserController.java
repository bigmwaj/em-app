package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.*;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.dto.platform.UserFilterDto;
import ca.bigmwaj.emapp.as.dto.shared.search.FilterItem;
import ca.bigmwaj.emapp.as.dto.shared.search.SortByItem;
import ca.bigmwaj.emapp.as.service.platform.UserService;
import ca.bigmwaj.emapp.as.shared.MessageConstants;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
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

@Tag(name = "user", description = "The User API")
@RestController
@RequestMapping("/api/v1/platform/user")
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

            @Positive
            @Parameter(description = "The page index for filtering")
            @RequestParam(value = "pageIndex", required = false)
            Integer pageIndex,

            @Parameter(description = "Calculate the total corresponding to filters")
            @RequestParam(value = "calculateStatTotal", required = false)
            boolean calculateStatTotal,

            @ValidFilterPatterns(
                    supportedFields = {
                            @FilterSupportedField(name = "status", type = UserStatusLvo.class),
                            @FilterSupportedField(name = "username", type = String.class),
                            @FilterSupportedField(name = "firstName", type = String.class, rootEntityName = "c"),
                            @FilterSupportedField(name = "lastName", type = String.class, rootEntityName = "c"),
                            @FilterSupportedField(name = "phone", type = String.class, rootEntityName = "cp"),
                            @FilterSupportedField(name = "email", type = String.class, rootEntityName = "ce"),
                            @FilterSupportedField(name = "address", type = String.class, rootEntityName = "ca"),
                    })
            @Parameter(description = "Filter results based on the following supported filter fields." +
                    "<ul>" +
                    "<li><b>status</b></li>" +
                    "<li><b>username</b></li>" +
                    "<li><b>firstName</b></li>" +
                    "<li><b>lastName</b></li>" +
                    "<li><b>phone</b></li>" +
                    "<li><b>email</b></li>" +
                    "<li><b>address</b></li>" +
                    "</ul>" +
                    Constants.FILTER_DOC)
            @RequestParam(value = "filters", required = false)
            List<FilterItem> filterItems,

            @ValidSortByPatterns(
                    supportedFields = {
                            @SortBySupportedField(name = "status"),
                            @SortBySupportedField(name = "username"),
                            @SortBySupportedField(name = "firstName", rootEntityName = "c"),
                            @SortBySupportedField(name = "lastName", rootEntityName = "c"),
                            @SortBySupportedField(name = "phone", rootEntityName = "cp"),
                            @SortBySupportedField(name = "email", rootEntityName = "ce"),
                            @SortBySupportedField(name = "address", rootEntityName = "ca"),
                    })
            @RequestParam(value = "sortBy", required = false)
            List<SortByItem> sortByItems
    ) {

        var builder = UserFilterDto.builder()
                .withCalculateStatTotal(calculateStatTotal)
                .withPageSize(pageSize)
                .withPageIndex(pageIndex)
                .withFilterItems(filterItems)
                .withSortByItems(sortByItems);

        return ResponseEntity.ok(service.search(builder.build()));
    }

    @Operation(
            summary = "Get user by ID",
            description = "",
            tags = {"user"}
    )
    @GetMapping("/user-id/{userId}")
    public ResponseEntity<ResponseMessage<UserDto>> findById(
            @Parameter(description = "The user's ID", required = true)
            @Positive @PathVariable Long userId) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(userId)));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<UserDto>> create(
            @Parameter(description = "The user's payload", required = true)
            @RequestBody @Validated UserDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @PatchMapping
    public ResponseEntity<ResponseMessage<UserDto>> update(
            @Parameter(description = "The user's payload", required = true)
            @RequestBody @Validated UserDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.update(dto)));
    }

    @DeleteMapping("/user-id/{userId}")
    public ResponseEntity<Message> delete(
            @Parameter(description = "The user's ID", required = true)
            @Positive @PathVariable Long userId) {
        service.deleteById(userId);
        return ResponseEntity.ok(_success(MessageConstants.MSG0012));
    }
}
