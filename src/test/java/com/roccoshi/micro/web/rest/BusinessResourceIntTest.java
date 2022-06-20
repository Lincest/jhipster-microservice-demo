package com.roccoshi.micro.web.rest;

import com.roccoshi.micro.MicroNameApp;

import com.roccoshi.micro.domain.Business;
import com.roccoshi.micro.repository.BusinessRepository;
import com.roccoshi.micro.service.BusinessService;
import com.roccoshi.micro.service.dto.BusinessDTO;
import com.roccoshi.micro.service.mapper.BusinessMapper;
import com.roccoshi.micro.web.rest.errors.ExceptionTranslator;
import com.roccoshi.micro.service.dto.BusinessCriteria;
import com.roccoshi.micro.service.BusinessQueryService;

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
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


import static com.roccoshi.micro.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BusinessResource REST controller.
 *
 * @see BusinessResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroNameApp.class)
public class BusinessResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_UUID = "AAAAAAAAAA";
    private static final String UPDATED_UUID = "BBBBBBBBBB";

    private static final String DEFAULT_RAW_JSON = "AAAAAAAAAA";
    private static final String UPDATED_RAW_JSON = "BBBBBBBBBB";

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessMapper businessMapper;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private BusinessQueryService businessQueryService;

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

    private MockMvc restBusinessMockMvc;

    private Business business;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BusinessResource businessResource = new BusinessResource(businessService, businessQueryService);
        this.restBusinessMockMvc = MockMvcBuilders.standaloneSetup(businessResource)
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
    public static Business createEntity(EntityManager em) {
        Business business = new Business()
            .name(DEFAULT_NAME)
            .time(DEFAULT_TIME)
            .uuid(DEFAULT_UUID)
            .rawJson(DEFAULT_RAW_JSON);
        return business;
    }

    @Before
    public void initTest() {
        business = createEntity(em);
    }

    @Test
    @Transactional
    public void createBusiness() throws Exception {
        int databaseSizeBeforeCreate = businessRepository.findAll().size();

        // Create the Business
        BusinessDTO businessDTO = businessMapper.toDto(business);
        restBusinessMockMvc.perform(post("/api/businesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessDTO)))
            .andExpect(status().isCreated());

        // Validate the Business in the database
        List<Business> businessList = businessRepository.findAll();
        assertThat(businessList).hasSize(databaseSizeBeforeCreate + 1);
        Business testBusiness = businessList.get(businessList.size() - 1);
        assertThat(testBusiness.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBusiness.getTime()).isEqualTo(DEFAULT_TIME);
        assertThat(testBusiness.getUuid()).isEqualTo(DEFAULT_UUID);
        assertThat(testBusiness.getRawJson()).isEqualTo(DEFAULT_RAW_JSON);
    }

    @Test
    @Transactional
    public void createBusinessWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = businessRepository.findAll().size();

        // Create the Business with an existing ID
        business.setId(1L);
        BusinessDTO businessDTO = businessMapper.toDto(business);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBusinessMockMvc.perform(post("/api/businesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Business in the database
        List<Business> businessList = businessRepository.findAll();
        assertThat(businessList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllBusinesses() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get all the businessList
        restBusinessMockMvc.perform(get("/api/businesses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(business.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.toString())))
            .andExpect(jsonPath("$.[*].uuid").value(hasItem(DEFAULT_UUID.toString())))
            .andExpect(jsonPath("$.[*].rawJson").value(hasItem(DEFAULT_RAW_JSON.toString())));
    }
    
    @Test
    @Transactional
    public void getBusiness() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get the business
        restBusinessMockMvc.perform(get("/api/businesses/{id}", business.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(business.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME.toString()))
            .andExpect(jsonPath("$.uuid").value(DEFAULT_UUID.toString()))
            .andExpect(jsonPath("$.rawJson").value(DEFAULT_RAW_JSON.toString()));
    }

    @Test
    @Transactional
    public void getAllBusinessesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get all the businessList where name equals to DEFAULT_NAME
        defaultBusinessShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the businessList where name equals to UPDATED_NAME
        defaultBusinessShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBusinessesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get all the businessList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBusinessShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the businessList where name equals to UPDATED_NAME
        defaultBusinessShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllBusinessesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get all the businessList where name is not null
        defaultBusinessShouldBeFound("name.specified=true");

        // Get all the businessList where name is null
        defaultBusinessShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllBusinessesByTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get all the businessList where time equals to DEFAULT_TIME
        defaultBusinessShouldBeFound("time.equals=" + DEFAULT_TIME);

        // Get all the businessList where time equals to UPDATED_TIME
        defaultBusinessShouldNotBeFound("time.equals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllBusinessesByTimeIsInShouldWork() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get all the businessList where time in DEFAULT_TIME or UPDATED_TIME
        defaultBusinessShouldBeFound("time.in=" + DEFAULT_TIME + "," + UPDATED_TIME);

        // Get all the businessList where time equals to UPDATED_TIME
        defaultBusinessShouldNotBeFound("time.in=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllBusinessesByTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get all the businessList where time is not null
        defaultBusinessShouldBeFound("time.specified=true");

        // Get all the businessList where time is null
        defaultBusinessShouldNotBeFound("time.specified=false");
    }

    @Test
    @Transactional
    public void getAllBusinessesByTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get all the businessList where time greater than or equals to DEFAULT_TIME
        defaultBusinessShouldBeFound("time.greaterOrEqualThan=" + DEFAULT_TIME);

        // Get all the businessList where time greater than or equals to UPDATED_TIME
        defaultBusinessShouldNotBeFound("time.greaterOrEqualThan=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllBusinessesByTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get all the businessList where time less than or equals to DEFAULT_TIME
        defaultBusinessShouldNotBeFound("time.lessThan=" + DEFAULT_TIME);

        // Get all the businessList where time less than or equals to UPDATED_TIME
        defaultBusinessShouldBeFound("time.lessThan=" + UPDATED_TIME);
    }


    @Test
    @Transactional
    public void getAllBusinessesByUuidIsEqualToSomething() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get all the businessList where uuid equals to DEFAULT_UUID
        defaultBusinessShouldBeFound("uuid.equals=" + DEFAULT_UUID);

        // Get all the businessList where uuid equals to UPDATED_UUID
        defaultBusinessShouldNotBeFound("uuid.equals=" + UPDATED_UUID);
    }

    @Test
    @Transactional
    public void getAllBusinessesByUuidIsInShouldWork() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get all the businessList where uuid in DEFAULT_UUID or UPDATED_UUID
        defaultBusinessShouldBeFound("uuid.in=" + DEFAULT_UUID + "," + UPDATED_UUID);

        // Get all the businessList where uuid equals to UPDATED_UUID
        defaultBusinessShouldNotBeFound("uuid.in=" + UPDATED_UUID);
    }

    @Test
    @Transactional
    public void getAllBusinessesByUuidIsNullOrNotNull() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        // Get all the businessList where uuid is not null
        defaultBusinessShouldBeFound("uuid.specified=true");

        // Get all the businessList where uuid is null
        defaultBusinessShouldNotBeFound("uuid.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultBusinessShouldBeFound(String filter) throws Exception {
        restBusinessMockMvc.perform(get("/api/businesses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(business.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.toString())))
            .andExpect(jsonPath("$.[*].uuid").value(hasItem(DEFAULT_UUID)))
            .andExpect(jsonPath("$.[*].rawJson").value(hasItem(DEFAULT_RAW_JSON.toString())));

        // Check, that the count call also returns 1
        restBusinessMockMvc.perform(get("/api/businesses/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultBusinessShouldNotBeFound(String filter) throws Exception {
        restBusinessMockMvc.perform(get("/api/businesses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBusinessMockMvc.perform(get("/api/businesses/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingBusiness() throws Exception {
        // Get the business
        restBusinessMockMvc.perform(get("/api/businesses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBusiness() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        int databaseSizeBeforeUpdate = businessRepository.findAll().size();

        // Update the business
        Business updatedBusiness = businessRepository.findById(business.getId()).get();
        // Disconnect from session so that the updates on updatedBusiness are not directly saved in db
        em.detach(updatedBusiness);
        updatedBusiness
            .name(UPDATED_NAME)
            .time(UPDATED_TIME)
            .uuid(UPDATED_UUID)
            .rawJson(UPDATED_RAW_JSON);
        BusinessDTO businessDTO = businessMapper.toDto(updatedBusiness);

        restBusinessMockMvc.perform(put("/api/businesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessDTO)))
            .andExpect(status().isOk());

        // Validate the Business in the database
        List<Business> businessList = businessRepository.findAll();
        assertThat(businessList).hasSize(databaseSizeBeforeUpdate);
        Business testBusiness = businessList.get(businessList.size() - 1);
        assertThat(testBusiness.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBusiness.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testBusiness.getUuid()).isEqualTo(UPDATED_UUID);
        assertThat(testBusiness.getRawJson()).isEqualTo(UPDATED_RAW_JSON);
    }

    @Test
    @Transactional
    public void updateNonExistingBusiness() throws Exception {
        int databaseSizeBeforeUpdate = businessRepository.findAll().size();

        // Create the Business
        BusinessDTO businessDTO = businessMapper.toDto(business);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessMockMvc.perform(put("/api/businesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Business in the database
        List<Business> businessList = businessRepository.findAll();
        assertThat(businessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBusiness() throws Exception {
        // Initialize the database
        businessRepository.saveAndFlush(business);

        int databaseSizeBeforeDelete = businessRepository.findAll().size();

        // Delete the business
        restBusinessMockMvc.perform(delete("/api/businesses/{id}", business.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Business> businessList = businessRepository.findAll();
        assertThat(businessList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Business.class);
        Business business1 = new Business();
        business1.setId(1L);
        Business business2 = new Business();
        business2.setId(business1.getId());
        assertThat(business1).isEqualTo(business2);
        business2.setId(2L);
        assertThat(business1).isNotEqualTo(business2);
        business1.setId(null);
        assertThat(business1).isNotEqualTo(business2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessDTO.class);
        BusinessDTO businessDTO1 = new BusinessDTO();
        businessDTO1.setId(1L);
        BusinessDTO businessDTO2 = new BusinessDTO();
        assertThat(businessDTO1).isNotEqualTo(businessDTO2);
        businessDTO2.setId(businessDTO1.getId());
        assertThat(businessDTO1).isEqualTo(businessDTO2);
        businessDTO2.setId(2L);
        assertThat(businessDTO1).isNotEqualTo(businessDTO2);
        businessDTO1.setId(null);
        assertThat(businessDTO1).isNotEqualTo(businessDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(businessMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(businessMapper.fromId(null)).isNull();
    }
}
