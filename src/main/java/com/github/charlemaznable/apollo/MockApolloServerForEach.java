package com.github.charlemaznable.apollo;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public final class MockApolloServerForEach implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        MockApolloServer.setUpMockServer();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        MockApolloServer.tearDownMockServer();
    }
}
