package com.Group4.personalAssistant;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    RegisterActivityTest.class,
    TaskTest.class,
    CalendarTest.class
})
public class AllTestsSuite {
}