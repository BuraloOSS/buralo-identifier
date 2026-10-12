module buralotech.identifier.spring {
    requires buralotech.identifier.core;
    requires org.apache.commons.logging;
    requires spring.context;
    requires spring.core;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.boot.jackson;
    requires tools.jackson.databind;
    requires static io.netty.buffer;
    requires static org.jspecify;
    requires static org.reactivestreams;
    requires static r2dbc.spi;
    requires static r2dbc.postgresql;
    requires static reactor.core;
    requires static spring.boot.r2dbc;
    exports com.buralotech.oss.identifier.spring;
    opens com.buralotech.oss.identifier.spring to spring.core;
}