package uz.developer.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Region.
 */
@Entity
@Table(name = "region")
public class Region implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Size(min = 2)
    @Column(name = "region")
    private String region;

    @Column(name = "student")
    private String student;

    @Column(name = "teacher")
    private String teacher;

    @Column(name = "city")
    private String city;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Region id(Long id) {
        this.id = id;
        return this;
    }

    public String getRegion() {
        return this.region;
    }

    public Region region(String region) {
        this.region = region;
        return this;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getStudent() {
        return this.student;
    }

    public Region student(String student) {
        this.student = student;
        return this;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getTeacher() {
        return this.teacher;
    }

    public Region teacher(String teacher) {
        this.teacher = teacher;
        return this;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getCity() {
        return this.city;
    }

    public Region city(String city) {
        this.city = city;
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Region)) {
            return false;
        }
        return id != null && id.equals(((Region) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Region{" +
            "id=" + getId() +
            ", region='" + getRegion() + "'" +
            ", student='" + getStudent() + "'" +
            ", teacher='" + getTeacher() + "'" +
            ", city='" + getCity() + "'" +
            "}";
    }
}
