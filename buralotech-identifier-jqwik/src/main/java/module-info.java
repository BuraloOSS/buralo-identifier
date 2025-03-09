module buralotech.identifier.jqwik {
    exports com.buralotech.oss.identifier.jqwik.api;
    exports com.buralotech.oss.identifier.jqwik.impl;
    requires buralotech.identifier.core;
    requires net.jqwik.api;
    provides net.jqwik.api.providers.ArbitraryProvider with com.buralotech.oss.identifier.jqwik.impl.IdentifierArbitraryProvider;
}