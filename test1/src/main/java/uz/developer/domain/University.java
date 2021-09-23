package uz.developer.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A University.
 */
@Entity
@Table(name = "university")
public class University implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "room")
    private String room;

    @Column(name = "name", unique = true)
    private String name;

    @Size(min = 1)
    @Column(name = "faculty", unique = true)
    private String faculty;

    @ManyToOne
    private Region region;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public University id(Long id) {
        this.id = id;
        return this;
    }

    public String getRoom() {
        return this.room;
    }

    public University room(String room) {
        this.room = room;
        return this;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getName() {
        return this.name;
    }

    public University name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaculty() {
        return this.faculty;
    }

    public University faculty(String faculty) {
        this.faculty = faculty;
        return this;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public Region getRegion() {
        return this.region;
    }

    public University region(Region region) {
        this.setRegion(region);
        return this;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof University)) {
            return false;
        }
        return id != null && id.equals(((University) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "University{" +
            "id=" + getId() +
            ", room='" + getRoom() + "'" +
            ", name='" + getName() + "'" +
            ", faculty='" + getFaculty() + "'" +
            "}";
    }
}
