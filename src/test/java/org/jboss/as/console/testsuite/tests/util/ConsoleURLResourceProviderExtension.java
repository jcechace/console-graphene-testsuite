package org.jboss.as.console.testsuite.tests.util;

import org.jboss.arquillian.container.test.impl.enricher.resource.URLResourceProvider;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 * Created by jcechace on 22/02/14.
 */
public class ConsoleURLResourceProviderExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.override(ResourceProvider.class, URLResourceProvider.class, ConsoleURLResourceProvider.class);
    }
}
