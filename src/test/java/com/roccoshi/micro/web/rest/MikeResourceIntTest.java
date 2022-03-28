package com.roccoshi.micro.web.rest;

import com.roccoshi.micro.MicroNameApp;

import com.roccoshi.micro.domain.Mike;
import com.roccoshi.micro.repository.MikeRepository;
import com.roccoshi.micro.service.MikeService;
import com.roccoshi.micro.service.dto.MikeDTO;
import com.roccoshi.micro.service.mapper.MikeMapper;
import com.roccoshi.micro.web.rest.errors.ExceptionTranslator;
import com.roccoshi.micro.service.dto.MikeCriteria;
import com.roccoshi.micro.service.MikeQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;


import static com.roccoshi.micro.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MikeResource REST controller.
 *
 * @see MikeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroNameApp.class)
public class MikeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_AGE = 1;
    private static final Integer UPDATED_AGE = 2;

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    @Autowired
    private MikeRepository mikeRepository;

    @Autowired
    private MikeMapper mikeMapper;

    @Autowired
    private MikeService mikeService;

    @Autowired
    private MikeQueryService mikeQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restMikeMockMvc;

    private Mike mike;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MikeResource mikeResource = new MikeResource(mikeService, mikeQueryService);
        this.restMikeMockMvc = MockMvcBuilders.standaloneSetup(mikeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mike createEntity(EntityManager em) {
        Mike mike = new Mike()
            .name(DEFAULT_NAME)
            .age(DEFAULT_AGE)
            .details(DEFAULT_DETAILS);
        return mike;
    }

    @Before
    public void initTest() {
        mike = createEntity(em);
    }

    @Test
    @Transactional
    public void createMike() throws Exception {
        int databaseSizeBeforeCreate = mikeRepository.findAll().size();

        // Create the Mike
        MikeDTO mikeDTO = mikeMapper.toDto(mike);
        restMikeMockMvc.perform(post("/api/mikes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mikeDTO)))
            .andExpect(status().isCreated());

        // Validate the Mike in the database
        List<Mike> mikeList = mikeRepository.findAll();
        assertThat(mikeList).hasSize(databaseSizeBeforeCreate + 1);
        Mike testMike = mikeList.get(mikeList.size() - 1);
        assertThat(testMike.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMike.getAge()).isEqualTo(DEFAULT_AGE);
        assertThat(testMike.getDetails()).isEqualTo(DEFAULT_DETAILS);
    }

    @Test
    @Transactional
    public void createMikeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = mikeRepository.findAll().size();

        // Create the Mike with an existing ID
        mike.setId(1L);
        MikeDTO mikeDTO = mikeMapper.toDto(mike);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMikeMockMvc.perform(post("/api/mikes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mikeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Mike in the database
        List<Mike> mikeList = mikeRepository.findAll();
        assertThat(mikeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMikes() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get all the mikeList
        restMikeMockMvc.perform(get("/api/mikes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mike.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS.toString())));
    }
    
    @Test
    @Transactional
    public void getMike() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get the mike
        restMikeMockMvc.perform(get("/api/mikes/{id}", mike.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(mike.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS.toString()));
    }

    @Test
    @Transactional
    public void getAllMikesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get all the mikeList where name equals to DEFAULT_NAME
        defaultMikeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the mikeList where name equals to UPDATED_NAME
        defaultMikeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMikesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get all the mikeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMikeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the mikeList where name equals to UPDATED_NAME
        defaultMikeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMikesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get all the mikeList where name is not null
        defaultMikeShouldBeFound("name.specified=true");

        // Get all the mikeList where name is null
        defaultMikeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllMikesByAgeIsEqualToSomething() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get all the mikeList where age equals to DEFAULT_AGE
        defaultMikeShouldBeFound("age.equals=" + DEFAULT_AGE);

        // Get all the mikeList where age equals to UPDATED_AGE
        defaultMikeShouldNotBeFound("age.equals=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    public void getAllMikesByAgeIsInShouldWork() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get all the mikeList where age in DEFAULT_AGE or UPDATED_AGE
        defaultMikeShouldBeFound("age.in=" + DEFAULT_AGE + "," + UPDATED_AGE);

        // Get all the mikeList where age equals to UPDATED_AGE
        defaultMikeShouldNotBeFound("age.in=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    public void getAllMikesByAgeIsNullOrNotNull() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get all the mikeList where age is not null
        defaultMikeShouldBeFound("age.specified=true");

        // Get all the mikeList where age is null
        defaultMikeShouldNotBeFound("age.specified=false");
    }

    @Test
    @Transactional
    public void getAllMikesByAgeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get all the mikeList where age greater than or equals to DEFAULT_AGE
        defaultMikeShouldBeFound("age.greaterOrEqualThan=" + DEFAULT_AGE);

        // Get all the mikeList where age greater than or equals to UPDATED_AGE
        defaultMikeShouldNotBeFound("age.greaterOrEqualThan=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    public void getAllMikesByAgeIsLessThanSomething() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get all the mikeList where age less than or equals to DEFAULT_AGE
        defaultMikeShouldNotBeFound("age.lessThan=" + DEFAULT_AGE);

        // Get all the mikeList where age less than or equals to UPDATED_AGE
        defaultMikeShouldBeFound("age.lessThan=" + UPDATED_AGE);
    }


    @Test
    @Transactional
    public void getAllMikesByDetailsIsEqualToSomething() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get all the mikeList where details equals to DEFAULT_DETAILS
        defaultMikeShouldBeFound("details.equals=" + DEFAULT_DETAILS);

        // Get all the mikeList where details equals to UPDATED_DETAILS
        defaultMikeShouldNotBeFound("details.equals=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void getAllMikesByDetailsIsInShouldWork() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get all the mikeList where details in DEFAULT_DETAILS or UPDATED_DETAILS
        defaultMikeShouldBeFound("details.in=" + DEFAULT_DETAILS + "," + UPDATED_DETAILS);

        // Get all the mikeList where details equals to UPDATED_DETAILS
        defaultMikeShouldNotBeFound("details.in=" + UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void getAllMikesByDetailsIsNullOrNotNull() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        // Get all the mikeList where details is not null
        defaultMikeShouldBeFound("details.specified=true");

        // Get all the mikeList where details is null
        defaultMikeShouldNotBeFound("details.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultMikeShouldBeFound(String filter) throws Exception {
        restMikeMockMvc.perform(get("/api/mikes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mike.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)));

        // Check, that the count call also returns 1
        restMikeMockMvc.perform(get("/api/mikes/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultMikeShouldNotBeFound(String filter) throws Exception {
        restMikeMockMvc.perform(get("/api/mikes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMikeMockMvc.perform(get("/api/mikes/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingMike() throws Exception {
        // Get the mike
        restMikeMockMvc.perform(get("/api/mikes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMike() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        int databaseSizeBeforeUpdate = mikeRepository.findAll().size();

        // Update the mike
        Mike updatedMike = mikeRepository.findById(mike.getId()).get();
        // Disconnect from session so that the updates on updatedMike are not directly saved in db
        em.detach(updatedMike);
        updatedMike
            .name(UPDATED_NAME)
            .age(UPDATED_AGE)
            .details(UPDATED_DETAILS);
        MikeDTO mikeDTO = mikeMapper.toDto(updatedMike);

        restMikeMockMvc.perform(put("/api/mikes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mikeDTO)))
            .andExpect(status().isOk());

        // Validate the Mike in the database
        List<Mike> mikeList = mikeRepository.findAll();
        assertThat(mikeList).hasSize(databaseSizeBeforeUpdate);
        Mike testMike = mikeList.get(mikeList.size() - 1);
        assertThat(testMike.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMike.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testMike.getDetails()).isEqualTo(UPDATED_DETAILS);
    }

    @Test
    @Transactional
    public void updateNonExistingMike() throws Exception {
        int databaseSizeBeforeUpdate = mikeRepository.findAll().size();

        // Create the Mike
        MikeDTO mikeDTO = mikeMapper.toDto(mike);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMikeMockMvc.perform(put("/api/mikes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mikeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Mike in the database
        List<Mike> mikeList = mikeRepository.findAll();
        assertThat(mikeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteMike() throws Exception {
        // Initialize the database
        mikeRepository.saveAndFlush(mike);

        int databaseSizeBeforeDelete = mikeRepository.findAll().size();

        // Delete the mike
        restMikeMockMvc.perform(delete("/api/mikes/{id}", mike.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Mike> mikeList = mikeRepository.findAll();
        assertThat(mikeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mike.class);
        Mike mike1 = new Mike();
        mike1.setId(1L);
        Mike mike2 = new Mike();
        mike2.setId(mike1.getId());
        assertThat(mike1).isEqualTo(mike2);
        mike2.setId(2L);
        assertThat(mike1).isNotEqualTo(mike2);
        mike1.setId(null);
        assertThat(mike1).isNotEqualTo(mike2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MikeDTO.class);
        MikeDTO mikeDTO1 = new MikeDTO();
        mikeDTO1.setId(1L);
        MikeDTO mikeDTO2 = new MikeDTO();
        assertThat(mikeDTO1).isNotEqualTo(mikeDTO2);
        mikeDTO2.setId(mikeDTO1.getId());
        assertThat(mikeDTO1).isEqualTo(mikeDTO2);
        mikeDTO2.setId(2L);
        assertThat(mikeDTO1).isNotEqualTo(mikeDTO2);
        mikeDTO1.setId(null);
        assertThat(mikeDTO1).isNotEqualTo(mikeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(mikeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(mikeMapper.fromId(null)).isNull();
    }
}
