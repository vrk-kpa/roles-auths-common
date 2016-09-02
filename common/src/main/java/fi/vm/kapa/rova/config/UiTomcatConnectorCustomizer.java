package fi.vm.kapa.rova.config;

import fi.vm.kapa.rova.logging.Logger;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UiTomcatConnectorCustomizer implements TomcatConnectorCustomizer {

    private static final Logger LOG = Logger.getLogger(UiTomcatConnectorCustomizer.class);

    private Environment env;

    private static final Set<String> schemes = new HashSet<String>(Arrays.asList(new String[] { "http", "https" })); // NOSONAR

    private static final String CONN_SECURE_PROPERTY = "tomcat_connector_secure";

    private static final String CONN_SCHEME_PROPERTY = "tomcat_connector_scheme";
    private static final String CONN_PROXY_NAME_PROPERTY = "tomcat_connector_proxy_name";
    private static final String CONN_PROXY_PORT_PROPERTY = "tomcat_connector_proxy_port";

    private static final String CONN_SCHEME_DEFAULT = "http";
    private static final String CONN_PROXY_NAME_DEFAULT = "localhost";
    private static final int CONN_PROXY_PORT_DEFAULT = 8080;

    public UiTomcatConnectorCustomizer(Environment env) {
        this.env = env;
    }

    private boolean isBlank(String property) {
        String str = env.getProperty(property);
        return str != null && "".equals(str.trim());
    }

    @Override
    public void customize(Connector connector) {

        // If properties are set intentionally blank, Connector default
        // values are used.

        if (!isBlank(CONN_SECURE_PROPERTY)) {
            String secureSetting = env.getProperty(CONN_SECURE_PROPERTY);
            boolean secure = Boolean.parseBoolean(secureSetting);
            connector.setSecure(secure);
        }

        // Tomcat connector scheme
        if (!isBlank(CONN_SCHEME_PROPERTY)) {
            String scheme = env.getProperty(CONN_SCHEME_PROPERTY);
            if (scheme == null || !schemes.contains(scheme)) {
                LOG.warning("Scheme incorrectly configured in '" + CONN_SCHEME_PROPERTY + "'. Using ' '.");
                scheme = CONN_SCHEME_DEFAULT;
            }
            LOG.info("Setting connector scheme: " + scheme);
            connector.setScheme(scheme);
        }

        // Tomcat connector proxy port
        if (!isBlank(CONN_PROXY_PORT_PROPERTY)) {
            int port = CONN_PROXY_PORT_DEFAULT;
            try {
                port = Integer.parseInt(env.getProperty(CONN_PROXY_PORT_PROPERTY));
            } catch (NumberFormatException nfe) {
                LOG.warning("Proxy port not properly set in '" + CONN_PROXY_PORT_PROPERTY + "'. Using "
                        + CONN_PROXY_PORT_DEFAULT + ".", nfe);
            }
            LOG.info("Setting connector proxy port: " + port);
            connector.setProxyPort(port);
        }

        // Tomcat connector proxy name
        if (!isBlank(CONN_PROXY_NAME_PROPERTY)) {
            String name = env.getProperty(CONN_PROXY_NAME_PROPERTY);
            if (name == null) {
                name = CONN_PROXY_NAME_DEFAULT;
                LOG.warning("Proxy name not properly set in '" + CONN_PROXY_NAME_PROPERTY + "'. Using '"
                        + CONN_PROXY_NAME_DEFAULT + "'.");
            }
            LOG.info("Setting connector proxy name: " + name);
            connector.setProxyName(name);
        }
    }

}
