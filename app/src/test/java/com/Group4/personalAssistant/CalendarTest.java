package com.Group4.personalAssistant;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class CalendarTest {

    @Test
    public void testFormatDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2026);
        calendar.set(Calendar.MONTH, Calendar.APRIL);
        calendar.set(Calendar.DAY_OF_MONTH, 28);
        
        String formattedDate = CalendarActivity.formatDate(calendar);

        assertEquals("Tue, Apr 28, 2026", formattedDate);
    }

    @Test
    public void testFormatTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 30);
        
        String formattedTime = CalendarActivity.formatTime(calendar);

        assertEquals("7:30 PM", formattedTime);
    }

    @Test
    public void testFormatTimeMidnight() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 15);
        
        String formattedTime = CalendarActivity.formatTime(calendar);

        assertEquals("12:15 AM", formattedTime);
    }
}
