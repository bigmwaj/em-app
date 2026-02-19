package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.builder.platform.UserDtoBuilder;
import ca.bigmwaj.emapp.as.dao.platform.UserDao;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for UserService using H2 in-memory database.
 * Tests CRUD operations with real database interactions.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        // Clean up any existing test data
        userDao.deleteAll();
        
        // Create a test user DTO with all required fields
        testUserDto = UserDtoBuilder.builderWithAllDefaults().build();
        testUserDto.setUsername("testuser@example.com");
        testUserDto.setPassword("TestPassword123!");
    }

    @Test
    void shouldCreateUser() {
        // When
        UserDto createdUser = userService.create(testUserDto);

        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("testuser@example.com");
        assertThat(createdUser.getStatus()).isEqualTo(UserStatusLvo.ACTIVE);
        assertThat(createdUser.getContact()).isNotNull();
        assertThat(createdUser.getContact().getFirstName()).isEqualTo("Test First Name");
        assertThat(createdUser.getContact().getLastName()).isEqualTo("Test Last Name");
        
        // Password should be hashed, not plain text
        assertThat(createdUser.getPassword()).isNotEqualTo("TestPassword123!");
        assertThat(createdUser.getPassword()).isNotNull();
    }

    @Test
    void shouldFindUserById() {
        // Given
        UserDto createdUser = userService.create(testUserDto);
        Short userId = createdUser.getId();

        // When
        UserDto foundUser = userService.findById(userId);

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getUsername()).isEqualTo("testuser@example.com");
        assertThat(foundUser.getContact()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        Short nonExistentId = (short) 9999;

        // When / Then
        assertThatThrownBy(() -> userService.findById(nonExistentId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("User not found with id: " + nonExistentId);
    }

    @Test
    void shouldUpdateUser() {
        // Given
        UserDto createdUser = userService.create(testUserDto);
        Short userId = createdUser.getId();

        // When
        createdUser.setEditAction(EditActionLvo.UPDATE);
        createdUser.setUsername("updated@example.com");
        createdUser.setStatus(UserStatusLvo.INACTIVE);
        UserDto updatedUser = userService.update(createdUser);

        // Then
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(userId);
        assertThat(updatedUser.getUsername()).isEqualTo("updated@example.com");
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatusLvo.INACTIVE);
        
        // Verify update persisted
        UserDto foundUser = userService.findById(userId);
        assertThat(foundUser.getUsername()).isEqualTo("updated@example.com");
        assertThat(foundUser.getStatus()).isEqualTo(UserStatusLvo.INACTIVE);
    }

    @Test
    void shouldUpdateUserPasswordWhenProvided() {
        // Given
        UserDto createdUser = userService.create(testUserDto);
        String originalPassword = createdUser.getPassword();

        // When
        createdUser.setEditAction(EditActionLvo.UPDATE);
        createdUser.setPassword("NewPassword456!");
        UserDto updatedUser = userService.update(createdUser);

        // Then
        assertThat(updatedUser.getPassword()).isNotNull();
        assertThat(updatedUser.getPassword()).isNotEqualTo(originalPassword);
        assertThat(updatedUser.getPassword()).isNotEqualTo("NewPassword456!");
    }

    @Test
    void shouldPreservePasswordWhenNotProvided() {
        // Given
        UserDto createdUser = userService.create(testUserDto);
        String originalPassword = createdUser.getPassword();

        // When
        createdUser.setEditAction(EditActionLvo.UPDATE);
        createdUser.setPassword(null); // Don't update password
        createdUser.setUsername("updated2@example.com");
        UserDto updatedUser = userService.update(createdUser);

        // Then
        assertThat(updatedUser.getPassword()).isEqualTo(originalPassword);
    }

    @Test
    void shouldDeleteUser() {
        // Given
        UserDto createdUser = userService.create(testUserDto);
        Short userId = createdUser.getId();

        // When
        userService.deleteById(userId);

        // Then
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldFindAllUsers() {
        // Given
        UserDto user1 = UserDtoBuilder.builderWithAllDefaults().build();
        user1.setUsername("user1@example.com");
        user1.setPassword("Password1!");
        userService.create(user1);

        UserDto user2 = UserDtoBuilder.builderWithAllDefaults().build();
        user2.setUsername("user2@example.com");
        user2.setPassword("Password2!");
        userService.create(user2);

        UserDto user3 = UserDtoBuilder.builderWithAllDefaults().build();
        user3.setUsername("user3@example.com");
        user3.setPassword("Password3!");
        userService.create(user3);

        // When
        var result = userService.search(null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResult()).isNotNull();
        assertThat(result.getResult()).hasSize(3);
        assertThat(result.getResult())
                .extracting(UserDto::getUsername)
                .containsExactlyInAnyOrder("user1@example.com", "user2@example.com", "user3@example.com");
    }

    @Test
    void shouldCheckUsernameUniqueness() {
        // Given
        userService.create(testUserDto);

        // When / Then
        assertThat(userService.isUsernameUnique("testuser@example.com")).isFalse();
        assertThat(userService.isUsernameUnique("nonexistent@example.com")).isTrue();
    }

    @Test
    void shouldValidateAccountHolder() {
        // Given
        testUserDto.setUsername("holder@example.com");
        testUserDto.setStatus(UserStatusLvo.ACTIVE);
        userService.create(testUserDto);

        // When / Then - Should not throw exception for active user
        userService.validateAccountHolder("holder@example.com");
    }

    @Test
    void shouldThrowExceptionForNonExistentAccountHolder() {
        // When / Then
        assertThatThrownBy(() -> userService.validateAccountHolder("nonexistent@example.com"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("No user found with email");
    }

    @Test
    void shouldThrowExceptionForInactiveAccountHolder() {
        // Given
        testUserDto.setUsername("inactive@example.com");
        testUserDto.setStatus(UserStatusLvo.INACTIVE);
        userService.create(testUserDto);

        // When / Then
        assertThatThrownBy(() -> userService.validateAccountHolder("inactive@example.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User account is not active");
    }
}
