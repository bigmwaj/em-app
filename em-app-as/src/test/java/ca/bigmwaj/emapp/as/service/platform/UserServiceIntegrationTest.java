package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.builder.platform.TestUserDtoBuilder;
import ca.bigmwaj.emapp.as.dao.platform.UserDao;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.validator.xml.common.AbstractDtoValidatorTest;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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

    @BeforeEach
    void setUp() {

    }

    @Test
    void test_CreateUser() {

    }

}
