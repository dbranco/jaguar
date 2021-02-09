package com.github.dbranco.jaguar.service;

/**
 * The service used to list the JDKs
 */
public interface JdkListService {
    
    /**
     * List the JDKs
     */
    default void list() {
        throw new UnsupportedOperationException();
    }

}
