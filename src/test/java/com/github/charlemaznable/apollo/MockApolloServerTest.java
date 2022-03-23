package com.github.charlemaznable.apollo;

import com.ctrip.framework.apollo.ConfigService;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@ExtendWith({MockApolloServerForAll.class, MockApolloServerForEach.class})
public class MockApolloServerTest {

    @Getter
    @Setter
    private int changeCount;

    @Test
    public void testMockApolloServer() {
        val testConfig = ConfigService.getConfig("test");
        assertEquals("aaa", testConfig.getProperty("AAA", "AAA"));
        assertEquals("bbb", testConfig.getProperty("BBB", "BBB"));
        assertEquals("ccc", testConfig.getProperty("CCC", "ccc"));
    }

    @Test
    public void testMockApolloServerModify() {
        val testConfig = ConfigService.getConfig("test");
        testConfig.addChangeListener(event ->
                event.changedKeys().forEach(x -> changeCount++));

        MockApolloServer.addOrModifyProperty("test", "AAA", "AAA");
        MockApolloServer.deleteProperty("test", "BBB");
        await().forever().until(() -> 2 == changeCount);
        assertEquals("AAA", testConfig.getProperty("AAA", "aaa"));
        assertEquals("BBB", testConfig.getProperty("BBB", "BBB"));
        assertEquals("ccc", testConfig.getProperty("CCC", "ccc"));

        MockApolloServer.deleteProperty("test", "AAA");
        MockApolloServer.addOrModifyProperty("test", "BBB", "bbb");
        await().forever().until(() -> 4 == changeCount);
        assertEquals("aaa", testConfig.getProperty("AAA", "aaa"));
        assertEquals("bbb", testConfig.getProperty("BBB", "BBB"));
        assertEquals("ccc", testConfig.getProperty("CCC", "ccc"));
    }
}
