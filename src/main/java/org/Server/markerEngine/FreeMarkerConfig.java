package org.Server.markerEngine;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;



public class FreeMarkerConfig {
    private static Configuration configuration;
    public static Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();
            configuration.setClassForTemplateLoading(FreeMarkerConfig.class, "/");
            configuration.setDefaultEncoding("UTF-8");
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        }
        return configuration;
    }
}
