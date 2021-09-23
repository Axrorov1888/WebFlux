package uz.developer.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.developer.domain.DEV;
import uz.developer.repository.DEVRepository;
import uz.developer.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.developer.domain.DEV}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class DEVResource {

    private final Logger log = LoggerFactory.getLogger(DEVResource.class);

    private static final String ENTITY_NAME = "dEV";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DEVRepository dEVRepository;

    public DEVResource(DEVRepository dEVRepository) {
        this.dEVRepository = dEVRepository;
    }

    /**
     * {@code POST  /devs} : Create a new dEV.
     *
     * @param dEV the dEV to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dEV, or with status {@code 400 (Bad Request)} if the dEV has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/devs")
    public ResponseEntity<DEV> createDEV(@RequestBody DEV dEV) throws URISyntaxException {
        log.debug("REST request to save DEV : {}", dEV);
        if (dEV.getId() != null) {
            throw new BadRequestAlertException("A new dEV cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DEV result = dEVRepository.save(dEV);
        return ResponseEntity
            .created(new URI("/api/devs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /devs/:id} : Updates an existing dEV.
     *
     * @param id the id of the dEV to save.
     * @param dEV the dEV to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dEV,
     * or with status {@code 400 (Bad Request)} if the dEV is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dEV couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/devs/{id}")
    public ResponseEntity<DEV> updateDEV(@PathVariable(value = "id", required = false) final Long id, @RequestBody DEV dEV)
        throws URISyntaxException {
        log.debug("REST request to update DEV : {}, {}", id, dEV);
        if (dEV.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dEV.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dEVRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DEV result = dEVRepository.save(dEV);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, dEV.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /devs/:id} : Partial updates given fields of an existing dEV, field will ignore if it is null
     *
     * @param id the id of the dEV to save.
     * @param dEV the dEV to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dEV,
     * or with status {@code 400 (Bad Request)} if the dEV is not valid,
     * or with status {@code 404 (Not Found)} if the dEV is not found,
     * or with status {@code 500 (Internal Server Error)} if the dEV couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/devs/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<DEV> partialUpdateDEV(@PathVariable(value = "id", required = false) final Long id, @RequestBody DEV dEV)
        throws URISyntaxException {
        log.debug("REST request to partial update DEV partially : {}, {}", id, dEV);
        if (dEV.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dEV.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dEVRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DEV> result = dEVRepository
            .findById(dEV.getId())
            .map(
                existingDEV -> {
                    if (dEV.getName() != null) {
                        existingDEV.setName(dEV.getName());
                    }

                    return existingDEV;
                }
            )
            .map(dEVRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, dEV.getId().toString())
        );
    }

    /**
     * {@code GET  /devs} : get all the dEVS.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dEVS in body.
     */
    @GetMapping("/devs")
    public List<DEV> getAllDEVS() {
        log.debug("REST request to get all DEVS");
        return dEVRepository.findAll();
    }

    /**
     * {@code GET  /devs/:id} : get the "id" dEV.
     *
     * @param id the id of the dEV to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dEV, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/devs/{id}")
    public ResponseEntity<DEV> getDEV(@PathVariable Long id) {
        log.debug("REST request to get DEV : {}", id);
        Optional<DEV> dEV = dEVRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(dEV);
    }

    /**
     * {@code DELETE  /devs/:id} : delete the "id" dEV.
     *
     * @param id the id of the dEV to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/devs/{id}")
    public ResponseEntity<Void> deleteDEV(@PathVariable Long id) {
        log.debug("REST request to delete DEV : {}", id);
        dEVRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
