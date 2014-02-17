package org.jboss.as.console.testsuite.tests.util;

import org.jboss.arquillian.container.test.impl.enricher.resource.URLResourceProvider;
import org.jboss.arquillian.test.api.ArquillianResource;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jcechace on 22/02/14.
 */
public class ConsoleURLResourceProvider extends URLResourceProvider {
    @Override
    public boolean canProvide(Class<?> type) {
        return super.canProvide(type);
    }

    @Override
    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        URL url = (URL) super.lookup(resource, qualifiers);

        if (url != null) {
            return url;
        }

        try {
            String protocol = ConfigUtils.getProtocol();
            // TODO: place authentication hack here
            String host = ConfigUtils.getHost();
            int port = ConfigUtils.getPort();
            String context = ConfigUtils.getContext();

            return new URL(protocol, host, port, context);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
}
