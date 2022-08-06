package com.roccoshi.micro.web.rest;

import com.roccoshi.micro.MicroNameApp;

import com.roccoshi.micro.domain.Chassis;
import com.roccoshi.micro.domain.LineCard;
import com.roccoshi.micro.repository.ChassisRepository;
import com.roccoshi.micro.service.ChassisService;
import com.roccoshi.micro.service.dto.ChassisDTO;
import com.roccoshi.micro.service.mapper.ChassisMapper;
import com.roccoshi.micro.web.rest.errors.ExceptionTranslator;
import com.roccoshi.micro.service.dto.ChassisCriteria;
import com.roccoshi.micro.service.ChassisQueryService;

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
 * Test class for the ChassisResource REST controller.
 *
 * @see ChassisResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroNameApp.class)
public class ChassisResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_INFO = "AAAAAAAAAA";
    private static final String UPDATED_INFO = "BBBBBBBBBB";

    @Autowired
    private ChassisRepository chassisRepository;

    @Autowired
    private ChassisMapper chassisMapper;

    @Autowired
    private ChassisService chassisService;

    @Autowired
    private ChassisQueryService chassisQueryService;

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

    private MockMvc restChassisMockMvc;

    private Chassis chassis;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ChassisResource chassisResource = new ChassisResource(chassisService, chassisQueryService);
        this.restChassisMockMvc = MockMvcBuilders.standaloneSetup(chassisResource)
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
    public static Chassis createEntity(EntityManager em) {
        Chassis chassis = new Chassis()
            .name(DEFAULT_NAME)
            .info(DEFAULT_INFO);
        return chassis;
    }

    @Before
    public void initTest() {
        chassis = createEntity(em);
    }

    @Test
    @Transactional
    public void createChassis() throws Exception {
        int databaseSizeBeforeCreate = chassisRepository.findAll().size();

        // Create the Chassis
        ChassisDTO chassisDTO = chassisMapper.toDto(chassis);
        restChassisMockMvc.perform(post("/api/chassis")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chassisDTO)))
            .andExpect(status().isCreated());

        // Validate the Chassis in the database
        List<Chassis> chassisList = chassisRepository.findAll();
        assertThat(chassisList).hasSize(databaseSizeBeforeCreate + 1);
        Chassis testChassis = chassisList.get(chassisList.size() - 1);
        assertThat(testChassis.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testChassis.getInfo()).isEqualTo(DEFAULT_INFO);
    }

    @Test
    @Transactional
    public void createChassisWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = chassisRepository.findAll().size();

        // Create the Chassis with an existing ID
        chassis.setId(1L);
        ChassisDTO chassisDTO = chassisMapper.toDto(chassis);

        // An entity with an existing ID cannot be created, so this API call must fail
        restChassisMockMvc.perform(post("/api/chassis")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chassisDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Chassis in the database
        List<Chassis> chassisList = chassisRepository.findAll();
        assertThat(chassisList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllChassis() throws Exception {
        // Initialize the database
        chassisRepository.saveAndFlush(chassis);

        // Get all the chassisList
        restChassisMockMvc.perform(get("/api/chassis?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chassis.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].info").value(hasItem(DEFAULT_INFO.toString())));
    }
    
    @Test
    @Transactional
    public void getChassis() throws Exception {
        // Initialize the database
        chassisRepository.saveAndFlush(chassis);

        // Get the chassis
        restChassisMockMvc.perform(get("/api/chassis/{id}", chassis.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(chassis.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.info").value(DEFAULT_INFO.toString()));
    }

    @Test
    @Transactional
    public void getAllChassisByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        chassisRepository.saveAndFlush(chassis);

        // Get all the chassisList where name equals to DEFAULT_NAME
        defaultChassisShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the chassisList where name equals to UPDATED_NAME
        defaultChassisShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllChassisByNameIsInShouldWork() throws Exception {
        // Initialize the database
        chassisRepository.saveAndFlush(chassis);

        // Get all the chassisList where name in DEFAULT_NAME or UPDATED_NAME
        defaultChassisShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the chassisList where name equals to UPDATED_NAME
        defaultChassisShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllChassisByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        chassisRepository.saveAndFlush(chassis);

        // Get all the chassisList where name is not null
        defaultChassisShouldBeFound("name.specified=true");

        // Get all the chassisList where name is null
        defaultChassisShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllChassisByInfoIsEqualToSomething() throws Exception {
        // Initialize the database
        chassisRepository.saveAndFlush(chassis);

        // Get all the chassisList where info equals to DEFAULT_INFO
        defaultChassisShouldBeFound("info.equals=" + DEFAULT_INFO);

        // Get all the chassisList where info equals to UPDATED_INFO
        defaultChassisShouldNotBeFound("info.equals=" + UPDATED_INFO);
    }

    @Test
    @Transactional
    public void getAllChassisByInfoIsInShouldWork() throws Exception {
        // Initialize the database
        chassisRepository.saveAndFlush(chassis);

        // Get all the chassisList where info in DEFAULT_INFO or UPDATED_INFO
        defaultChassisShouldBeFound("info.in=" + DEFAULT_INFO + "," + UPDATED_INFO);

        // Get all the chassisList where info equals to UPDATED_INFO
        defaultChassisShouldNotBeFound("info.in=" + UPDATED_INFO);
    }

    @Test
    @Transactional
    public void getAllChassisByInfoIsNullOrNotNull() throws Exception {
        // Initialize the database
        chassisRepository.saveAndFlush(chassis);

        // Get all the chassisList where info is not null
        defaultChassisShouldBeFound("info.specified=true");

        // Get all the chassisList where info is null
        defaultChassisShouldNotBeFound("info.specified=false");
    }

    @Test
    @Transactional
    public void getAllChassisByLineCardIsEqualToSomething() throws Exception {
        // Initialize the database
        LineCard lineCard = LineCardResourceIntTest.createEntity(em);
        em.persist(lineCard);
        em.flush();
        chassis.addLineCard(lineCard);
        chassisRepository.saveAndFlush(chassis);
        Long lineCardId = lineCard.getId();

        // Get all the chassisList where lineCard equals to lineCardId
        defaultChassisShouldBeFound("lineCardId.equals=" + lineCardId);

        // Get all the chassisList where lineCard equals to lineCardId + 1
        defaultChassisShouldNotBeFound("lineCardId.equals=" + (lineCardId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultChassisShouldBeFound(String filter) throws Exception {
        restChassisMockMvc.perform(get("/api/chassis?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chassis.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].info").value(hasItem(DEFAULT_INFO)));

        // Check, that the count call also returns 1
        restChassisMockMvc.perform(get("/api/chassis/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultChassisShouldNotBeFound(String filter) throws Exception {
        restChassisMockMvc.perform(get("/api/chassis?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restChassisMockMvc.perform(get("/api/chassis/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingChassis() throws Exception {
        // Get the chassis
        restChassisMockMvc.perform(get("/api/chassis/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateChassis() throws Exception {
        // Initialize the database
        chassisRepository.saveAndFlush(chassis);

        int databaseSizeBeforeUpdate = chassisRepository.findAll().size();

        // Update the chassis
        Chassis updatedChassis = chassisRepository.findById(chassis.getId()).get();
        // Disconnect from session so that the updates on updatedChassis are not directly saved in db
        em.detach(updatedChassis);
        updatedChassis
            .name(UPDATED_NAME)
            .info(UPDATED_INFO);
        ChassisDTO chassisDTO = chassisMapper.toDto(updatedChassis);

        restChassisMockMvc.perform(put("/api/chassis")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chassisDTO)))
            .andExpect(status().isOk());

        // Validate the Chassis in the database
        List<Chassis> chassisList = chassisRepository.findAll();
        assertThat(chassisList).hasSize(databaseSizeBeforeUpdate);
        Chassis testChassis = chassisList.get(chassisList.size() - 1);
        assertThat(testChassis.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testChassis.getInfo()).isEqualTo(UPDATED_INFO);
    }

    @Test
    @Transactional
    public void updateNonExistingChassis() throws Exception {
        int databaseSizeBeforeUpdate = chassisRepository.findAll().size();

        // Create the Chassis
        ChassisDTO chassisDTO = chassisMapper.toDto(chassis);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChassisMockMvc.perform(put("/api/chassis")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chassisDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Chassis in the database
        List<Chassis> chassisList = chassisRepository.findAll();
        assertThat(chassisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteChassis() throws Exception {
        // Initialize the database
        chassisRepository.saveAndFlush(chassis);

        int databaseSizeBeforeDelete = chassisRepository.findAll().size();

        // Delete the chassis
        restChassisMockMvc.perform(delete("/api/chassis/{id}", chassis.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Chassis> chassisList = chassisRepository.findAll();
        assertThat(chassisList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Chassis.class);
        Chassis chassis1 = new Chassis();
        chassis1.setId(1L);
        Chassis chassis2 = new Chassis();
        chassis2.setId(chassis1.getId());
        assertThat(chassis1).isEqualTo(chassis2);
        chassis2.setId(2L);
        assertThat(chassis1).isNotEqualTo(chassis2);
        chassis1.setId(null);
        assertThat(chassis1).isNotEqualTo(chassis2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChassisDTO.class);
        ChassisDTO chassisDTO1 = new ChassisDTO();
        chassisDTO1.setId(1L);
        ChassisDTO chassisDTO2 = new ChassisDTO();
        assertThat(chassisDTO1).isNotEqualTo(chassisDTO2);
        chassisDTO2.setId(chassisDTO1.getId());
        assertThat(chassisDTO1).isEqualTo(chassisDTO2);
        chassisDTO2.setId(2L);
        assertThat(chassisDTO1).isNotEqualTo(chassisDTO2);
        chassisDTO1.setId(null);
        assertThat(chassisDTO1).isNotEqualTo(chassisDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(chassisMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(chassisMapper.fromId(null)).isNull();
    }
}
