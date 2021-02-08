package com.github.dbranco.jaguar.service;

public interface JdkListService {
    
    default void list() {
        throw new UnsupportedOperationException();
    }

}
