package nl.tudelft.sem.resource.manager.domain.node.converters;

import sem.commons.URL;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class URLConverter implements AttributeConverter<URL, String> {
    @Override
    public String convertToDatabaseColumn(URL attribute) {
        return attribute.toString();
    }

    @Override
    public URL convertToEntityAttribute(String dbData) {
        return new URL(dbData);
    }
}
