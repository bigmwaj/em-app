package ca.bigmwaj.emapp.as.api.platform;

import ca.bigmwaj.emapp.as.api.AbstractBaseAPI;
import ca.bigmwaj.emapp.as.service.platform.AddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "address", description = "The Address API")
@RestController
@RequestMapping("/api/v1/contact/id/{contactId}/address")
public class ContactAddressController extends AbstractBaseAPI {

    @Autowired
    private AddressService service;

}
