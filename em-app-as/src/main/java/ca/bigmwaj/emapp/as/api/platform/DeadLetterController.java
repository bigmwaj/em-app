package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.Message;
import ca.bigmwaj.emapp.as.api.shared.ResponseMessage;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.platform.DeadLetterDto;
import ca.bigmwaj.emapp.as.dto.platform.DeadLetterSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.DataListDto;
import ca.bigmwaj.emapp.as.service.platform.DeadLetterService;
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

@Tag(name = "Dead Letters API", description = "The Dead Letters API")
@RestController
@RequestMapping("/api/v1/platform/dead-letters")
@Validated
public class DeadLetterController extends AbstractBaseAPI {

    private static final String NAMESPACE = "platform/dead-letter";

    private final DeadLetterService service;

    @Autowired
    public DeadLetterController(DeadLetterService service) {
        this.service = service;
    }

    @Operation(description = "Search Dead Letters by criteria")
    @GetMapping
    public ResponseEntity<DataListDto<DeadLetterDto>> search(
            @Valid @ParameterObject DeadLetterSearchCriteria sr) {
        return ResponseEntity.ok(service.search(sr));
    }

    @Operation(description = "Get dead letter by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<DeadLetterDto>> findById(
            @Parameter(description = "The dead letter's ID", required = true)
            @Positive @PathVariable Long id) {
        return ResponseEntity.ok(new ResponseMessage<>(service.findById(id)));
    }

    @Operation(description = "Create new dead letter")
    @PostMapping
    public ResponseEntity<ResponseMessage<DeadLetterDto>> create(
            @Parameter(description = "The deadLetter's payload", required = true)
            @RequestBody @ValidDto(value = NAMESPACE, operation = CREATE) DeadLetterDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.create(dto)));
    }

    @Operation(description = "Update an existing dead letter")
    @PatchMapping
    public ResponseEntity<ResponseMessage<DeadLetterDto>> update(
            @Parameter(description = "The DeadLetter's payload", required = true)
            @RequestBody @ValidDto(value = NAMESPACE, operation = UPDATE) DeadLetterDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.update(dto)));
    }

    @Operation(description = "Delete a dead letter by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> delete(
            @Parameter(description = "The deadLetter's ID", required = true)
            @ParameterObject @ValidDto(value = NAMESPACE, operation = DELETE) DeadLetterDto dto) {
        service.deleteById(dto.getId());
        return ResponseEntity.ok(_success("DeadLetter successfully deleted."));
    }

    @Operation(description = "Change the status of a dead letter")
    @PostMapping("/{id}/change-status/{status}")
    public ResponseEntity<ResponseMessage<DeadLetterDto>> changeStatus(
            @ParameterObject @ValidDto(value = NAMESPACE, operation = CHANGE_STATUS) DeadLetterDto dto) {
        return ResponseEntity.ok(new ResponseMessage<>(service.changeStatus(dto)));
    }
}
