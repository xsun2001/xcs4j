package io.xsun.xcs4j.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Token {
    public final String name;
    public final ITokenAttribute<?> attribute;

    private static final Map<String, Map<ITokenAttribute<?>, Token>> TOKEN_CACHE = new HashMap<>();

    private Token(String name, ITokenAttribute<?> attribute) {
        this.name = name;
        this.attribute = attribute;
    }

    public static final Token of(String name, ITokenAttribute<?> attr) {
        return findCache(name, attr).orElseGet(() -> constructAndPushToCache(name, attr));
    }

    private static final Optional<Token> findCache(String name, ITokenAttribute<?> attr) {
        if (TOKEN_CACHE.containsKey(name)) {
            var attrMap = TOKEN_CACHE.get(name);
            return Optional.ofNullable(attrMap.get(attr));
        } else {
            TOKEN_CACHE.put(name, new HashMap<>());
            return Optional.empty();
        }
    }

    private static final Token constructAndPushToCache(String name, ITokenAttribute<?> attr) {
        return TOKEN_CACHE.get(name).put(attr, new Token(name, attr));
    }
}
