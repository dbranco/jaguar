package com.github.dbranco.jaguar.service;

import java.util.List;

/**
 * The service used to list the JDKs
 */
public interface JdkListService<T> {
    
    /**
     * Fetch the JDKs list
     */
    List<T> list();

    /**
     * Print the list to the console
     * 
     * @param theOutputToRender the list to be printed
     */
    void print(List<T> theOutputToRender);

    /**
     * Fetch the JDKs list and print it out.
     */
    default void listAndPrint() {
        print(list());
    }

}
