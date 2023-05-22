package com.roccoshi.micro.web.rest;

import com.roccoshi.micro.MicroNameApp;

import com.roccoshi.micro.domain.NorthNotificationEvents;
import com.roccoshi.micro.repository.NorthNotificationEventsRepository;
import com.roccoshi.micro.web.rest.errors.ExceptionTranslator;

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
 * Test class for the NorthNotificationEventsResource REST controller.
 *
 * @see NorthNotificationEventsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroNameApp.class)
public class NorthNotificationEventsResourceIntTest {

    private static final String DEFAULT_PATCH_ID = "AAAAAAAAAA";
    private static final String UPDATED_PATCH_ID = "BBBBBBBBBB";

    private static final String DEFAULT_TARGET_ID = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_ID = "BBBBBBBBBB";

    private static final String DEFAULT_OPER_STATE = "AAAAAAAAAA";
    private static final String UPDATED_OPER_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENT_STATE = "BBBBBBBBBB";

    @Autowired
    private NorthNotificationEventsRepository northNotificationEventsRepository;

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

    private MockMvc restNorthNotificationEventsMockMvc;

    private NorthNotificationEvents northNotificationEvents;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final NorthNotificationEventsResource northNotificationEventsResource = new NorthNotificationEventsResource(northNotificationEventsRepository);
        this.restNorthNotificationEventsMockMvc = MockMvcBuilders.standaloneSetup(northNotificationEventsResource)
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
    public static NorthNotificationEvents createEntity(EntityManager em) {
        NorthNotificationEvents northNotificationEvents = new NorthNotificationEvents()
            .patchId(DEFAULT_PATCH_ID)
            .targetId(DEFAULT_TARGET_ID)
            .operState(DEFAULT_OPER_STATE)
            .currentState(DEFAULT_CURRENT_STATE);
        return northNotificationEvents;
    }

    @Before
    public void initTest() {
        northNotificationEvents = createEntity(em);
    }

