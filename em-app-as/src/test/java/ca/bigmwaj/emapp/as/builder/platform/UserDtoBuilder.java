package ca.bigmwaj.emapp.as.builder.platform;

import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UsernameTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;

public class UserDtoBuilder {
    public static UserDtoBuilder.Builder builder() {
        return new UserDtoBuilder.Builder();
    }

    public static class Builder {

        private final static HolderTypeLvo HOLDER_TYPE = HolderTypeLvo.CORPORATE;

        private final UserDto dto = new UserDto();

        private ContactDtoBuilder.Builder contactBuilder;

        public UserDto build() {
            if (contactBuilder != null) {
                dto.setContact(contactBuilder.build());
            }
            return dto;
        }

        public UserDtoBuilder.Builder withDefaults() {
            dto.setEditAction(EditActionLvo.CREATE);
            dto.setUsername("Test User Name");
            dto.setPassword("Test Pass&word123");
            dto.setHolderType(HOLDER_TYPE);
            dto.setStatus(UserStatusLvo.ACTIVE);
            dto.setUsernameType(UsernameTypeLvo.BASIC);

            return this;
        }

        public UserDtoBuilder.Builder withDefaultContactBuilder() {
            return withContactBuilder(ContactDtoBuilder.builder().withDefaults(HOLDER_TYPE));
        }

        public UserDtoBuilder.Builder withContactBuilder(ContactDtoBuilder.Builder contactBuilder) {
            this.contactBuilder = contactBuilder;
            return this;
        }
    }

    public static UserDtoBuilder.Builder builderWithAllDefaults() {
        return UserDtoBuilder.builder().withDefaults()
                .withContactBuilder(ContactDtoBuilder.builderWithAllDefaults(Builder.HOLDER_TYPE));
    }
}
