package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.api.shared.Message;
import ca.bigmwaj.emapp.as.dto.platform.PrivilegeDto;
import ca.bigmwaj.emapp.as.dto.platform.PrivilegeSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.DataListDto;
import ca.bigmwaj.emapp.as.service.platform.PrivilegeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Privileges API", description = "The Privilege API")
@RestController
@RequestMapping("/api/v1/platform/privileges")
@Validated
public class PrivilegeController extends AbstractBaseAPI {

    private static final String NAMESPACE = "platform/privilege";

    private final PrivilegeService service;

    @Autowired
    public PrivilegeController(PrivilegeService service) {
        this.service = service;
    }

    @Operation(description = "Search Privileges by criteria")
    @GetMapping
    public ResponseEntity<DataListDto<PrivilegeDto>> search(
            @Valid @ParameterObject PrivilegeSearchCriteria sr) {
        return ResponseEntity.ok(service.search(sr));
    }

    @Operation(description = "Sync privileges with the system's permissions")
    @PostMapping("sync")
    public ResponseEntity<Message> sync() {
        return ResponseEntity.ok(_success("Total added privileges: %d".formatted(service.syncPrivileges())));
    }
}
