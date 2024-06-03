package com.github.charlemaznable.apollo;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.core.dto.ApolloConfig;
import com.ctrip.framework.apollo.core.dto.ApolloConfigNotification;
import com.ctrip.framework.apollo.core.utils.ResourceUtils;
import com.ctrip.framework.apollo.internals.ConfigServiceLocator;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.slf4j.helpers.Reporter;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.joor.Reflect.on;

@NoArgsConstructor(access = PRIVATE)
public final class MockApolloServer {

    private static final Object serverLock = new Object();
    private static final Type notificationType
            = new TypeToken<List<ApolloConfigNotification>>() {}.getType();
    private static final Gson GSON = new Gson();
    private static final Map<String, List<Prop>>
            overriddenPropertiesOfNamespace = Maps.newConcurrentMap();
    private static final ConfigServiceLocator configServiceLocator;
    private static MockWebServer server;

    static {
        System.setProperty("apollo.longPollingInitialDelayInMills", "0");
        configServiceLocator = ApolloInjector.getInstance(ConfigServiceLocator.class);
    }

    @SneakyThrows
    public static void setUpMockServer() {
        synchronized (serverLock) {
            if (nonNull(server)) return;

            server = new MockWebServer();
            server.setDispatcher(new Dispatcher() {
                @Nonnull
                @Override
                public MockResponse dispatch(@Nonnull RecordedRequest request) {
                    if (requireNonNull(request.getPath()).startsWith("/notifications/v2")) {
                        val notifications = requireNonNull(request.getRequestUrl()).queryParameter("notifications");
                        return new MockResponse().setResponseCode(200).setBody(mockLongPollBody(notifications));
                    }
                    if (requireNonNull(request.getPath()).startsWith("/configs")) {
                        val pathSegments = requireNonNull(request.getRequestUrl()).pathSegments();
                        // appId [ pathSegments.get(1) ] and
                        // cluster [ pathSegments.get(2) ] might be used in the future
                        val namespace = pathSegments.get(3);
                        return new MockResponse().setResponseCode(200).setBody(loadConfigFor(namespace));
                    }
                    return new MockResponse().setResponseCode(404);
                }
            });
            server.start();

            mockConfigServiceUrl("http://localhost:" + server.getPort());
        }
    }

    public static void tearDownMockServer() {
        synchronized (serverLock) {
            if (isNull(server)) return;

            try {
                clear();
                server.close();
            } catch (Exception e) {
                Reporter.error("stop apollo server error", e);
            } finally {
                server = null;
            }
        }
    }

    public static void addOrModifyProperty(String namespace, String someKey, String someValue) {
        overrideProperty(namespace, someKey, someValue);
    }

    public static void deleteProperty(String namespace, String someKey) {
        overrideProperty(namespace, someKey, null);
    }

    public static void resetOverriddenProperties() {
        overriddenPropertiesOfNamespace.clear();
    }

    private static void clear() {
        resetOverriddenProperties();
    }

    private static String mockLongPollBody(String notificationsStr) {
        val oldNotifications = GSON.<List<ApolloConfigNotification>>fromJson(notificationsStr, notificationType);
        val newNotifications = new ArrayList<ApolloConfigNotification>();
        for (val notification : oldNotifications) {
            newNotifications.add(new ApolloConfigNotification(
                    notification.getNamespaceName(), notification.getNotificationId() + 1));
        }
        return GSON.toJson(newNotifications);
    }

    private static String loadConfigFor(String namespace) {
        val filename = String.format("mockdata-%s.properties", namespace);
        val prop = ResourceUtils.readConfigFile(filename, new Properties());
        val configurations = Maps.<String, String>newHashMap();
        for (val propertyName : prop.stringPropertyNames()) {
            configurations.put(propertyName, prop.getProperty(propertyName));
        }
        val apolloConfig = new ApolloConfig("someAppId", "someCluster", namespace, "someReleaseKey");
        val mergedConfigurations = mergeOverriddenProperties(namespace, configurations);
        apolloConfig.setConfigurations(mergedConfigurations);
        return GSON.toJson(apolloConfig);
    }

    private static Map<String, String> mergeOverriddenProperties(String namespace, Map<String, String> configurations) {
        if (overriddenPropertiesOfNamespace.containsKey(namespace)) {
            for (val prop : overriddenPropertiesOfNamespace.get(namespace)) {
                if (isNull(prop.getValue())) configurations.remove(prop.getKey());
                else configurations.put(prop.getKey(), prop.getValue());
            }
        }
        return configurations;
    }

    @SneakyThrows
    private static void mockConfigServiceUrl(String url) {
        System.setProperty(ApolloClientSystemConsts.APOLLO_CONFIG_SERVICE, url);
        on(configServiceLocator).call("initConfigServices");
    }

    private static void overrideProperty(String namespace, String someKey, String someValue) {
        if (overriddenPropertiesOfNamespace.containsKey(namespace)) {
            overriddenPropertiesOfNamespace.get(namespace).add(new Prop(someKey, someValue));
        } else {
            overriddenPropertiesOfNamespace.put(namespace, newArrayList(new Prop(someKey, someValue)));
        }
    }

    @AllArgsConstructor
    @Getter
    private static class Prop {

        private String key;
        private String value;
    }
}
