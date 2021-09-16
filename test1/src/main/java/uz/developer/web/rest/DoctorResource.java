package uz.developer.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.developer.domain.Doctor;
import uz.developer.repository.DoctorRepository;
import uz.developer.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.developer.domain.Doctor}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class DoctorResource {

    private final Logger log = LoggerFactory.getLogger(DoctorResource.class);

    private static final String ENTITY_NAME = "doctor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DoctorRepository doctorRepository;

    public DoctorResource(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * {@code POST  /doctors} : Create a new doctor.
     *
     * @param doctor the doctor to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new doctor, or with status {@code 400 (Bad Request)} if the doctor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/doctors")
    public ResponseEntity<Doctor> createDoctor(@Valid @RequestBody Doctor doctor) throws URISyntaxException {
        log.debug("REST request to save Doctor : {}", doctor);
        if (doctor.getId() != null) {
            throw new BadRequestAlertException("A new doctor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Doctor result = doctorRepository.save(doctor);
        return ResponseEntity
            .created(new URI("/api/doctors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /doctors/:id} : Updates an existing doctor.
     *
     * @param id the id of the doctor to save.
     * @param doctor the doctor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctor,
     * or with status {@code 400 (Bad Request)} if the doctor is not valid,
     * or with status {@code 500 (Internal Server Error)} if the doctor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/doctors/{id}")
    public ResponseEntity<Doctor> updateDoctor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Doctor doctor
    ) throws URISyntaxException {
        log.debug("REST request to update Doctor : {}, {}", id, doctor);
        if (doctor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!doctorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Doctor result = doctorRepository.save(doctor);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, doctor.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /doctors/:id} : Partial updates given fields of an existing doctor, field will ignore if it is null
     *
     * @param id the id of the doctor to save.
     * @param doctor the doctor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctor,
     * or with status {@code 400 (Bad Request)} if the doctor is not valid,
     * or with status {@code 404 (Not Found)} if the doctor is not found,
     * or with status {@code 500 (Internal Server Error)} if the doctor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/doctors/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Doctor> partialUpdateDoctor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Doctor doctor
    ) throws URISyntaxException {
        log.debug("REST request to partial update Doctor partially : {}, {}", id, doctor);
        if (doctor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!doctorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Doctor> result = doctorRepository
            .findById(doctor.getId())
            .map(
                existingDoctor -> {
                    if (doctor.getDoctor() != null) {
                        existingDoctor.setDoctor(doctor.getDoctor());
                    }

                    return existingDoctor;
                }
            )
            .map(doctorRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, doctor.getId().toString())
        );
    }

    /**
     * {@code GET  /doctors} : get all the doctors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of doctors in body.
     */
    @GetMapping("/doctors")
    public List<Doctor> getAllDoctors() {
        log.debug("REST request to get all Doctors");
        return doctorRepository.findAll();
    }

    /**
     * {@code GET  /doctors/:id} : get the "id" doctor.
     *
     * @param id the id of the doctor to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the doctor, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/doctors/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id) {
        log.debug("REST request to get Doctor : {}", id);
        Optional<Doctor> doctor = doctorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(doctor);
    }

    /**
     * {@code DELETE  /doctors/:id} : delete the "id" doctor.
     *
     * @param id the id of the doctor to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        log.debug("REST request to delete Doctor : {}", id);
        doctorRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
