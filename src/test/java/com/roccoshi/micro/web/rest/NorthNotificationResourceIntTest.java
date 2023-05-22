package com.roccoshi.micro.web.rest;

import com.roccoshi.micro.MicroNameApp;

import com.roccoshi.micro.domain.NorthNotification;
import com.roccoshi.micro.repository.NorthNotificationRepository;
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
 * Test class for the NorthNotificationResource REST controller.
 *
 * @see NorthNotificationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroNameApp.class)
public class NorthNotificationResourceIntTest {

    private static final String DEFAULT_IDENTIFIER = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFIER = "BBBBBBBBBB";

    private static final String DEFAULT_ENCODING = "AAAAAAAAAA";
    private static final String UPDATED_ENCODING = "BBBBBBBBBB";

    private static final String DEFAULT_TOPIC = "AAAAAAAAAA";
    private static final String UPDATED_TOPIC = "BBBBBBBBBB";

    private static final String DEFAULT_OBJECT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_OBJECT_TYPE = "BBBBBBBBBB";

    @Autowired
    private NorthNotificationRepository northNotificationRepository;

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

    private MockMvc restNorthNotificationMockMvc;

    private NorthNotification northNotification;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final NorthNotificationResource northNotificationResource = new NorthNotificationResource(northNotificationRepository);
        this.restNorthNotificationMockMvc = MockMvcBuilders.standaloneSetup(northNotificationResource)
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
    public static NorthNotification createEntity(EntityManager em) {
        NorthNotification northNotification = new NorthNotification()
            .identifier(DEFAULT_IDENTIFIER)
            .encoding(DEFAULT_ENCODING)
            .topic(DEFAULT_TOPIC)
            .objectType(DEFAULT_OBJECT_TYPE);
        return northNotification;
    }

    @Before
    public void initTest() {
        northNotification = createEntity(em);
    }

