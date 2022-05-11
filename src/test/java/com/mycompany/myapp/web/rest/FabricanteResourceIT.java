package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Fabricante;
import com.mycompany.myapp.repository.FabricanteRepository;
import com.mycompany.myapp.service.dto.FabricanteDTO;
import com.mycompany.myapp.service.mapper.FabricanteMapper;
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

/**
 * Integration tests for the {@link FabricanteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FabricanteResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/fabricantes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FabricanteRepository fabricanteRepository;

    @Autowired
    private FabricanteMapper fabricanteMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFabricanteMockMvc;

    private Fabricante fabricante;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Fabricante createEntity(EntityManager em) {
        Fabricante fabricante = new Fabricante().nome(DEFAULT_NOME);
        return fabricante;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Fabricante createUpdatedEntity(EntityManager em) {
        Fabricante fabricante = new Fabricante().nome(UPDATED_NOME);
        return fabricante;
    }

    @BeforeEach
    public void initTest() {
        fabricante = createEntity(em);
    }

    @Test
    @Transactional
    void createFabricante() throws Exception {
        int databaseSizeBeforeCreate = fabricanteRepository.findAll().size();
        // Create the Fabricante
        FabricanteDTO fabricanteDTO = fabricanteMapper.toDto(fabricante);
        restFabricanteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fabricanteDTO)))
            .andExpect(status().isCreated());

        // Validate the Fabricante in the database
        List<Fabricante> fabricanteList = fabricanteRepository.findAll();
        assertThat(fabricanteList).hasSize(databaseSizeBeforeCreate + 1);
        Fabricante testFabricante = fabricanteList.get(fabricanteList.size() - 1);
        assertThat(testFabricante.getNome()).isEqualTo(DEFAULT_NOME);
    }

    @Test
    @Transactional
    void createFabricanteWithExistingId() throws Exception {
        // Create the Fabricante with an existing ID
        fabricante.setId(1L);
        FabricanteDTO fabricanteDTO = fabricanteMapper.toDto(fabricante);

        int databaseSizeBeforeCreate = fabricanteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFabricanteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fabricanteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Fabricante in the database
        List<Fabricante> fabricanteList = fabricanteRepository.findAll();
        assertThat(fabricanteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFabricantes() throws Exception {
        // Initialize the database
        fabricanteRepository.saveAndFlush(fabricante);

        // Get all the fabricanteList
        restFabricanteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fabricante.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)));
    }

    @Test
    @Transactional
    void getFabricante() throws Exception {
        // Initialize the database
        fabricanteRepository.saveAndFlush(fabricante);

        // Get the fabricante
        restFabricanteMockMvc
            .perform(get(ENTITY_API_URL_ID, fabricante.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fabricante.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME));
    }

    @Test
    @Transactional
    void getNonExistingFabricante() throws Exception {
        // Get the fabricante
        restFabricanteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFabricante() throws Exception {
        // Initialize the database
        fabricanteRepository.saveAndFlush(fabricante);

        int databaseSizeBeforeUpdate = fabricanteRepository.findAll().size();

        // Update the fabricante
        Fabricante updatedFabricante = fabricanteRepository.findById(fabricante.getId()).get();
        // Disconnect from session so that the updates on updatedFabricante are not directly saved in db
        em.detach(updatedFabricante);
        updatedFabricante.nome(UPDATED_NOME);
        FabricanteDTO fabricanteDTO = fabricanteMapper.toDto(updatedFabricante);

        restFabricanteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fabricanteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fabricanteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Fabricante in the database
        List<Fabricante> fabricanteList = fabricanteRepository.findAll();
        assertThat(fabricanteList).hasSize(databaseSizeBeforeUpdate);
        Fabricante testFabricante = fabricanteList.get(fabricanteList.size() - 1);
        assertThat(testFabricante.getNome()).isEqualTo(UPDATED_NOME);
    }

    @Test
    @Transactional
    void putNonExistingFabricante() throws Exception {
        int databaseSizeBeforeUpdate = fabricanteRepository.findAll().size();
        fabricante.setId(count.incrementAndGet());

        // Create the Fabricante
        FabricanteDTO fabricanteDTO = fabricanteMapper.toDto(fabricante);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFabricanteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fabricanteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fabricanteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fabricante in the database
        List<Fabricante> fabricanteList = fabricanteRepository.findAll();
        assertThat(fabricanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFabricante() throws Exception {
        int databaseSizeBeforeUpdate = fabricanteRepository.findAll().size();
        fabricante.setId(count.incrementAndGet());

        // Create the Fabricante
        FabricanteDTO fabricanteDTO = fabricanteMapper.toDto(fabricante);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFabricanteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fabricanteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fabricante in the database
        List<Fabricante> fabricanteList = fabricanteRepository.findAll();
        assertThat(fabricanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFabricante() throws Exception {
        int databaseSizeBeforeUpdate = fabricanteRepository.findAll().size();
        fabricante.setId(count.incrementAndGet());

        // Create the Fabricante
        FabricanteDTO fabricanteDTO = fabricanteMapper.toDto(fabricante);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFabricanteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fabricanteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Fabricante in the database
        List<Fabricante> fabricanteList = fabricanteRepository.findAll();
        assertThat(fabricanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFabricanteWithPatch() throws Exception {
        // Initialize the database
        fabricanteRepository.saveAndFlush(fabricante);

        int databaseSizeBeforeUpdate = fabricanteRepository.findAll().size();

        // Update the fabricante using partial update
        Fabricante partialUpdatedFabricante = new Fabricante();
        partialUpdatedFabricante.setId(fabricante.getId());

        partialUpdatedFabricante.nome(UPDATED_NOME);

        restFabricanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFabricante.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFabricante))
            )
            .andExpect(status().isOk());

        // Validate the Fabricante in the database
        List<Fabricante> fabricanteList = fabricanteRepository.findAll();
        assertThat(fabricanteList).hasSize(databaseSizeBeforeUpdate);
        Fabricante testFabricante = fabricanteList.get(fabricanteList.size() - 1);
        assertThat(testFabricante.getNome()).isEqualTo(UPDATED_NOME);
    }

    @Test
    @Transactional
    void fullUpdateFabricanteWithPatch() throws Exception {
        // Initialize the database
        fabricanteRepository.saveAndFlush(fabricante);

        int databaseSizeBeforeUpdate = fabricanteRepository.findAll().size();

        // Update the fabricante using partial update
        Fabricante partialUpdatedFabricante = new Fabricante();
        partialUpdatedFabricante.setId(fabricante.getId());

        partialUpdatedFabricante.nome(UPDATED_NOME);

        restFabricanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFabricante.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFabricante))
            )
            .andExpect(status().isOk());

        // Validate the Fabricante in the database
        List<Fabricante> fabricanteList = fabricanteRepository.findAll();
        assertThat(fabricanteList).hasSize(databaseSizeBeforeUpdate);
        Fabricante testFabricante = fabricanteList.get(fabricanteList.size() - 1);
        assertThat(testFabricante.getNome()).isEqualTo(UPDATED_NOME);
    }

    @Test
    @Transactional
    void patchNonExistingFabricante() throws Exception {
        int databaseSizeBeforeUpdate = fabricanteRepository.findAll().size();
        fabricante.setId(count.incrementAndGet());

        // Create the Fabricante
        FabricanteDTO fabricanteDTO = fabricanteMapper.toDto(fabricante);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFabricanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fabricanteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fabricanteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fabricante in the database
        List<Fabricante> fabricanteList = fabricanteRepository.findAll();
        assertThat(fabricanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFabricante() throws Exception {
        int databaseSizeBeforeUpdate = fabricanteRepository.findAll().size();
        fabricante.setId(count.incrementAndGet());

        // Create the Fabricante
        FabricanteDTO fabricanteDTO = fabricanteMapper.toDto(fabricante);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFabricanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fabricanteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fabricante in the database
        List<Fabricante> fabricanteList = fabricanteRepository.findAll();
        assertThat(fabricanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFabricante() throws Exception {
        int databaseSizeBeforeUpdate = fabricanteRepository.findAll().size();
        fabricante.setId(count.incrementAndGet());

        // Create the Fabricante
        FabricanteDTO fabricanteDTO = fabricanteMapper.toDto(fabricante);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFabricanteMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(fabricanteDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Fabricante in the database
        List<Fabricante> fabricanteList = fabricanteRepository.findAll();
        assertThat(fabricanteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFabricante() throws Exception {
        // Initialize the database
        fabricanteRepository.saveAndFlush(fabricante);

        int databaseSizeBeforeDelete = fabricanteRepository.findAll().size();

        // Delete the fabricante
        restFabricanteMockMvc
            .perform(delete(ENTITY_API_URL_ID, fabricante.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Fabricante> fabricanteList = fabricanteRepository.findAll();
        assertThat(fabricanteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