    @Test
    @Transactional
    public void createNorthNotificationEvents() throws Exception {
        int databaseSizeBeforeCreate = northNotificationEventsRepository.findAll().size();

        // Create the NorthNotificationEvents
        restNorthNotificationEventsMockMvc.perform(post("/api/north-notification-events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(northNotificationEvents)))
            .andExpect(status().isCreated());

        // Validate the NorthNotificationEvents in the database
        List<NorthNotificationEvents> northNotificationEventsList = northNotificationEventsRepository.findAll();
        assertThat(northNotificationEventsList).hasSize(databaseSizeBeforeCreate + 1);
        NorthNotificationEvents testNorthNotificationEvents = northNotificationEventsList.get(northNotificationEventsList.size() - 1);
        assertThat(testNorthNotificationEvents.getPatchId()).isEqualTo(DEFAULT_PATCH_ID);
        assertThat(testNorthNotificationEvents.getTargetId()).isEqualTo(DEFAULT_TARGET_ID);
        assertThat(testNorthNotificationEvents.getOperState()).isEqualTo(DEFAULT_OPER_STATE);
        assertThat(testNorthNotificationEvents.getCurrentState()).isEqualTo(DEFAULT_CURRENT_STATE);
    }

    @Test
    @Transactional
    public void createNorthNotificationEventsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = northNotificationEventsRepository.findAll().size();

        // Create the NorthNotificationEvents with an existing ID
        northNotificationEvents.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restNorthNotificationEventsMockMvc.perform(post("/api/north-notification-events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(northNotificationEvents)))
            .andExpect(status().isBadRequest());

        // Validate the NorthNotificationEvents in the database
        List<NorthNotificationEvents> northNotificationEventsList = northNotificationEventsRepository.findAll();
        assertThat(northNotificationEventsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllNorthNotificationEvents() throws Exception {
        // Initialize the database
        northNotificationEventsRepository.saveAndFlush(northNotificationEvents);

        // Get all the northNotificationEventsList
        restNorthNotificationEventsMockMvc.perform(get("/api/north-notification-events?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(northNotificationEvents.getId().intValue())))
            .andExpect(jsonPath("$.[*].patchId").value(hasItem(DEFAULT_PATCH_ID.toString())))
            .andExpect(jsonPath("$.[*].targetId").value(hasItem(DEFAULT_TARGET_ID.toString())))
            .andExpect(jsonPath("$.[*].operState").value(hasItem(DEFAULT_OPER_STATE.toString())))
            .andExpect(jsonPath("$.[*].currentState").value(hasItem(DEFAULT_CURRENT_STATE.toString())));
    }
    
    @Test
    @Transactional
    public void getNorthNotificationEvents() throws Exception {
        // Initialize the database
        northNotificationEventsRepository.saveAndFlush(northNotificationEvents);

        // Get the northNotificationEvents
        restNorthNotificationEventsMockMvc.perform(get("/api/north-notification-events/{id}", northNotificationEvents.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(northNotificationEvents.getId().intValue()))
            .andExpect(jsonPath("$.patchId").value(DEFAULT_PATCH_ID.toString()))
            .andExpect(jsonPath("$.targetId").value(DEFAULT_TARGET_ID.toString()))
            .andExpect(jsonPath("$.operState").value(DEFAULT_OPER_STATE.toString()))
            .andExpect(jsonPath("$.currentState").value(DEFAULT_CURRENT_STATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingNorthNotificationEvents() throws Exception {
        // Get the northNotificationEvents
        restNorthNotificationEventsMockMvc.perform(get("/api/north-notification-events/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNorthNotificationEvents() throws Exception {
        // Initialize the database
        northNotificationEventsRepository.saveAndFlush(northNotificationEvents);

        int databaseSizeBeforeUpdate = northNotificationEventsRepository.findAll().size();

        // Update the northNotificationEvents
        NorthNotificationEvents updatedNorthNotificationEvents = northNotificationEventsRepository.findById(northNotificationEvents.getId()).get();
        // Disconnect from session so that the updates on updatedNorthNotificationEvents are not directly saved in db
        em.detach(updatedNorthNotificationEvents);
        updatedNorthNotificationEvents
            .patchId(UPDATED_PATCH_ID)
            .targetId(UPDATED_TARGET_ID)
            .operState(UPDATED_OPER_STATE)
            .currentState(UPDATED_CURRENT_STATE);

        restNorthNotificationEventsMockMvc.perform(put("/api/north-notification-events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedNorthNotificationEvents)))
            .andExpect(status().isOk());

        // Validate the NorthNotificationEvents in the database
        List<NorthNotificationEvents> northNotificationEventsList = northNotificationEventsRepository.findAll();
        assertThat(northNotificationEventsList).hasSize(databaseSizeBeforeUpdate);
        NorthNotificationEvents testNorthNotificationEvents = northNotificationEventsList.get(northNotificationEventsList.size() - 1);
        assertThat(testNorthNotificationEvents.getPatchId()).isEqualTo(UPDATED_PATCH_ID);
        assertThat(testNorthNotificationEvents.getTargetId()).isEqualTo(UPDATED_TARGET_ID);
        assertThat(testNorthNotificationEvents.getOperState()).isEqualTo(UPDATED_OPER_STATE);
        assertThat(testNorthNotificationEvents.getCurrentState()).isEqualTo(UPDATED_CURRENT_STATE);
    }

    @Test
    @Transactional
    public void updateNonExistingNorthNotificationEvents() throws Exception {
        int databaseSizeBeforeUpdate = northNotificationEventsRepository.findAll().size();

        // Create the NorthNotificationEvents

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNorthNotificationEventsMockMvc.perform(put("/api/north-notification-events")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(northNotificationEvents)))
            .andExpect(status().isBadRequest());

        // Validate the NorthNotificationEvents in the database
        List<NorthNotificationEvents> northNotificationEventsList = northNotificationEventsRepository.findAll();
        assertThat(northNotificationEventsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteNorthNotificationEvents() throws Exception {
        // Initialize the database
        northNotificationEventsRepository.saveAndFlush(northNotificationEvents);

        int databaseSizeBeforeDelete = northNotificationEventsRepository.findAll().size();

        // Delete the northNotificationEvents
        restNorthNotificationEventsMockMvc.perform(delete("/api/north-notification-events/{id}", northNotificationEvents.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<NorthNotificationEvents> northNotificationEventsList = northNotificationEventsRepository.findAll();
        assertThat(northNotificationEventsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NorthNotificationEvents.class);
        NorthNotificationEvents northNotificationEvents1 = new NorthNotificationEvents();
        northNotificationEvents1.setId(1L);
        NorthNotificationEvents northNotificationEvents2 = new NorthNotificationEvents();
        northNotificationEvents2.setId(northNotificationEvents1.getId());
        assertThat(northNotificationEvents1).isEqualTo(northNotificationEvents2);
        northNotificationEvents2.setId(2L);
        assertThat(northNotificationEvents1).isNotEqualTo(northNotificationEvents2);
        northNotificationEvents1.setId(null);
        assertThat(northNotificationEvents1).isNotEqualTo(northNotificationEvents2);
    }
}
