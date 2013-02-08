/*
 * Copyright 2012 Alexey Hanin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package getrest.android.jaxrs;

import getrest.android.core.Loggers;

import getrest.android.exception.GetrestConfigurationException;

import getrest.android.util.Logger;

import java.lang.reflect.Method;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

public class JaxrsApplicationFactory {

    private static final Logger logger = Loggers.getConfigLogger();
    private final Class<?extends Application> applicationClass;

    public JaxrsApplicationFactory(final Class<?extends Application> applicationClass) {
        this.applicationClass = applicationClass;
    }

    public void create() {
        logger.debug("Configure JSR311 application: " + applicationClass);

        final Application application;

        try {
            application = applicationClass.newInstance();
        } catch (final Exception e) {
            throw new GetrestConfigurationException(
                "Unable to instantiate application class: " + applicationClass.getName(),
                e);
        }

        for (final Class<?> aClass : application.getClasses()) {

            final Provider provider = aClass.getAnnotation(Provider.class);

            if (provider != null) {
                configureProvider(aClass, provider);
            }

            final Path rootPath = aClass.getAnnotation(Path.class);

            if (rootPath != null) {
                logger.debug("Root path found: " + rootPath.value());
            }

            final Method[] methods = aClass.getMethods();

            for (final Method method : methods) {

                final Path path = method.getAnnotation(Path.class);

                logger.debug("Resource method found: {0}({1})",
                             method.getName(),
                             toParameterTypeString(method.getParameterTypes()));
            }
        }
    }

    private void configureProvider(final Class<?> providerClass, final Provider provider) {
        logger.debug("Provider found: {0}", providerClass.getName());

        if (MessageBodyReader.class.isAssignableFrom(providerClass)) {

            // TODO wire-up reader
        }

        if (MessageBodyWriter.class.isAssignableFrom(providerClass)) {

            // TODO wire-up writer
        }
    }

    private String toParameterTypeString(final Class<?>[] types) {

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < types.length; i++) {
            sb.append(types[i].getSimpleName());

            if (i < (types.length - 1)) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }
}
