package ca.bigmwaj.emapp.as.validator.xml.common;

import ca.bigmwaj.emapp.as.integration.KafkaPublisher;
import ca.bigmwaj.emapp.as.service.AbstractBaseService;
import ca.bigmwaj.emapp.as.validator.xml.ValidationConfigurationException;
import ca.bigmwaj.emapp.dm.dto.AbstractBaseDto;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for ContactDto XML-driven validation.
 * Tests the complete flow from DTO annotation to validation execution.
 */
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public abstract class AbstractDtoValidatorTest {

    protected Logger logger = LoggerFactory.getLogger(getClass());

//    @MockitoBean
    protected KafkaPublisher kafkaPublisher; // Mock KafkaPublisher to avoid actual Kafka interactions during tests

    @BeforeAll
    static void beforeAll() {
        var authentication = new UsernamePasswordAuthenticationToken("TEST_USER", null, Collections.emptyList());
        //authentication.setDetails();
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @BeforeEach
    protected void setUp() {
//        Mockito.mockStatic(SecurityContextHolder.class);
//        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
//        Authentication authentication = Mockito.mock(Authentication.class);
//        Mockito.when(authentication.getName()).thenReturn("TestUser");
//        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
//        Mockito.when(SecurityContextHolder.getContext()).thenReturn(securityContext);
    }

    @AfterEach
    protected void tearDown() {
//        Mockito.clearInvocations(SecurityContextHolder.class);
//        SecurityContextHolder.clearContext();
    }

    @Autowired
    protected Validator validator;

    protected void assertViolationsOnField(AbstractBaseDto nonValidDto, String fieldName) {
        assertViolationsOnField(nonValidDto, fieldName, null);
    }
    protected void assertViolationsOnField(AbstractBaseDto nonValidDto, String fieldName, String expectedMessage) {
        var violations = validator.validate(nonValidDto);
        assertFalse(violations.isEmpty(), "Expected violations for user with invalid data");
        assertTrue(violations.stream()
                        .anyMatch(v -> fieldName.equals(v.getPropertyPath().toString())),
                "Expected violation on '" + fieldName + "' field");

        if( expectedMessage != null ){
            assertTrue(violations.stream()
                            .anyMatch(v -> fieldName.equals(v.getPropertyPath().toString()) && expectedMessage.equals(v.getMessage())),
                    "Expected violation on '" + fieldName + "' field with message: " + expectedMessage);
        }
    }

    protected void assertThrowValidationConfigurationException(AbstractBaseDto nonValidDto, String message) {
        assertThrows(ValidationConfigurationException.class, () -> {
            try {
                validator.validate(nonValidDto);
            } catch (ValidationException e) {
                assertEquals(e.getCause().getMessage(), message);
                throw e.getCause();
            }
        });
    }

    protected void assertNoViolations(AbstractBaseDto dto) {
        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no violations for valid Contact");
    }
}