    @Test
    @Transactional
    public void createNorthNotification() throws Exception {
        int databaseSizeBeforeCreate = northNotificationRepository.findAll().size();

        // Create the NorthNotification
        restNorthNotificationMockMvc.perform(post("/api/north-notifications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(northNotification)))
            .andExpect(status().isCreated());

        // Validate the NorthNotification in the database
        List<NorthNotification> northNotificationList = northNotificationRepository.findAll();
        assertThat(northNotificationList).hasSize(databaseSizeBeforeCreate + 1);
        NorthNotification testNorthNotification = northNotificationList.get(northNotificationList.size() - 1);
        assertThat(testNorthNotification.getIdentifier()).isEqualTo(DEFAULT_IDENTIFIER);
        assertThat(testNorthNotification.getEncoding()).isEqualTo(DEFAULT_ENCODING);
        assertThat(testNorthNotification.getTopic()).isEqualTo(DEFAULT_TOPIC);
        assertThat(testNorthNotification.getObjectType()).isEqualTo(DEFAULT_OBJECT_TYPE);
    }

    @Test
    @Transactional
    public void createNorthNotificationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = northNotificationRepository.findAll().size();

        // Create the NorthNotification with an existing ID
        northNotification.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restNorthNotificationMockMvc.perform(post("/api/north-notifications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(northNotification)))
            .andExpect(status().isBadRequest());

        // Validate the NorthNotification in the database
        List<NorthNotification> northNotificationList = northNotificationRepository.findAll();
        assertThat(northNotificationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllNorthNotifications() throws Exception {
        // Initialize the database
        northNotificationRepository.saveAndFlush(northNotification);

        // Get all the northNotificationList
        restNorthNotificationMockMvc.perform(get("/api/north-notifications?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(northNotification.getId().intValue())))
            .andExpect(jsonPath("$.[*].identifier").value(hasItem(DEFAULT_IDENTIFIER.toString())))
            .andExpect(jsonPath("$.[*].encoding").value(hasItem(DEFAULT_ENCODING.toString())))
            .andExpect(jsonPath("$.[*].topic").value(hasItem(DEFAULT_TOPIC.toString())))
            .andExpect(jsonPath("$.[*].objectType").value(hasItem(DEFAULT_OBJECT_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getNorthNotification() throws Exception {
        // Initialize the database
        northNotificationRepository.saveAndFlush(northNotification);

        // Get the northNotification
        restNorthNotificationMockMvc.perform(get("/api/north-notifications/{id}", northNotification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(northNotification.getId().intValue()))
            .andExpect(jsonPath("$.identifier").value(DEFAULT_IDENTIFIER.toString()))
            .andExpect(jsonPath("$.encoding").value(DEFAULT_ENCODING.toString()))
            .andExpect(jsonPath("$.topic").value(DEFAULT_TOPIC.toString()))
            .andExpect(jsonPath("$.objectType").value(DEFAULT_OBJECT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingNorthNotification() throws Exception {
        // Get the northNotification
        restNorthNotificationMockMvc.perform(get("/api/north-notifications/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNorthNotification() throws Exception {
        // Initialize the database
        northNotificationRepository.saveAndFlush(northNotification);

        int databaseSizeBeforeUpdate = northNotificationRepository.findAll().size();

        // Update the northNotification
        NorthNotification updatedNorthNotification = northNotificationRepository.findById(northNotification.getId()).get();
        // Disconnect from session so that the updates on updatedNorthNotification are not directly saved in db
        em.detach(updatedNorthNotification);
        updatedNorthNotification
            .identifier(UPDATED_IDENTIFIER)
            .encoding(UPDATED_ENCODING)
            .topic(UPDATED_TOPIC)
            .objectType(UPDATED_OBJECT_TYPE);

        restNorthNotificationMockMvc.perform(put("/api/north-notifications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedNorthNotification)))
            .andExpect(status().isOk());

        // Validate the NorthNotification in the database
        List<NorthNotification> northNotificationList = northNotificationRepository.findAll();
        assertThat(northNotificationList).hasSize(databaseSizeBeforeUpdate);
        NorthNotification testNorthNotification = northNotificationList.get(northNotificationList.size() - 1);
        assertThat(testNorthNotification.getIdentifier()).isEqualTo(UPDATED_IDENTIFIER);
        assertThat(testNorthNotification.getEncoding()).isEqualTo(UPDATED_ENCODING);
        assertThat(testNorthNotification.getTopic()).isEqualTo(UPDATED_TOPIC);
        assertThat(testNorthNotification.getObjectType()).isEqualTo(UPDATED_OBJECT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingNorthNotification() throws Exception {
        int databaseSizeBeforeUpdate = northNotificationRepository.findAll().size();

        // Create the NorthNotification

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNorthNotificationMockMvc.perform(put("/api/north-notifications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(northNotification)))
            .andExpect(status().isBadRequest());

        // Validate the NorthNotification in the database
        List<NorthNotification> northNotificationList = northNotificationRepository.findAll();
        assertThat(northNotificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteNorthNotification() throws Exception {
        // Initialize the database
        northNotificationRepository.saveAndFlush(northNotification);

        int databaseSizeBeforeDelete = northNotificationRepository.findAll().size();

        // Delete the northNotification
        restNorthNotificationMockMvc.perform(delete("/api/north-notifications/{id}", northNotification.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<NorthNotification> northNotificationList = northNotificationRepository.findAll();
        assertThat(northNotificationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NorthNotification.class);
        NorthNotification northNotification1 = new NorthNotification();
        northNotification1.setId(1L);
        NorthNotification northNotification2 = new NorthNotification();
        northNotification2.setId(northNotification1.getId());
        assertThat(northNotification1).isEqualTo(northNotification2);
        northNotification2.setId(2L);
        assertThat(northNotification1).isNotEqualTo(northNotification2);
        northNotification1.setId(null);
        assertThat(northNotification1).isNotEqualTo(northNotification2);
    }
}
