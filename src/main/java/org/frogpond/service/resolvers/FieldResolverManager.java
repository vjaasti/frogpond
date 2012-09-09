package org.frogpond.service.resolvers;

import org.frogpond.model.Primitive;

public interface FieldResolverManager {

    FieldResolver getResolver(Primitive primitive);

}
