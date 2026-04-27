package com.Group4.personalAssistant;

import static org.junit.Assert.*;
import org.junit.Test;

public class RegisterActivityTest {

    @Test
    public void testValidPassword() {
        assertTrue("Password should be valid", RegisterActivity.isValidPassword("Valid123!"));
    }

    @Test
    public void testPasswordTooShort() {
        assertFalse("Password is too short", RegisterActivity.isValidPassword("V123!"));
    }

    @Test
    public void testPasswordMissingUppercase() {
        assertFalse("Password missing uppercase", RegisterActivity.isValidPassword("valid123!"));
    }

    @Test
    public void testPasswordMissingLowercase() {
        assertFalse("Password missing lowercase", RegisterActivity.isValidPassword("VALID123!"));
    }

    @Test
    public void testPasswordMissingSpecial() {
        assertFalse("Password missing special character", RegisterActivity.isValidPassword("Valid123"));
    }

    @Test
    public void testPasswordWithDisallowedChar() {
        assertFalse("Password contains disallowed character '-'", RegisterActivity.isValidPassword("Valid123-"));
        assertFalse("Password contains disallowed character ','", RegisterActivity.isValidPassword("Valid123,"));
        assertFalse("Password contains disallowed character '<'", RegisterActivity.isValidPassword("Valid123<"));
        assertFalse("Password contains disallowed character '>'", RegisterActivity.isValidPassword("Valid123>"));
    }

    @Test
    public void testNullPassword() {
        assertFalse("Password is null", RegisterActivity.isValidPassword(null));
    }
}