package com.Group4.personalAssistant;

import org.junit.Test;
import static org.junit.Assert.*;

public class TaskTest {

    @Test
    public void testTaskCreation() {
        Task task = new Task("Grocery Shopping", "Sun, Apr 28, 2026");
        
        assertEquals("Grocery Shopping", task.getTitle());
        assertEquals("Sun, Apr 28, 2026", task.getDueDate());
    }

    @Test
    public void testSetters() {
        Task task = new Task("Initial Title", "Mon, Jan 1, 2024");
        
        task.setTitle("Updated Title");
        task.setDueDate("Tue, Feb 2, 2025");

        assertEquals("Updated Title", task.getTitle());
        assertEquals("Tue, Feb 2, 2025", task.getDueDate());
    }

    @Test
    public void testToString() {
        Task task = new Task("Finish Homework", "Fri, May 15, 2026");
        String expectedOutput = "Task: Finish Homework\nDue: Fri, May 15, 2026";
        
        assertEquals(expectedOutput, task.toString());
    }
}
