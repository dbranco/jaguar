package com.github.dbranco.jaguar.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JdkClassification {

    private String os;
    private String architecture;
    private List<JdkArchive> archives;
    
    @Data
    @NoArgsConstructor
    public static class JdkArchive {
        private String type;
        private String version;
        private String url;
    }
}
