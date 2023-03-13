package nl.tudelft.sem.template.user.domain.user;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Converter for the Faculty Enum.
 */
@Converter
public class FacultyAttributeConverter implements AttributeConverter<List<FacultyName>, String> {

    @Override
    public String convertToDatabaseColumn(List<FacultyName> attribute) {
        String faculties = attribute.toString();
        faculties = faculties.replace("[", "");
        faculties = faculties.replace("]", "");
        return faculties;
    }

    @Override
    public List<FacultyName> convertToEntityAttribute(String dbData) {
        String[] facultyList = dbData.split(", ");
        List<FacultyName> faculty = new ArrayList<>();
        for (String f : facultyList) {
            faculty.add(FacultyName.valueOf(f));
        }
        return faculty;
    }

}

