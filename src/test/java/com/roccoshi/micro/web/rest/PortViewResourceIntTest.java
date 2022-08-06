package com.roccoshi.micro.web.rest;

import com.roccoshi.micro.MicroNameApp;

import com.roccoshi.micro.domain.PortView;
import com.roccoshi.micro.repository.PortViewRepository;
import com.roccoshi.micro.service.PortViewService;
import com.roccoshi.micro.web.rest.errors.ExceptionTranslator;
import com.roccoshi.micro.service.dto.PortViewCriteria;
import com.roccoshi.micro.service.PortViewQueryService;

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
 * Test class for the PortViewResource REST controller.
 *
 * @see PortViewResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroNameApp.class)
public class PortViewResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_INFO = "AAAAAAAAAA";
    private static final String UPDATED_INFO = "BBBBBBBBBB";

    private static final Long DEFAULT_CHASSIS_ID = 1L;
    private static final Long UPDATED_CHASSIS_ID = 2L;

    private static final String DEFAULT_CHASSIS_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CHASSIS_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_LINE_CARD_ID = 1L;
    private static final Long UPDATED_LINE_CARD_ID = 2L;

    private static final String DEFAULT_LINE_CARD_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LINE_CARD_NAME = "BBBBBBBBBB";

    @Autowired
    private PortViewRepository portViewRepository;

    @Autowired
    private PortViewService portViewService;

    @Autowired
    private PortViewQueryService portViewQueryService;

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

    private MockMvc restPortViewMockMvc;

    private PortView portView;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PortViewResource portViewResource = new PortViewResource(portViewService, portViewQueryService);
        this.restPortViewMockMvc = MockMvcBuilders.standaloneSetup(portViewResource)
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
    public static PortView createEntity(EntityManager em) {
        PortView portView = new PortView()
            .name(DEFAULT_NAME)
            .info(DEFAULT_INFO)
            .chassisId(DEFAULT_CHASSIS_ID)
            .chassisName(DEFAULT_CHASSIS_NAME)
            .lineCardId(DEFAULT_LINE_CARD_ID)
            .lineCardName(DEFAULT_LINE_CARD_NAME);
        return portView;
    }

    @Before
    public void initTest() {
        portView = createEntity(em);
    }

    @Test
    @Transactional
    public void createPortView() throws Exception {
        int databaseSizeBeforeCreate = portViewRepository.findAll().size();

        // Create the PortView
        restPortViewMockMvc.perform(post("/api/port-views")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(portView)))
            .andExpect(status().isCreated());

        // Validate the PortView in the database
        List<PortView> portViewList = portViewRepository.findAll();
        assertThat(portViewList).hasSize(databaseSizeBeforeCreate + 1);
        PortView testPortView = portViewList.get(portViewList.size() - 1);
        assertThat(testPortView.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPortView.getInfo()).isEqualTo(DEFAULT_INFO);
        assertThat(testPortView.getChassisId()).isEqualTo(DEFAULT_CHASSIS_ID);
        assertThat(testPortView.getChassisName()).isEqualTo(DEFAULT_CHASSIS_NAME);
        assertThat(testPortView.getLineCardId()).isEqualTo(DEFAULT_LINE_CARD_ID);
        assertThat(testPortView.getLineCardName()).isEqualTo(DEFAULT_LINE_CARD_NAME);
    }

    @Test
    @Transactional
    public void createPortViewWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = portViewRepository.findAll().size();

        // Create the PortView with an existing ID
        portView.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPortViewMockMvc.perform(post("/api/port-views")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(portView)))
            .andExpect(status().isBadRequest());

        // Validate the PortView in the database
        List<PortView> portViewList = portViewRepository.findAll();
        assertThat(portViewList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllPortViews() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList
        restPortViewMockMvc.perform(get("/api/port-views?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(portView.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].info").value(hasItem(DEFAULT_INFO.toString())))
            .andExpect(jsonPath("$.[*].chassisId").value(hasItem(DEFAULT_CHASSIS_ID.intValue())))
            .andExpect(jsonPath("$.[*].chassisName").value(hasItem(DEFAULT_CHASSIS_NAME.toString())))
            .andExpect(jsonPath("$.[*].lineCardId").value(hasItem(DEFAULT_LINE_CARD_ID.intValue())))
            .andExpect(jsonPath("$.[*].lineCardName").value(hasItem(DEFAULT_LINE_CARD_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getPortView() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get the portView
        restPortViewMockMvc.perform(get("/api/port-views/{id}", portView.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(portView.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.info").value(DEFAULT_INFO.toString()))
            .andExpect(jsonPath("$.chassisId").value(DEFAULT_CHASSIS_ID.intValue()))
            .andExpect(jsonPath("$.chassisName").value(DEFAULT_CHASSIS_NAME.toString()))
            .andExpect(jsonPath("$.lineCardId").value(DEFAULT_LINE_CARD_ID.intValue()))
            .andExpect(jsonPath("$.lineCardName").value(DEFAULT_LINE_CARD_NAME.toString()));
    }

    @Test
    @Transactional
    public void getAllPortViewsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where name equals to DEFAULT_NAME
        defaultPortViewShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the portViewList where name equals to UPDATED_NAME
        defaultPortViewShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPortViewsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPortViewShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the portViewList where name equals to UPDATED_NAME
        defaultPortViewShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPortViewsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where name is not null
        defaultPortViewShouldBeFound("name.specified=true");

        // Get all the portViewList where name is null
        defaultPortViewShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllPortViewsByInfoIsEqualToSomething() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where info equals to DEFAULT_INFO
        defaultPortViewShouldBeFound("info.equals=" + DEFAULT_INFO);

        // Get all the portViewList where info equals to UPDATED_INFO
        defaultPortViewShouldNotBeFound("info.equals=" + UPDATED_INFO);
    }

    @Test
    @Transactional
    public void getAllPortViewsByInfoIsInShouldWork() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where info in DEFAULT_INFO or UPDATED_INFO
        defaultPortViewShouldBeFound("info.in=" + DEFAULT_INFO + "," + UPDATED_INFO);

        // Get all the portViewList where info equals to UPDATED_INFO
        defaultPortViewShouldNotBeFound("info.in=" + UPDATED_INFO);
    }

    @Test
    @Transactional
    public void getAllPortViewsByInfoIsNullOrNotNull() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where info is not null
        defaultPortViewShouldBeFound("info.specified=true");

        // Get all the portViewList where info is null
        defaultPortViewShouldNotBeFound("info.specified=false");
    }

    @Test
    @Transactional
    public void getAllPortViewsByChassisIdIsEqualToSomething() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where chassisId equals to DEFAULT_CHASSIS_ID
        defaultPortViewShouldBeFound("chassisId.equals=" + DEFAULT_CHASSIS_ID);

        // Get all the portViewList where chassisId equals to UPDATED_CHASSIS_ID
        defaultPortViewShouldNotBeFound("chassisId.equals=" + UPDATED_CHASSIS_ID);
    }

    @Test
    @Transactional
    public void getAllPortViewsByChassisIdIsInShouldWork() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where chassisId in DEFAULT_CHASSIS_ID or UPDATED_CHASSIS_ID
        defaultPortViewShouldBeFound("chassisId.in=" + DEFAULT_CHASSIS_ID + "," + UPDATED_CHASSIS_ID);

        // Get all the portViewList where chassisId equals to UPDATED_CHASSIS_ID
        defaultPortViewShouldNotBeFound("chassisId.in=" + UPDATED_CHASSIS_ID);
    }

    @Test
    @Transactional
    public void getAllPortViewsByChassisIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where chassisId is not null
        defaultPortViewShouldBeFound("chassisId.specified=true");

        // Get all the portViewList where chassisId is null
        defaultPortViewShouldNotBeFound("chassisId.specified=false");
    }

    @Test
    @Transactional
    public void getAllPortViewsByChassisIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where chassisId greater than or equals to DEFAULT_CHASSIS_ID
        defaultPortViewShouldBeFound("chassisId.greaterOrEqualThan=" + DEFAULT_CHASSIS_ID);

        // Get all the portViewList where chassisId greater than or equals to UPDATED_CHASSIS_ID
        defaultPortViewShouldNotBeFound("chassisId.greaterOrEqualThan=" + UPDATED_CHASSIS_ID);
    }

    @Test
    @Transactional
    public void getAllPortViewsByChassisIdIsLessThanSomething() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where chassisId less than or equals to DEFAULT_CHASSIS_ID
        defaultPortViewShouldNotBeFound("chassisId.lessThan=" + DEFAULT_CHASSIS_ID);

        // Get all the portViewList where chassisId less than or equals to UPDATED_CHASSIS_ID
        defaultPortViewShouldBeFound("chassisId.lessThan=" + UPDATED_CHASSIS_ID);
    }


    @Test
    @Transactional
    public void getAllPortViewsByChassisNameIsEqualToSomething() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where chassisName equals to DEFAULT_CHASSIS_NAME
        defaultPortViewShouldBeFound("chassisName.equals=" + DEFAULT_CHASSIS_NAME);

        // Get all the portViewList where chassisName equals to UPDATED_CHASSIS_NAME
        defaultPortViewShouldNotBeFound("chassisName.equals=" + UPDATED_CHASSIS_NAME);
    }

    @Test
    @Transactional
    public void getAllPortViewsByChassisNameIsInShouldWork() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where chassisName in DEFAULT_CHASSIS_NAME or UPDATED_CHASSIS_NAME
        defaultPortViewShouldBeFound("chassisName.in=" + DEFAULT_CHASSIS_NAME + "," + UPDATED_CHASSIS_NAME);

        // Get all the portViewList where chassisName equals to UPDATED_CHASSIS_NAME
        defaultPortViewShouldNotBeFound("chassisName.in=" + UPDATED_CHASSIS_NAME);
    }

    @Test
    @Transactional
    public void getAllPortViewsByChassisNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where chassisName is not null
        defaultPortViewShouldBeFound("chassisName.specified=true");

        // Get all the portViewList where chassisName is null
        defaultPortViewShouldNotBeFound("chassisName.specified=false");
    }

    @Test
    @Transactional
    public void getAllPortViewsByLineCardIdIsEqualToSomething() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where lineCardId equals to DEFAULT_LINE_CARD_ID
        defaultPortViewShouldBeFound("lineCardId.equals=" + DEFAULT_LINE_CARD_ID);

        // Get all the portViewList where lineCardId equals to UPDATED_LINE_CARD_ID
        defaultPortViewShouldNotBeFound("lineCardId.equals=" + UPDATED_LINE_CARD_ID);
    }

    @Test
    @Transactional
    public void getAllPortViewsByLineCardIdIsInShouldWork() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where lineCardId in DEFAULT_LINE_CARD_ID or UPDATED_LINE_CARD_ID
        defaultPortViewShouldBeFound("lineCardId.in=" + DEFAULT_LINE_CARD_ID + "," + UPDATED_LINE_CARD_ID);

        // Get all the portViewList where lineCardId equals to UPDATED_LINE_CARD_ID
        defaultPortViewShouldNotBeFound("lineCardId.in=" + UPDATED_LINE_CARD_ID);
    }

    @Test
    @Transactional
    public void getAllPortViewsByLineCardIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where lineCardId is not null
        defaultPortViewShouldBeFound("lineCardId.specified=true");

        // Get all the portViewList where lineCardId is null
        defaultPortViewShouldNotBeFound("lineCardId.specified=false");
    }

    @Test
    @Transactional
    public void getAllPortViewsByLineCardIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where lineCardId greater than or equals to DEFAULT_LINE_CARD_ID
        defaultPortViewShouldBeFound("lineCardId.greaterOrEqualThan=" + DEFAULT_LINE_CARD_ID);

        // Get all the portViewList where lineCardId greater than or equals to UPDATED_LINE_CARD_ID
        defaultPortViewShouldNotBeFound("lineCardId.greaterOrEqualThan=" + UPDATED_LINE_CARD_ID);
    }

    @Test
    @Transactional
    public void getAllPortViewsByLineCardIdIsLessThanSomething() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where lineCardId less than or equals to DEFAULT_LINE_CARD_ID
        defaultPortViewShouldNotBeFound("lineCardId.lessThan=" + DEFAULT_LINE_CARD_ID);

        // Get all the portViewList where lineCardId less than or equals to UPDATED_LINE_CARD_ID
        defaultPortViewShouldBeFound("lineCardId.lessThan=" + UPDATED_LINE_CARD_ID);
    }


    @Test
    @Transactional
    public void getAllPortViewsByLineCardNameIsEqualToSomething() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where lineCardName equals to DEFAULT_LINE_CARD_NAME
        defaultPortViewShouldBeFound("lineCardName.equals=" + DEFAULT_LINE_CARD_NAME);

        // Get all the portViewList where lineCardName equals to UPDATED_LINE_CARD_NAME
        defaultPortViewShouldNotBeFound("lineCardName.equals=" + UPDATED_LINE_CARD_NAME);
    }

    @Test
    @Transactional
    public void getAllPortViewsByLineCardNameIsInShouldWork() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where lineCardName in DEFAULT_LINE_CARD_NAME or UPDATED_LINE_CARD_NAME
        defaultPortViewShouldBeFound("lineCardName.in=" + DEFAULT_LINE_CARD_NAME + "," + UPDATED_LINE_CARD_NAME);

        // Get all the portViewList where lineCardName equals to UPDATED_LINE_CARD_NAME
        defaultPortViewShouldNotBeFound("lineCardName.in=" + UPDATED_LINE_CARD_NAME);
    }

    @Test
    @Transactional
    public void getAllPortViewsByLineCardNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        portViewRepository.saveAndFlush(portView);

        // Get all the portViewList where lineCardName is not null
        defaultPortViewShouldBeFound("lineCardName.specified=true");

        // Get all the portViewList where lineCardName is null
        defaultPortViewShouldNotBeFound("lineCardName.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultPortViewShouldBeFound(String filter) throws Exception {
        restPortViewMockMvc.perform(get("/api/port-views?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(portView.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].info").value(hasItem(DEFAULT_INFO)))
            .andExpect(jsonPath("$.[*].chassisId").value(hasItem(DEFAULT_CHASSIS_ID.intValue())))
            .andExpect(jsonPath("$.[*].chassisName").value(hasItem(DEFAULT_CHASSIS_NAME)))
            .andExpect(jsonPath("$.[*].lineCardId").value(hasItem(DEFAULT_LINE_CARD_ID.intValue())))
            .andExpect(jsonPath("$.[*].lineCardName").value(hasItem(DEFAULT_LINE_CARD_NAME)));

        // Check, that the count call also returns 1
        restPortViewMockMvc.perform(get("/api/port-views/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultPortViewShouldNotBeFound(String filter) throws Exception {
        restPortViewMockMvc.perform(get("/api/port-views?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPortViewMockMvc.perform(get("/api/port-views/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingPortView() throws Exception {
        // Get the portView
        restPortViewMockMvc.perform(get("/api/port-views/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePortView() throws Exception {
        // Initialize the database
        portViewService.save(portView);

        int databaseSizeBeforeUpdate = portViewRepository.findAll().size();

        // Update the portView
        PortView updatedPortView = portViewRepository.findById(portView.getId()).get();
        // Disconnect from session so that the updates on updatedPortView are not directly saved in db
        em.detach(updatedPortView);
        updatedPortView
            .name(UPDATED_NAME)
            .info(UPDATED_INFO)
            .chassisId(UPDATED_CHASSIS_ID)
            .chassisName(UPDATED_CHASSIS_NAME)
            .lineCardId(UPDATED_LINE_CARD_ID)
            .lineCardName(UPDATED_LINE_CARD_NAME);

        restPortViewMockMvc.perform(put("/api/port-views")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPortView)))
            .andExpect(status().isOk());

        // Validate the PortView in the database
        List<PortView> portViewList = portViewRepository.findAll();
        assertThat(portViewList).hasSize(databaseSizeBeforeUpdate);
        PortView testPortView = portViewList.get(portViewList.size() - 1);
        assertThat(testPortView.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPortView.getInfo()).isEqualTo(UPDATED_INFO);
        assertThat(testPortView.getChassisId()).isEqualTo(UPDATED_CHASSIS_ID);
        assertThat(testPortView.getChassisName()).isEqualTo(UPDATED_CHASSIS_NAME);
        assertThat(testPortView.getLineCardId()).isEqualTo(UPDATED_LINE_CARD_ID);
        assertThat(testPortView.getLineCardName()).isEqualTo(UPDATED_LINE_CARD_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingPortView() throws Exception {
        int databaseSizeBeforeUpdate = portViewRepository.findAll().size();

        // Create the PortView

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPortViewMockMvc.perform(put("/api/port-views")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(portView)))
            .andExpect(status().isBadRequest());

        // Validate the PortView in the database
        List<PortView> portViewList = portViewRepository.findAll();
        assertThat(portViewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePortView() throws Exception {
        // Initialize the database
        portViewService.save(portView);

        int databaseSizeBeforeDelete = portViewRepository.findAll().size();

        // Delete the portView
        restPortViewMockMvc.perform(delete("/api/port-views/{id}", portView.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<PortView> portViewList = portViewRepository.findAll();
        assertThat(portViewList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PortView.class);
        PortView portView1 = new PortView();
        portView1.setId(1L);
        PortView portView2 = new PortView();
        portView2.setId(portView1.getId());
        assertThat(portView1).isEqualTo(portView2);
        portView2.setId(2L);
        assertThat(portView1).isNotEqualTo(portView2);
        portView1.setId(null);
        assertThat(portView1).isNotEqualTo(portView2);
    }
}
