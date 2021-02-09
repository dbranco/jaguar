package com.github.dbranco.jaguar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "jaguar")
/**
 * The Jaguar application properties.
 */
public class JaguarConfigurationProperties {

    /**
     * The properties for configuring the "list" functionality
     */
    private ListProperties list;

    @Data
    @NoArgsConstructor
    public static class ListProperties {

        /**
         * The URL from where the list.json is retrieved
         */
        private String url;
    }
    
}
