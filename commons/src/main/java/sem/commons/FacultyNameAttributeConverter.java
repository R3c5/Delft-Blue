package sem.commons;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA Converter for the FacultyName Enum in commons.
 */
@Converter
public class FacultyNameAttributeConverter implements AttributeConverter<FacultyName, String> {

    @Override
    public String convertToDatabaseColumn(FacultyName attribute) {
        String faculties = attribute.toString();
        faculties = faculties.replace("[", "");
        faculties = faculties.replace("]", "");
        return faculties;
    }

    @Override
    public FacultyName convertToEntityAttribute(String dbData) {
        return FacultyName.valueOf(dbData);
    }

}

