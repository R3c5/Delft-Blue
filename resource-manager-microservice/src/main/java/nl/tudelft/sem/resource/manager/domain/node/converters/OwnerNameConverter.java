package nl.tudelft.sem.resource.manager.domain.node.converters;

import sem.commons.OwnerName;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * A JPA converter for the OwnerName value object.
 */

@Converter
public class OwnerNameConverter implements AttributeConverter<OwnerName, String> {
    @Override
    public String convertToDatabaseColumn(OwnerName attribute) {
        return attribute.toString();
    }

    @Override
    public OwnerName convertToEntityAttribute(String dbData) {
        return new OwnerName(dbData);
    }
}
