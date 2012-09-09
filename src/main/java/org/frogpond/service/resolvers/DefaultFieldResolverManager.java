package org.frogpond.service.resolvers;

import org.frogpond.model.Primitive;

import java.util.HashMap;
import java.util.Map;

public class DefaultFieldResolverManager implements FieldResolverManager {
    private FieldResolver defaultResolver;
    private Map<Primitive, FieldResolver> resolvers = new HashMap<Primitive, FieldResolver>();

    @Override
    public FieldResolver getResolver(Primitive primitive) {
        FieldResolver result = resolvers.get(primitive);

        return (result == null) ? defaultResolver : result;
    }

    public FieldResolver getDefaultResolver() {
        return defaultResolver;
    }

    public void setDefaultResolver(FieldResolver defaultResolver) {
        this.defaultResolver = defaultResolver;
    }

    public Map<Primitive, FieldResolver> getResolvers() {
        return resolvers;
    }

    public void setResolvers(Map<Primitive, FieldResolver> resolvers) {
        this.resolvers = resolvers;
    }
}
