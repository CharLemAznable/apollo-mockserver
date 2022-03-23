package com.github.charlemaznable.apollo;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public final class MockApolloServerForAll implements BeforeAllCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        MockApolloServer.setUpMockServer();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        MockApolloServer.tearDownMockServer();
    }
}
