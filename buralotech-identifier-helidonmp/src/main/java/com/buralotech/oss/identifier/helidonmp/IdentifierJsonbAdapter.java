package com.buralotech.oss.identifier.helidonmp;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.api.IdentifierService;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.ws.rs.ext.Provider;
import org.jspecify.annotations.Nullable;

@Provider
public class IdentifierJsonbAdapter implements JsonbAdapter<Identifier, String> {

    private final IdentifierService identifierService;

    public IdentifierJsonbAdapter(final IdentifierService identifierService) {
        this.identifierService = identifierService;
    }

    @Override
    public @Nullable String adaptToJson(@Nullable final Identifier id) throws Exception {
        return id == null ? null : id.text();
    }

    @Override
    public @Nullable Identifier adaptFromJson(@Nullable final String text) throws Exception {
        return text == null ? null : identifierService.fromText(text);
    }
}
