package com.roccoshi.micro.web.rest;

import com.roccoshi.micro.MicroNameApp;

import com.roccoshi.micro.domain.LineCard;
import com.roccoshi.micro.domain.Port;
import com.roccoshi.micro.domain.Chassis;
import com.roccoshi.micro.repository.LineCardRepository;
import com.roccoshi.micro.service.LineCardService;
import com.roccoshi.micro.service.dto.LineCardDTO;
import com.roccoshi.micro.service.mapper.LineCardMapper;
import com.roccoshi.micro.web.rest.errors.ExceptionTranslator;
import com.roccoshi.micro.service.dto.LineCardCriteria;
import com.roccoshi.micro.service.LineCardQueryService;

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
 * Test class for the LineCardResource REST controller.
 *
 * @see LineCardResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroNameApp.class)
public class LineCardResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_INFO = "AAAAAAAAAA";
    private static final String UPDATED_INFO = "BBBBBBBBBB";

    @Autowired
    private LineCardRepository lineCardRepository;

    @Autowired
    private LineCardMapper lineCardMapper;

    @Autowired
    private LineCardService lineCardService;

    @Autowired
    private LineCardQueryService lineCardQueryService;

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

    private MockMvc restLineCardMockMvc;

    private LineCard lineCard;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LineCardResource lineCardResource = new LineCardResource(lineCardService, lineCardQueryService);
        this.restLineCardMockMvc = MockMvcBuilders.standaloneSetup(lineCardResource)
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
    public static LineCard createEntity(EntityManager em) {
        LineCard lineCard = new LineCard()
            .name(DEFAULT_NAME)
            .info(DEFAULT_INFO);
        return lineCard;
    }

    @Before
    public void initTest() {
        lineCard = createEntity(em);
    }

    @Test
    @Transactional
    public void createLineCard() throws Exception {
        int databaseSizeBeforeCreate = lineCardRepository.findAll().size();

        // Create the LineCard
        LineCardDTO lineCardDTO = lineCardMapper.toDto(lineCard);
        restLineCardMockMvc.perform(post("/api/line-cards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lineCardDTO)))
            .andExpect(status().isCreated());

        // Validate the LineCard in the database
        List<LineCard> lineCardList = lineCardRepository.findAll();
        assertThat(lineCardList).hasSize(databaseSizeBeforeCreate + 1);
        LineCard testLineCard = lineCardList.get(lineCardList.size() - 1);
        assertThat(testLineCard.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLineCard.getInfo()).isEqualTo(DEFAULT_INFO);
    }

    @Test
    @Transactional
    public void createLineCardWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = lineCardRepository.findAll().size();

        // Create the LineCard with an existing ID
        lineCard.setId(1L);
        LineCardDTO lineCardDTO = lineCardMapper.toDto(lineCard);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLineCardMockMvc.perform(post("/api/line-cards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lineCardDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LineCard in the database
        List<LineCard> lineCardList = lineCardRepository.findAll();
        assertThat(lineCardList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllLineCards() throws Exception {
        // Initialize the database
        lineCardRepository.saveAndFlush(lineCard);

        // Get all the lineCardList
        restLineCardMockMvc.perform(get("/api/line-cards?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lineCard.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].info").value(hasItem(DEFAULT_INFO.toString())));
    }
    
    @Test
    @Transactional
    public void getLineCard() throws Exception {
        // Initialize the database
        lineCardRepository.saveAndFlush(lineCard);

        // Get the lineCard
        restLineCardMockMvc.perform(get("/api/line-cards/{id}", lineCard.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(lineCard.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.info").value(DEFAULT_INFO.toString()));
    }

    @Test
    @Transactional
    public void getAllLineCardsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        lineCardRepository.saveAndFlush(lineCard);

        // Get all the lineCardList where name equals to DEFAULT_NAME
        defaultLineCardShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the lineCardList where name equals to UPDATED_NAME
        defaultLineCardShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllLineCardsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        lineCardRepository.saveAndFlush(lineCard);

        // Get all the lineCardList where name in DEFAULT_NAME or UPDATED_NAME
        defaultLineCardShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the lineCardList where name equals to UPDATED_NAME
        defaultLineCardShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllLineCardsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        lineCardRepository.saveAndFlush(lineCard);

        // Get all the lineCardList where name is not null
        defaultLineCardShouldBeFound("name.specified=true");

        // Get all the lineCardList where name is null
        defaultLineCardShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllLineCardsByInfoIsEqualToSomething() throws Exception {
        // Initialize the database
        lineCardRepository.saveAndFlush(lineCard);

        // Get all the lineCardList where info equals to DEFAULT_INFO
        defaultLineCardShouldBeFound("info.equals=" + DEFAULT_INFO);

        // Get all the lineCardList where info equals to UPDATED_INFO
        defaultLineCardShouldNotBeFound("info.equals=" + UPDATED_INFO);
    }

    @Test
    @Transactional
    public void getAllLineCardsByInfoIsInShouldWork() throws Exception {
        // Initialize the database
        lineCardRepository.saveAndFlush(lineCard);

        // Get all the lineCardList where info in DEFAULT_INFO or UPDATED_INFO
        defaultLineCardShouldBeFound("info.in=" + DEFAULT_INFO + "," + UPDATED_INFO);

        // Get all the lineCardList where info equals to UPDATED_INFO
        defaultLineCardShouldNotBeFound("info.in=" + UPDATED_INFO);
    }

    @Test
    @Transactional
    public void getAllLineCardsByInfoIsNullOrNotNull() throws Exception {
        // Initialize the database
        lineCardRepository.saveAndFlush(lineCard);

        // Get all the lineCardList where info is not null
        defaultLineCardShouldBeFound("info.specified=true");

        // Get all the lineCardList where info is null
        defaultLineCardShouldNotBeFound("info.specified=false");
    }

    @Test
    @Transactional
    public void getAllLineCardsByPortIsEqualToSomething() throws Exception {
        // Initialize the database
        Port port = PortResourceIntTest.createEntity(em);
        em.persist(port);
        em.flush();
        lineCard.addPort(port);
        lineCardRepository.saveAndFlush(lineCard);
        Long portId = port.getId();

        // Get all the lineCardList where port equals to portId
        defaultLineCardShouldBeFound("portId.equals=" + portId);

        // Get all the lineCardList where port equals to portId + 1
        defaultLineCardShouldNotBeFound("portId.equals=" + (portId + 1));
    }


    @Test
    @Transactional
    public void getAllLineCardsByChassisIsEqualToSomething() throws Exception {
        // Initialize the database
        Chassis chassis = ChassisResourceIntTest.createEntity(em);
        em.persist(chassis);
        em.flush();
        lineCard.setChassis(chassis);
        lineCardRepository.saveAndFlush(lineCard);
        Long chassisId = chassis.getId();

        // Get all the lineCardList where chassis equals to chassisId
        defaultLineCardShouldBeFound("chassisId.equals=" + chassisId);

        // Get all the lineCardList where chassis equals to chassisId + 1
        defaultLineCardShouldNotBeFound("chassisId.equals=" + (chassisId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultLineCardShouldBeFound(String filter) throws Exception {
        restLineCardMockMvc.perform(get("/api/line-cards?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lineCard.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].info").value(hasItem(DEFAULT_INFO)));

        // Check, that the count call also returns 1
        restLineCardMockMvc.perform(get("/api/line-cards/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultLineCardShouldNotBeFound(String filter) throws Exception {
        restLineCardMockMvc.perform(get("/api/line-cards?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLineCardMockMvc.perform(get("/api/line-cards/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingLineCard() throws Exception {
        // Get the lineCard
        restLineCardMockMvc.perform(get("/api/line-cards/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLineCard() throws Exception {
        // Initialize the database
        lineCardRepository.saveAndFlush(lineCard);

        int databaseSizeBeforeUpdate = lineCardRepository.findAll().size();

        // Update the lineCard
        LineCard updatedLineCard = lineCardRepository.findById(lineCard.getId()).get();
        // Disconnect from session so that the updates on updatedLineCard are not directly saved in db
        em.detach(updatedLineCard);
        updatedLineCard
            .name(UPDATED_NAME)
            .info(UPDATED_INFO);
        LineCardDTO lineCardDTO = lineCardMapper.toDto(updatedLineCard);

        restLineCardMockMvc.perform(put("/api/line-cards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lineCardDTO)))
            .andExpect(status().isOk());

        // Validate the LineCard in the database
        List<LineCard> lineCardList = lineCardRepository.findAll();
        assertThat(lineCardList).hasSize(databaseSizeBeforeUpdate);
        LineCard testLineCard = lineCardList.get(lineCardList.size() - 1);
        assertThat(testLineCard.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLineCard.getInfo()).isEqualTo(UPDATED_INFO);
    }

    @Test
    @Transactional
    public void updateNonExistingLineCard() throws Exception {
        int databaseSizeBeforeUpdate = lineCardRepository.findAll().size();

        // Create the LineCard
        LineCardDTO lineCardDTO = lineCardMapper.toDto(lineCard);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLineCardMockMvc.perform(put("/api/line-cards")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lineCardDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LineCard in the database
        List<LineCard> lineCardList = lineCardRepository.findAll();
        assertThat(lineCardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLineCard() throws Exception {
        // Initialize the database
        lineCardRepository.saveAndFlush(lineCard);

        int databaseSizeBeforeDelete = lineCardRepository.findAll().size();

        // Delete the lineCard
        restLineCardMockMvc.perform(delete("/api/line-cards/{id}", lineCard.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<LineCard> lineCardList = lineCardRepository.findAll();
        assertThat(lineCardList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LineCard.class);
        LineCard lineCard1 = new LineCard();
        lineCard1.setId(1L);
        LineCard lineCard2 = new LineCard();
        lineCard2.setId(lineCard1.getId());
        assertThat(lineCard1).isEqualTo(lineCard2);
        lineCard2.setId(2L);
        assertThat(lineCard1).isNotEqualTo(lineCard2);
        lineCard1.setId(null);
        assertThat(lineCard1).isNotEqualTo(lineCard2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LineCardDTO.class);
        LineCardDTO lineCardDTO1 = new LineCardDTO();
        lineCardDTO1.setId(1L);
        LineCardDTO lineCardDTO2 = new LineCardDTO();
        assertThat(lineCardDTO1).isNotEqualTo(lineCardDTO2);
        lineCardDTO2.setId(lineCardDTO1.getId());
        assertThat(lineCardDTO1).isEqualTo(lineCardDTO2);
        lineCardDTO2.setId(2L);
        assertThat(lineCardDTO1).isNotEqualTo(lineCardDTO2);
        lineCardDTO1.setId(null);
        assertThat(lineCardDTO1).isNotEqualTo(lineCardDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(lineCardMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(lineCardMapper.fromId(null)).isNull();
    }
}
