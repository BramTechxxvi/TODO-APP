package org.bram.services;

import org.bram.data.models.User;
import org.bram.data.repositories.UserRepository;
import org.bram.dtos.requests.*;
import org.bram.dtos.response.*;
import org.bram.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServicesImplTest {

    @Autowired
    private UserServices userServices;
    @Autowired
    private UserRepository userRepository;
    private RegisterUserRequest registerRequest;
    private RegisterUserResponse registerResponse;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private ChangePasswordRequest changePasswordRequest;
    private ChangePasswordResponse changePasswordResponse;
    private ChangeEmailResponse changeEmailResponse;
    private ChangeEmailRequest changeEmailRequest;
    private UserLogoutRequest logoutRequest;
    private UserLogoutResponse logoutResponse;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        registerRequest = new RegisterUserRequest();
        registerResponse = new RegisterUserResponse();
        loginRequest = new LoginRequest();
        loginResponse = new LoginResponse();
        changePasswordRequest = new ChangePasswordRequest();
        changePasswordResponse = new ChangePasswordResponse();
        changeEmailResponse = new ChangeEmailResponse();
        changeEmailRequest = new ChangeEmailRequest();
        logoutRequest = new UserLogoutRequest();
        logoutResponse = new UserLogoutResponse();
    }

    @Test
    public void registerUserTest() {
        registerUser();
        User user = userRepository.findById(registerResponse.getUserId()).orElseThrow();
        assertNotNull(user.getId());
        assertFalse(user.isLoggedIn());
        assertEquals("Registration Successful", registerResponse.getMessage());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void registerNewUserWithExistingEmail__throwsExceptionTest() {
        registerUser();
        RegisterUserRequest newRequest = new RegisterUserRequest();
        newRequest.setFirstName("Adedeji");
        newRequest.setLastName("Ibrahim");
        newRequest.setEmail("grace@ayoola.com");
        newRequest.setPassword("password");
        Exception error = assertThrows(DetailsAlreadyInUseException.class, ()-> userServices.registerUser(newRequest));
        assertEquals("Email already exists", error.getMessage());
    }

    @Test
    public void loginAUser__loginTest() {
        registerUser();
        loginRequest.setEmail("grace@ayoola.com");
        loginRequest.setPassword("123456");
        loginResponse = userServices.login(loginRequest);
        User user = userRepository.findById(loginResponse.getUserId()).orElseThrow();
        assertNotNull(user.getId());
        assertTrue(user.isLoggedIn());
        assertEquals("Welcome back Grace Ayoola", loginResponse.getMessage());
    }

    @Test
    public void loginAUserWithInvalidEmail__throwsExceptionTest() {
        registerUser();
        loginRequest.setEmail("grace@ayola.com");
        loginRequest.setPassword("123456");
        Exception error = assertThrows(InvalidCredentialsException.class, ()-> userServices.login(loginRequest));
        assertEquals("Invalid credentials", error.getMessage());
    }

    @Test
    public void loginAUserWithInvalidPassword__throwsExceptionTest() {
        registerUser();
        loginRequest.setEmail("grace@ayoola.com");
        loginRequest.setPassword("12345");
        Exception error = assertThrows(InvalidCredentialsException.class, ()-> userServices.login(loginRequest));
        assertEquals("Invalid credentials", error.getMessage());
    }

    @Test
    public void userCanChangePassword__changePasswordTest() {
        loginAUser__loginTest();
        changePasswordRequest.setUserId(loginResponse.getUserId());
        changePasswordRequest.setOldPassword("123456");
        changePasswordRequest.setNewPassword("password");
        changePasswordResponse = userServices.changePassword(changePasswordRequest);
        assertEquals("Password changed successfully", changePasswordResponse.getMessage());
    }

    @Test
    public void changePasswordWithSameOldPassword__throwsException() {
        loginAUser__loginTest();
        changePasswordRequest.setUserId(loginResponse.getUserId());
        changePasswordRequest.setOldPassword("password");
        changePasswordRequest.setNewPassword("password");
        Exception error = assertThrows(SamePasswordException.class, ()-> userServices.changePassword(changePasswordRequest));
        assertEquals("New password cannot be the same as the old password", error.getMessage());
    }

    @Test
    public void changePasswordWithWrongOldPassword__throwsException() {
        loginAUser__loginTest();
        changePasswordRequest.setUserId(loginResponse.getUserId());
        changePasswordRequest.setOldPassword("vudcnbis");
        changePasswordRequest.setNewPassword("123456");
        Exception error = assertThrows(IncorrectOldPasswordException.class, ()-> userServices.changePassword(changePasswordRequest));
        assertEquals("Old password not correct", error.getMessage());
    }

    @Test
    public void userCanChangeEmail__changeEmailTest() {
        loginAUser__loginTest();
        changeEmailRequest.setUserId(loginResponse.getUserId());
        changeEmailRequest.setOldEmail("grace@ayoola.com");
        changeEmailRequest.setNewEmail("graceAyoola@gmail.com");
        changeEmailResponse = userServices.changeEmail(changeEmailRequest);
        assertEquals("Email changed successfully", changeEmailResponse.getMessage());
    }

    @Test
    public void changeEmailWithWrongOldEmail__throwsException() {
        loginAUser__loginTest();
        changeEmailRequest.setUserId(loginResponse.getUserId());
        changeEmailRequest.setOldEmail("ayoola@ayoola.com");
        changeEmailRequest.setNewEmail("graceAyoola@yahoo.com");
        Exception error = assertThrows(IncorrectOldEmailException.class, ()-> userServices.changeEmail(changeEmailRequest));
        assertEquals("Old email not correct", error.getMessage());
    }

    @Test
    public void changeEmailWithSameOldEmail__throwsException() {
        loginAUser__loginTest();
        changeEmailRequest.setUserId(loginResponse.getUserId());
        changeEmailRequest.setOldEmail("graceAyoola@gmail.com");
        changeEmailRequest.setNewEmail("graceAyoola@gmail.com");
        Exception error = assertThrows(SameEmailException.class, ()-> userServices.changeEmail(changeEmailRequest));
        assertEquals("New email cannot be same as old email", error.getMessage());
    }

    @Test
    public void userCanLogout__logoutTest() {
        loginAUser__loginTest();
        User user = userRepository.findById(loginResponse.getUserId()).orElseThrow();
        logoutRequest.setUserId(user.getId());
        logoutResponse = userServices.logout(logoutRequest);
        User loggedOutUser = userRepository.findById(user.getId()).orElseThrow();
        assertFalse(loggedOutUser.isLoggedIn());
        assertEquals("We hope to see you soon", logoutResponse.getMessage());
    }

    @Test
    public void logoutUserNotLoggedIn__throwsException() {
        userCanLogout__logoutTest();
        logoutRequest.setUserId(loginResponse.getUserId());
        Exception error = assertThrows(UserNotLoggedInException.class, ()-> userServices.logout(logoutRequest));
        assertEquals("User is not logged in", error.getMessage());
    }

    @Test
    public void changePasswordWhenNotLoggedIn__throwsException() {
        userCanLogout__logoutTest();
        changePasswordRequest.setUserId(loginResponse.getUserId());
        changePasswordRequest.setOldPassword("graceAyoola@gmail.com");
        changePasswordRequest.setNewPassword("olaiwola@gmail.com");
        Exception errorResponse = assertThrows(UserNotLoggedInException.class, ()-> userServices.changePassword(changePasswordRequest));
        assertEquals("User is not logged in", errorResponse.getMessage());
    }

    @Test
    public void changeEmailWhenNotLoggedIn__throwsException() {
        userCanLogout__logoutTest();
        changeEmailRequest.setUserId(loginResponse.getUserId());
        changeEmailRequest.setOldEmail("graceAyoola@gmail.com");
        changeEmailRequest.setNewEmail("olaiwola@gmail.com");
        Exception errorResponse = assertThrows(UserNotLoggedInException.class, ()-> userServices.changeEmail(changeEmailRequest));
        assertEquals("User is not logged in", errorResponse.getMessage());
    }

    private void registerUser() {
        registerRequest.setFirstName("Grace");
        registerRequest.setLastName("Ayoola");
        registerRequest.setEmail("grace@ayoola.com");
        registerRequest.setPassword("123456");
        registerResponse = userServices.registerUser(registerRequest);
    }
}