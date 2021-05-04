package org.sterl.test.log4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestAppenderTest {
    private static Logger LOG = LoggerFactory.getLogger(TestAppenderTest.class);

    @BeforeEach
    void before() {
        TestAppender.clear();
    }

    @Test
    void testOneLog() {
        LOG.info("Hello test logger!");
        assertEquals(1, TestAppender.count(Level.INFO));
        assertTrue(TestAppender.first(Level.INFO, "test logger").isPresent());
        assertFalse(TestAppender.first(Level.INFO, "test logger!!").isPresent());
    }
    
    @Test
    void testManyLogs() {
        LOG.info("Hello test logger INFO!");
        LOG.warn("Hello test logger WARN!");
        LOG.error("Hello test logger ERROR!");
        LOG.error("Hello test logger ERROR2!");
        
        assertEquals(2, TestAppender.count(Level.ERROR));
        assertEquals(4, TestAppender.count());
        
        assertFalse(TestAppender.first(Level.INFO, "logger WARN").isPresent());
        assertTrue(TestAppender.first(Level.WARN, "logger WARN").isPresent());
        
        assertTrue(TestAppender.first(Level.ERROR, "ERROR2").isPresent());
    }

}
