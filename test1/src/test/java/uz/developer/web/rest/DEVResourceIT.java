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
import uz.developer.domain.DEV;
import uz.developer.repository.DEVRepository;

/**
 * Integration tests for the {@link DEVResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DEVResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/devs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DEVRepository dEVRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDEVMockMvc;

    private DEV dEV;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DEV createEntity(EntityManager em) {
        DEV dEV = new DEV().name(DEFAULT_NAME);
        return dEV;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DEV createUpdatedEntity(EntityManager em) {
        DEV dEV = new DEV().name(UPDATED_NAME);
        return dEV;
    }

    @BeforeEach
    public void initTest() {
        dEV = createEntity(em);
    }

    @Test
    @Transactional
    void createDEV() throws Exception {
        int databaseSizeBeforeCreate = dEVRepository.findAll().size();
        // Create the DEV
        restDEVMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dEV)))
            .andExpect(status().isCreated());

        // Validate the DEV in the database
        List<DEV> dEVList = dEVRepository.findAll();
        assertThat(dEVList).hasSize(databaseSizeBeforeCreate + 1);
        DEV testDEV = dEVList.get(dEVList.size() - 1);
        assertThat(testDEV.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createDEVWithExistingId() throws Exception {
        // Create the DEV with an existing ID
        dEV.setId(1L);

        int databaseSizeBeforeCreate = dEVRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDEVMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dEV)))
            .andExpect(status().isBadRequest());

        // Validate the DEV in the database
        List<DEV> dEVList = dEVRepository.findAll();
        assertThat(dEVList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDEVS() throws Exception {
        // Initialize the database
        dEVRepository.saveAndFlush(dEV);

        // Get all the dEVList
        restDEVMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dEV.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getDEV() throws Exception {
        // Initialize the database
        dEVRepository.saveAndFlush(dEV);

        // Get the dEV
        restDEVMockMvc
            .perform(get(ENTITY_API_URL_ID, dEV.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dEV.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingDEV() throws Exception {
        // Get the dEV
        restDEVMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDEV() throws Exception {
        // Initialize the database
        dEVRepository.saveAndFlush(dEV);

        int databaseSizeBeforeUpdate = dEVRepository.findAll().size();

        // Update the dEV
        DEV updatedDEV = dEVRepository.findById(dEV.getId()).get();
        // Disconnect from session so that the updates on updatedDEV are not directly saved in db
        em.detach(updatedDEV);
        updatedDEV.name(UPDATED_NAME);

        restDEVMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDEV.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDEV))
            )
            .andExpect(status().isOk());

        // Validate the DEV in the database
        List<DEV> dEVList = dEVRepository.findAll();
        assertThat(dEVList).hasSize(databaseSizeBeforeUpdate);
        DEV testDEV = dEVList.get(dEVList.size() - 1);
        assertThat(testDEV.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingDEV() throws Exception {
        int databaseSizeBeforeUpdate = dEVRepository.findAll().size();
        dEV.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDEVMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dEV.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dEV))
            )
            .andExpect(status().isBadRequest());

        // Validate the DEV in the database
        List<DEV> dEVList = dEVRepository.findAll();
        assertThat(dEVList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDEV() throws Exception {
        int databaseSizeBeforeUpdate = dEVRepository.findAll().size();
        dEV.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDEVMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dEV))
            )
            .andExpect(status().isBadRequest());

        // Validate the DEV in the database
        List<DEV> dEVList = dEVRepository.findAll();
        assertThat(dEVList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDEV() throws Exception {
        int databaseSizeBeforeUpdate = dEVRepository.findAll().size();
        dEV.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDEVMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dEV)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DEV in the database
        List<DEV> dEVList = dEVRepository.findAll();
        assertThat(dEVList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDEVWithPatch() throws Exception {
        // Initialize the database
        dEVRepository.saveAndFlush(dEV);

        int databaseSizeBeforeUpdate = dEVRepository.findAll().size();

        // Update the dEV using partial update
        DEV partialUpdatedDEV = new DEV();
        partialUpdatedDEV.setId(dEV.getId());

        restDEVMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDEV.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDEV))
            )
            .andExpect(status().isOk());

        // Validate the DEV in the database
        List<DEV> dEVList = dEVRepository.findAll();
        assertThat(dEVList).hasSize(databaseSizeBeforeUpdate);
        DEV testDEV = dEVList.get(dEVList.size() - 1);
        assertThat(testDEV.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateDEVWithPatch() throws Exception {
        // Initialize the database
        dEVRepository.saveAndFlush(dEV);

        int databaseSizeBeforeUpdate = dEVRepository.findAll().size();

        // Update the dEV using partial update
        DEV partialUpdatedDEV = new DEV();
        partialUpdatedDEV.setId(dEV.getId());

        partialUpdatedDEV.name(UPDATED_NAME);

        restDEVMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDEV.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDEV))
            )
            .andExpect(status().isOk());

        // Validate the DEV in the database
        List<DEV> dEVList = dEVRepository.findAll();
        assertThat(dEVList).hasSize(databaseSizeBeforeUpdate);
        DEV testDEV = dEVList.get(dEVList.size() - 1);
        assertThat(testDEV.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingDEV() throws Exception {
        int databaseSizeBeforeUpdate = dEVRepository.findAll().size();
        dEV.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDEVMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dEV.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dEV))
            )
            .andExpect(status().isBadRequest());

        // Validate the DEV in the database
        List<DEV> dEVList = dEVRepository.findAll();
        assertThat(dEVList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDEV() throws Exception {
        int databaseSizeBeforeUpdate = dEVRepository.findAll().size();
        dEV.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDEVMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dEV))
            )
            .andExpect(status().isBadRequest());

        // Validate the DEV in the database
        List<DEV> dEVList = dEVRepository.findAll();
        assertThat(dEVList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDEV() throws Exception {
        int databaseSizeBeforeUpdate = dEVRepository.findAll().size();
        dEV.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDEVMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dEV)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DEV in the database
        List<DEV> dEVList = dEVRepository.findAll();
        assertThat(dEVList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDEV() throws Exception {
        // Initialize the database
        dEVRepository.saveAndFlush(dEV);

        int databaseSizeBeforeDelete = dEVRepository.findAll().size();

        // Delete the dEV
        restDEVMockMvc.perform(delete(ENTITY_API_URL_ID, dEV.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DEV> dEVList = dEVRepository.findAll();
        assertThat(dEVList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
