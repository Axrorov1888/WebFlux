package uz.developer.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Doctor.
 */
@Entity
@Table(name = "doctor")
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Size(min = 1, max = 20)
    @Column(name = "doctor", length = 20, unique = true)
    private String doctor;

    @ManyToOne
    private Admin admin;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor id(Long id) {
        this.id = id;
        return this;
    }

    public String getDoctor() {
        return this.doctor;
    }

    public Doctor doctor(String doctor) {
        this.doctor = doctor;
        return this;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public Admin getAdmin() {
        return this.admin;
    }

    public Doctor admin(Admin admin) {
        this.setAdmin(admin);
        return this;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Doctor)) {
            return false;
        }
        return id != null && id.equals(((Doctor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Doctor{" +
            "id=" + getId() +
            ", doctor='" + getDoctor() + "'" +
            "}";
    }
}
