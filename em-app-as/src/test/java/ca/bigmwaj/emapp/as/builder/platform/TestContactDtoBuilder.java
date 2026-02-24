package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.ContactAddressDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactEmailDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactPhoneDto;
import ca.bigmwaj.emapp.dm.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@NoArgsConstructor
@SuperBuilder(toBuilder = true, setterPrefix = "with")
public class TestContactDtoBuilder extends ContactDto {
    public static TestContactDtoBuilderBuilder withDefaults() {
        return TestContactDtoBuilder.builder()
                .withEditAction(EditActionLvo.CREATE)
                .withFirstName("Test First Name")
                .withLastName("Test Last Name")
                .withHolderType(OwnerTypeLvo.ACCOUNT)
                .withBirthDate(LocalDate.now().minusYears(21));
    }

    public static TestContactDtoBuilderBuilder builderWithAllDefaults() {
        return builderWithAllDefaults(OwnerTypeLvo.ACCOUNT);
    }

    public static TestContactDtoBuilderBuilder builderWithAllDefaults(OwnerTypeLvo ownerType) {
        return (TestContactDtoBuilderBuilder) TestContactDtoBuilder
                .withDefaults()
                .withHolderType(ownerType)
                .withAddress((ContactAddressDto) TestContactAddressDtoBuilder.withDefaults().withHolderType(ownerType).build())
                .withEmail((ContactEmailDto) TestContactEmailDtoBuilder.withDefaults().withHolderType(ownerType).build())
                .withPhone((ContactPhoneDto) TestContactPhoneDtoBuilder.withDefaults().withHolderType(ownerType).build());
    }

}
