package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The type Date dto.
 */
@Data
public class DateDTO {

    private int year;
    private int month;
    private int day;

    /**
     * Instantiates a new Date dto.
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     */
    public DateDTO(
            @JsonProperty("year") int year,
            @JsonProperty("month") int month,
            @JsonProperty("day") int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
