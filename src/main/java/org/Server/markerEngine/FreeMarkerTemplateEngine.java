package org.Server.markerEngine;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class FreeMarkerTemplateEngine {
    public String render(String templateName, Map<String, Object> dataModel) throws IOException, TemplateException {
        Template template = FreeMarkerConfig.getConfiguration().getTemplate(templateName);
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }
}
