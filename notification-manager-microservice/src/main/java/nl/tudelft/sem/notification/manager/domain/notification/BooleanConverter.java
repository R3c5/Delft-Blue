package nl.tudelft.sem.notification.manager.domain.notification;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the NetID value object.
 */
@Converter
public class BooleanConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return attribute.toString();
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return dbData.equals("true");
    }

}

