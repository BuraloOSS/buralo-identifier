module buralotech.identifier.spring {
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires buralotech.identifier.core;
    exports com.buralotech.oss.identifier.spring;
    opens com.buralotech.oss.identifier.spring to spring.core;
}