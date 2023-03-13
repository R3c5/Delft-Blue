package nl.tudelft.sem.resource.manager.domain.resource;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservedResourceId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "reserver", nullable = false)
    @Enumerated(EnumType.STRING)
    private Reserver reserver;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservedResourceId)) {
            return false;
        }
        ReservedResourceId that = (ReservedResourceId) o;
        return Objects.equals(date, that.date) && reserver == that.reserver;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, reserver);
    }
}
