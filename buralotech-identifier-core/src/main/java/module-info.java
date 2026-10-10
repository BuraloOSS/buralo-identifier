module buralotech.identifier.core {
    requires com.fasterxml.uuid;
    requires static org.jspecify;
    requires io.netty.buffer;
    exports com.buralotech.oss.identifier.api;
    exports com.buralotech.oss.identifier.uuid;
}