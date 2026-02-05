package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.service.platform.AccountContactService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "accountContact", description = "The Account Contact API")
@RestController
@RequestMapping("/api/v1/account/id/{accountId}/contact")
public class AccountContactController extends AbstractBaseAPI {

    @Autowired
    private AccountContactService service;
}
