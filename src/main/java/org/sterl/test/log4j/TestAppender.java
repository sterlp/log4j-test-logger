package org.sterl.test.log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;


@Plugin(name = "TestAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class TestAppender extends AbstractAppender {

    /**
     * String of message as some logger e.g. RING buffer may reuse the event class
     */
    public static final ConcurrentMap<Level, List<String>> LOGS = new ConcurrentHashMap<>();

    protected TestAppender(String name, Filter filter) {
        super(name, filter, null, true, Property.EMPTY_ARRAY);
    }

    @PluginFactory
    public static TestAppender createAppender(
        @PluginAttribute("name") String name, 
        @PluginElement("Filter") Filter filter) {
        return new TestAppender(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        final List<String> messages = LOGS.computeIfAbsent(event.getLevel(), (l) -> new ArrayList<>());
        messages.add(event.getMessage().getFormattedMessage());
    }
    /**
     * Returns all logs with the given level.
     * @return the requested logs, never <code>null</code>
     */
    public static List<String> getLogs(Level level) {
        final ArrayList<String> result = new ArrayList<>();
        final List<String> logs = LOGS.get(level);
        if (logs != null) result.addAll(logs);
        return result;
    }

    public static int count() {
        return LOGS.values().stream().mapToInt(List::size).sum();
    }

    public static int count(Level level) {
        return getLogs(level).size();
    }
    
    public static int count(Level level, Level... levels) {
        int result = getLogs(level).size();
        for (Level l : levels) {
            result += getLogs(l).size();
        }
        return result;
    }

    public static Optional<String> first(Level info, String logMessage) {
        final List<String> logs = getLogs(info);
        return firstFrom(logMessage, logs);
    }

    private static Optional<String> firstFrom(String logMessage, final List<String> logs) {
        Optional<String> result = Optional.empty();
        for (String m : logs) {
            if (m.contains(logMessage)) {
                result = Optional.of(m);
                break;
            }
        }
        return result;
    }
    
    public static Optional<String> first(String logMessage) {
        Optional<String> result = Optional.empty();
        for (List<String> le : LOGS.values()) {
            result = firstFrom(logMessage, le);
            if (result.isPresent()) break;
        }
        return result;
    }
    
    public static void clear() {
        LOGS.clear();
    }
    
    public static void printAll() {
        for (Level l : LOGS.keySet()) {
            printLevel(l);
        }
    }

    public static void printLevel(Level level) {
        assert level != null;
        for (String m : getLogs(level)) {
            System.err.println(level + ": " + m);
        }
    }
}
