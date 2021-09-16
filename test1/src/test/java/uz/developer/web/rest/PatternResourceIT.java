package uz.developer.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.developer.IntegrationTest;
import uz.developer.domain.Pattern;
import uz.developer.repository.PatternRepository;

/**
 * Integration tests for the {@link PatternResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PatternResourceIT {

    private static final String DEFAULT_PATTERN = "AAAAAAAAAA";
    private static final String UPDATED_PATTERN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/patterns";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PatternRepository patternRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPatternMockMvc;

    private Pattern pattern;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pattern createEntity(EntityManager em) {
        Pattern pattern = new Pattern().pattern(DEFAULT_PATTERN);
        return pattern;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pattern createUpdatedEntity(EntityManager em) {
        Pattern pattern = new Pattern().pattern(UPDATED_PATTERN);
        return pattern;
    }

    @BeforeEach
    public void initTest() {
        pattern = createEntity(em);
    }

    @Test
    @Transactional
    void createPattern() throws Exception {
        int databaseSizeBeforeCreate = patternRepository.findAll().size();
        // Create the Pattern
        restPatternMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pattern)))
            .andExpect(status().isCreated());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeCreate + 1);
        Pattern testPattern = patternList.get(patternList.size() - 1);
        assertThat(testPattern.getPattern()).isEqualTo(DEFAULT_PATTERN);
    }

    @Test
    @Transactional
    void createPatternWithExistingId() throws Exception {
        // Create the Pattern with an existing ID
        pattern.setId(1L);

        int databaseSizeBeforeCreate = patternRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPatternMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pattern)))
            .andExpect(status().isBadRequest());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPatternIsRequired() throws Exception {
        int databaseSizeBeforeTest = patternRepository.findAll().size();
        // set the field null
        pattern.setPattern(null);

        // Create the Pattern, which fails.

        restPatternMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pattern)))
            .andExpect(status().isBadRequest());

        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPatterns() throws Exception {
        // Initialize the database
        patternRepository.saveAndFlush(pattern);

        // Get all the patternList
        restPatternMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pattern.getId().intValue())))
            .andExpect(jsonPath("$.[*].pattern").value(hasItem(DEFAULT_PATTERN)));
    }

    @Test
    @Transactional
    void getPattern() throws Exception {
        // Initialize the database
        patternRepository.saveAndFlush(pattern);

        // Get the pattern
        restPatternMockMvc
            .perform(get(ENTITY_API_URL_ID, pattern.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pattern.getId().intValue()))
            .andExpect(jsonPath("$.pattern").value(DEFAULT_PATTERN));
    }

    @Test
    @Transactional
    void getNonExistingPattern() throws Exception {
        // Get the pattern
        restPatternMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPattern() throws Exception {
        // Initialize the database
        patternRepository.saveAndFlush(pattern);

        int databaseSizeBeforeUpdate = patternRepository.findAll().size();

        // Update the pattern
        Pattern updatedPattern = patternRepository.findById(pattern.getId()).get();
        // Disconnect from session so that the updates on updatedPattern are not directly saved in db
        em.detach(updatedPattern);
        updatedPattern.pattern(UPDATED_PATTERN);

        restPatternMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPattern.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPattern))
            )
            .andExpect(status().isOk());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeUpdate);
        Pattern testPattern = patternList.get(patternList.size() - 1);
        assertThat(testPattern.getPattern()).isEqualTo(UPDATED_PATTERN);
    }

    @Test
    @Transactional
    void putNonExistingPattern() throws Exception {
        int databaseSizeBeforeUpdate = patternRepository.findAll().size();
        pattern.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPatternMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pattern.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pattern))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPattern() throws Exception {
        int databaseSizeBeforeUpdate = patternRepository.findAll().size();
        pattern.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatternMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pattern))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPattern() throws Exception {
        int databaseSizeBeforeUpdate = patternRepository.findAll().size();
        pattern.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatternMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pattern)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePatternWithPatch() throws Exception {
        // Initialize the database
        patternRepository.saveAndFlush(pattern);

        int databaseSizeBeforeUpdate = patternRepository.findAll().size();

        // Update the pattern using partial update
        Pattern partialUpdatedPattern = new Pattern();
        partialUpdatedPattern.setId(pattern.getId());

        restPatternMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPattern.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPattern))
            )
            .andExpect(status().isOk());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeUpdate);
        Pattern testPattern = patternList.get(patternList.size() - 1);
        assertThat(testPattern.getPattern()).isEqualTo(DEFAULT_PATTERN);
    }

    @Test
    @Transactional
    void fullUpdatePatternWithPatch() throws Exception {
        // Initialize the database
        patternRepository.saveAndFlush(pattern);

        int databaseSizeBeforeUpdate = patternRepository.findAll().size();

        // Update the pattern using partial update
        Pattern partialUpdatedPattern = new Pattern();
        partialUpdatedPattern.setId(pattern.getId());

        partialUpdatedPattern.pattern(UPDATED_PATTERN);

        restPatternMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPattern.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPattern))
            )
            .andExpect(status().isOk());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeUpdate);
        Pattern testPattern = patternList.get(patternList.size() - 1);
        assertThat(testPattern.getPattern()).isEqualTo(UPDATED_PATTERN);
    }

    @Test
    @Transactional
    void patchNonExistingPattern() throws Exception {
        int databaseSizeBeforeUpdate = patternRepository.findAll().size();
        pattern.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPatternMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pattern.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pattern))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPattern() throws Exception {
        int databaseSizeBeforeUpdate = patternRepository.findAll().size();
        pattern.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatternMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pattern))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPattern() throws Exception {
        int databaseSizeBeforeUpdate = patternRepository.findAll().size();
        pattern.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatternMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(pattern)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePattern() throws Exception {
        // Initialize the database
        patternRepository.saveAndFlush(pattern);

        int databaseSizeBeforeDelete = patternRepository.findAll().size();

        // Delete the pattern
        restPatternMockMvc
            .perform(delete(ENTITY_API_URL_ID, pattern.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
