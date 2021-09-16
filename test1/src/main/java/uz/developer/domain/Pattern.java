package uz.developer.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Pattern.
 */
@Entity
@Table(name = "pattern")
public class Pattern implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 2)
    @Column(name = "pattern", nullable = false)
    private String pattern;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pattern id(Long id) {
        this.id = id;
        return this;
    }

    public String getPattern() {
        return this.pattern;
    }

    public Pattern pattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pattern)) {
            return false;
        }
        return id != null && id.equals(((Pattern) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pattern{" +
            "id=" + getId() +
            ", pattern='" + getPattern() + "'" +
            "}";
    }
}
