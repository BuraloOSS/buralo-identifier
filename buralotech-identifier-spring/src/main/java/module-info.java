module buralotech.identifier.spring {
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires buralotech.identifier.core;
    requires spring.boot.jackson;
    requires tools.jackson.databind;
    requires static org.jspecify;
    requires spring.boot;
    exports com.buralotech.oss.identifier.spring;
    opens com.buralotech.oss.identifier.spring to spring.core;
}