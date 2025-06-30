package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.util.*;
import databaseClasses.Database;
import entityClasses.User;

public class DatabaseTest {
    private Database db;

    @BeforeEach
    public void setup() throws SQLException {
        db = new Database();  // Initialize here
        db.connectToDatabase();
        db.dropDatabase();
        db.createTables();
    }

    @AfterEach
    public void cleanup() throws SQLException {
        db.deleteUserByUsername("someUser");
    }

    @Test
    public void testInvitationCodeManagement() {
        String code = db.generateInvitationCode("test@example.com", "Student");
        assertNotNull(code);

        List<String> list = db.getInvitationList();
        assertTrue(list.stream().anyMatch(entry -> entry.contains("test@example.com")));

        assertTrue(db.invitationCodeExists(code, "test@example.com", "Student"));
        assertTrue(db.getNumberOfInvitations() > 0);

        db.removeInvitationAfterUse(code);
        assertFalse(db.invitationCodeExists(code, "test@example.com", "Student"));
    }

    @Test
    public void testOneTimePassword() throws SQLException {
        User user = new User("otpUser", "pass123", true, false, false, false, false);
        db.register(user);

        assertTrue(db.doesUserExist("otpUser"), "User should exist after registration");

        boolean otpSet = db.setOneTimePassword("otpUser", "123456");
        System.out.println("OTP set result: " + otpSet);
        assertTrue(otpSet);

        boolean firstCheck = db.checkOneTimePassword("otpUser", "123456");
        System.out.println("First OTP check result: " + firstCheck);
        assertTrue(firstCheck);

        boolean secondCheck = db.checkOneTimePassword("otpUser", "123456");
        System.out.println("Second OTP check result: " + secondCheck);
        assertFalse(secondCheck);
    }

    @Test
    public void testDeleteUser() throws SQLException {
        User user = new User("deleteMe", "pass", false, true, false, false, false);
        db.register(user);

        assertTrue(db.doesUserExist("deleteMe"));
        int before = db.getNumberOfUsers();

        assertTrue(db.deleteUserByUsername("deleteMe"));
        assertFalse(db.doesUserExist("deleteMe"));
        assertEquals(before - 1, db.getNumberOfUsers());
    }
    
    @Test
    public void testGetUserList() throws SQLException {
        User user = new User("listUser", "pass", false, true, false, false, false);
        db.register(user);

        List<String> usernames = db.getAllUsernames();
        assertTrue(usernames.contains("listUser"));

        List<String> userList = db.getUserList();
        assertTrue(userList.stream().anyMatch(u -> u.equals("listUser") || u.contains("listUser")));
    }

    @Test
    public void testUpdateUserRole() throws SQLException {
        User user = new User("roleUser", "pass", false, false, false, false, false);
        db.register(user);

        assertTrue(db.updateUserRole("roleUser", "Admin", "true"));
        db.getUserAccountDetails("roleUser");
        assertTrue(db.getCurrentAdminRole());

        assertTrue(db.updateUserRole("roleUser", "Admin", "false"));
        db.getUserAccountDetails("roleUser");
        assertFalse(db.getCurrentAdminRole());
    }
}

