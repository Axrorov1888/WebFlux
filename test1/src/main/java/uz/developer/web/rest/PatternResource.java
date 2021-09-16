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
import uz.developer.domain.Pattern;
import uz.developer.repository.PatternRepository;
import uz.developer.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.developer.domain.Pattern}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PatternResource {

    private final Logger log = LoggerFactory.getLogger(PatternResource.class);

    private static final String ENTITY_NAME = "pattern";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PatternRepository patternRepository;

    public PatternResource(PatternRepository patternRepository) {
        this.patternRepository = patternRepository;
    }

    /**
     * {@code POST  /patterns} : Create a new pattern.
     *
     * @param pattern the pattern to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pattern, or with status {@code 400 (Bad Request)} if the pattern has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/patterns")
    public ResponseEntity<Pattern> createPattern(@Valid @RequestBody Pattern pattern) throws URISyntaxException {
        log.debug("REST request to save Pattern : {}", pattern);
        if (pattern.getId() != null) {
            throw new BadRequestAlertException("A new pattern cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Pattern result = patternRepository.save(pattern);
        return ResponseEntity
            .created(new URI("/api/patterns/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /patterns/:id} : Updates an existing pattern.
     *
     * @param id the id of the pattern to save.
     * @param pattern the pattern to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pattern,
     * or with status {@code 400 (Bad Request)} if the pattern is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pattern couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/patterns/{id}")
    public ResponseEntity<Pattern> updatePattern(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Pattern pattern
    ) throws URISyntaxException {
        log.debug("REST request to update Pattern : {}, {}", id, pattern);
        if (pattern.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pattern.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!patternRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Pattern result = patternRepository.save(pattern);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pattern.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /patterns/:id} : Partial updates given fields of an existing pattern, field will ignore if it is null
     *
     * @param id the id of the pattern to save.
     * @param pattern the pattern to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pattern,
     * or with status {@code 400 (Bad Request)} if the pattern is not valid,
     * or with status {@code 404 (Not Found)} if the pattern is not found,
     * or with status {@code 500 (Internal Server Error)} if the pattern couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/patterns/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Pattern> partialUpdatePattern(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Pattern pattern
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pattern partially : {}, {}", id, pattern);
        if (pattern.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pattern.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!patternRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Pattern> result = patternRepository
            .findById(pattern.getId())
            .map(
                existingPattern -> {
                    if (pattern.getPattern() != null) {
                        existingPattern.setPattern(pattern.getPattern());
                    }

                    return existingPattern;
                }
            )
            .map(patternRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pattern.getId().toString())
        );
    }

    /**
     * {@code GET  /patterns} : get all the patterns.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of patterns in body.
     */
    @GetMapping("/patterns")
    public List<Pattern> getAllPatterns() {
        log.debug("REST request to get all Patterns");
        return patternRepository.findAll();
    }

    /**
     * {@code GET  /patterns/:id} : get the "id" pattern.
     *
     * @param id the id of the pattern to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pattern, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/patterns/{id}")
    public ResponseEntity<Pattern> getPattern(@PathVariable Long id) {
        log.debug("REST request to get Pattern : {}", id);
        Optional<Pattern> pattern = patternRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pattern);
    }

    /**
     * {@code DELETE  /patterns/:id} : delete the "id" pattern.
     *
     * @param id the id of the pattern to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/patterns/{id}")
    public ResponseEntity<Void> deletePattern(@PathVariable Long id) {
        log.debug("REST request to delete Pattern : {}", id);
        patternRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
