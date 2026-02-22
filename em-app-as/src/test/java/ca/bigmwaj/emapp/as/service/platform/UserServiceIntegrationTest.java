package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.builder.platform.TestContactDtoBuilder;
import ca.bigmwaj.emapp.as.builder.platform.TestUserDtoBuilder;
import ca.bigmwaj.emapp.as.dao.platform.UserDao;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.validator.xml.common.AbstractDtoValidatorTest;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for UserService using H2 in-memory database.
 * Tests CRUD operations with real database interactions.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest extends AbstractDtoValidatorTest {

    @Autowired
    private UserService userService;

    @Autowired
    private Validator validator;

    @Autowired
    private UserDao userDao;

    private UserDto validDto;

    @BeforeEach
    void setUp() {
        // Clean up any existing test data
        //userDao.deleteAll();

        // Create a test user DTO with required fields but without child contact points
        // to avoid cascade issues in tests
        validDto = TestUserDtoBuilder.builderWithAllDefaults().build();

        validDto.setUsername("testuser@example.com");
        validDto.setPassword("TestPassword123!");
    }


}
