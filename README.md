[![Java CI with Maven](https://github.com/sterlp/log4j-test-logger/actions/workflows/maven.yml/badge.svg)](https://github.com/sterlp/log4j-test-logger/actions/workflows/maven.yml)

# log4j-test-logger
Simple in memory log4j2 appender to assert logs in JUnit tests.

# Alternative if you use Spring
https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/system/OutputCaptureExtension.html

# Usage

```java
    @BeforeEach
    void before() {
        TestAppender.clear();
    }
    @Test
    void yourTestMethod() {
        // GIVEN: log is clean
        TestAppender.clear();

        // WHEN: I call my fancy method
        subject.doFancyStuff(argument);

        // THEN: I should have
        assertFalse(TestAppender.first(Level.INFO, "Fancy method executed").isPresent());
        assertEquals(1, TestAppender.count(Level.INFO));
        assertEquals(0, TestAppender.count(Level.WARN));     
        assertEquals(0, TestAppender.count(Level.ERROR));
    }
```

# Configuration
## Dependency Maven pom.xml

```xml
        <dependency>
            <groupId>org.sterl.test.log4j</groupId>
            <artifactId>log4j-test-logger</artifactId>
            <version>0.1.0</version>
            <scope>test</scope>
        </dependency>
```

## Configuration log4j2.xml

In your `src/test/resources` add or extend the log4j file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n" />
        </Console>

        <!--  define the test appender -->
        <TestAppender name="TestAppender" />

    </Appenders>
    <Loggers>
        <Root level="debug">
            <AppenderRef ref="Console" />

            <!-- include the appender -->
            <AppenderRef ref="TestAppender" />

        </Root>
    </Loggers>
</Configuration>
```
