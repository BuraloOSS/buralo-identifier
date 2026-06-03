/*
 *  Copyright 2026 Búraló Technologies
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.buralotech.oss.identifier.quarkus;

import com.buralotech.oss.identifier.api.Identifier;
import com.buralotech.oss.identifier.api.IdentifierService;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * This provider acts as a factory for creating the {@link IdentifierParamConverter}.
 */
@Provider
public class IdentifierParamConverterProvider implements ParamConverterProvider {

    /**
     * The identifier service is used to parse string representation of {@link Identifier}.
     */
    private final IdentifierService identifierService;

    /**
     * Initialize the provider by injecting the identifier service.
     *
     * @param identifierService The identifier service.
     */
    public IdentifierParamConverterProvider(IdentifierService identifierService) {
        this.identifierService = identifierService;
    }

    /**
     * This is the factory method creates the converter.
     *
     * @param rawType     The target type.
     * @param genericType The source type.
     * @param annotations Annotations on the target.
     * @param <T>         Generic type.
     * @return The parameter converter.
     */
    @Override
    public <T> @Nullable ParamConverter<T> getConverter(final Class<T> rawType,
                                                        final Type genericType,
                                                        final Annotation[] annotations) {
        if (Identifier.class.equals(rawType)) {
            return (ParamConverter<T>) new IdentifierParamConverter(identifierService);
        }
        return null;
    }
}