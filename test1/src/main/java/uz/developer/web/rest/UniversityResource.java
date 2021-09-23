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
import uz.developer.domain.University;
import uz.developer.repository.UniversityRepository;
import uz.developer.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.developer.domain.University}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UniversityResource {

    private final Logger log = LoggerFactory.getLogger(UniversityResource.class);

    private static final String ENTITY_NAME = "university";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UniversityRepository universityRepository;

    public UniversityResource(UniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }

    /**
     * {@code POST  /universities} : Create a new university.
     *
     * @param university the university to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new university, or with status {@code 400 (Bad Request)} if the university has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/universities")
    public ResponseEntity<University> createUniversity(@Valid @RequestBody University university) throws URISyntaxException {
        log.debug("REST request to save University : {}", university);
        if (university.getId() != null) {
            throw new BadRequestAlertException("A new university cannot already have an ID", ENTITY_NAME, "idexists");
        }
        University result = universityRepository.save(university);
        return ResponseEntity
            .created(new URI("/api/universities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /universities/:id} : Updates an existing university.
     *
     * @param id the id of the university to save.
     * @param university the university to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated university,
     * or with status {@code 400 (Bad Request)} if the university is not valid,
     * or with status {@code 500 (Internal Server Error)} if the university couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/universities/{id}")
    public ResponseEntity<University> updateUniversity(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody University university
    ) throws URISyntaxException {
        log.debug("REST request to update University : {}, {}", id, university);
        if (university.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, university.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!universityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        University result = universityRepository.save(university);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, university.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /universities/:id} : Partial updates given fields of an existing university, field will ignore if it is null
     *
     * @param id the id of the university to save.
     * @param university the university to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated university,
     * or with status {@code 400 (Bad Request)} if the university is not valid,
     * or with status {@code 404 (Not Found)} if the university is not found,
     * or with status {@code 500 (Internal Server Error)} if the university couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/universities/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<University> partialUpdateUniversity(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody University university
    ) throws URISyntaxException {
        log.debug("REST request to partial update University partially : {}, {}", id, university);
        if (university.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, university.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!universityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<University> result = universityRepository
            .findById(university.getId())
            .map(
                existingUniversity -> {
                    if (university.getRoom() != null) {
                        existingUniversity.setRoom(university.getRoom());
                    }
                    if (university.getName() != null) {
                        existingUniversity.setName(university.getName());
                    }
                    if (university.getFaculty() != null) {
                        existingUniversity.setFaculty(university.getFaculty());
                    }

                    return existingUniversity;
                }
            )
            .map(universityRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, university.getId().toString())
        );
    }

    /**
     * {@code GET  /universities} : get all the universities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of universities in body.
     */
    @GetMapping("/universities")
    public List<University> getAllUniversities() {
        log.debug("REST request to get all Universities");
        return universityRepository.findAll();
    }

    /**
     * {@code GET  /universities/:id} : get the "id" university.
     *
     * @param id the id of the university to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the university, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/universities/{id}")
    public ResponseEntity<University> getUniversity(@PathVariable Long id) {
        log.debug("REST request to get University : {}", id);
        Optional<University> university = universityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(university);
    }

    /**
     * {@code DELETE  /universities/:id} : delete the "id" university.
     *
     * @param id the id of the university to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/universities/{id}")
    public ResponseEntity<Void> deleteUniversity(@PathVariable Long id) {
        log.debug("REST request to delete University : {}", id);
        universityRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
