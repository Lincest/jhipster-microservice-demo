package com.roccoshi.micro.web.rest;

import com.roccoshi.micro.MicroNameApp;

import com.roccoshi.micro.domain.Port;
import com.roccoshi.micro.domain.LineCard;
import com.roccoshi.micro.repository.PortRepository;
import com.roccoshi.micro.service.PortService;
import com.roccoshi.micro.service.dto.PortDTO;
import com.roccoshi.micro.service.mapper.PortMapper;
import com.roccoshi.micro.web.rest.errors.ExceptionTranslator;
import com.roccoshi.micro.service.dto.PortCriteria;
import com.roccoshi.micro.service.PortQueryService;

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
 * Test class for the PortResource REST controller.
 *
 * @see PortResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroNameApp.class)
public class PortResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_INFO = "AAAAAAAAAA";
    private static final String UPDATED_INFO = "BBBBBBBBBB";

    @Autowired
    private PortRepository portRepository;

    @Autowired
    private PortMapper portMapper;

    @Autowired
    private PortService portService;

    @Autowired
    private PortQueryService portQueryService;

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

    private MockMvc restPortMockMvc;

    private Port port;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PortResource portResource = new PortResource(portService, portQueryService);
        this.restPortMockMvc = MockMvcBuilders.standaloneSetup(portResource)
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
    public static Port createEntity(EntityManager em) {
        Port port = new Port()
            .name(DEFAULT_NAME)
            .info(DEFAULT_INFO);
        return port;
    }

    @Before
    public void initTest() {
        port = createEntity(em);
    }

    @Test
    @Transactional
    public void createPort() throws Exception {
        int databaseSizeBeforeCreate = portRepository.findAll().size();

        // Create the Port
        PortDTO portDTO = portMapper.toDto(port);
        restPortMockMvc.perform(post("/api/ports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(portDTO)))
            .andExpect(status().isCreated());

        // Validate the Port in the database
        List<Port> portList = portRepository.findAll();
        assertThat(portList).hasSize(databaseSizeBeforeCreate + 1);
        Port testPort = portList.get(portList.size() - 1);
        assertThat(testPort.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPort.getInfo()).isEqualTo(DEFAULT_INFO);
    }

    @Test
    @Transactional
    public void createPortWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = portRepository.findAll().size();

        // Create the Port with an existing ID
        port.setId(1L);
        PortDTO portDTO = portMapper.toDto(port);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPortMockMvc.perform(post("/api/ports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(portDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Port in the database
        List<Port> portList = portRepository.findAll();
        assertThat(portList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllPorts() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        // Get all the portList
        restPortMockMvc.perform(get("/api/ports?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(port.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].info").value(hasItem(DEFAULT_INFO.toString())));
    }
    
    @Test
    @Transactional
    public void getPort() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        // Get the port
        restPortMockMvc.perform(get("/api/ports/{id}", port.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(port.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.info").value(DEFAULT_INFO.toString()));
    }

    @Test
    @Transactional
    public void getAllPortsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        // Get all the portList where name equals to DEFAULT_NAME
        defaultPortShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the portList where name equals to UPDATED_NAME
        defaultPortShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPortsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        // Get all the portList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPortShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the portList where name equals to UPDATED_NAME
        defaultPortShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPortsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        // Get all the portList where name is not null
        defaultPortShouldBeFound("name.specified=true");

        // Get all the portList where name is null
        defaultPortShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllPortsByInfoIsEqualToSomething() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        // Get all the portList where info equals to DEFAULT_INFO
        defaultPortShouldBeFound("info.equals=" + DEFAULT_INFO);

        // Get all the portList where info equals to UPDATED_INFO
        defaultPortShouldNotBeFound("info.equals=" + UPDATED_INFO);
    }

    @Test
    @Transactional
    public void getAllPortsByInfoIsInShouldWork() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        // Get all the portList where info in DEFAULT_INFO or UPDATED_INFO
        defaultPortShouldBeFound("info.in=" + DEFAULT_INFO + "," + UPDATED_INFO);

        // Get all the portList where info equals to UPDATED_INFO
        defaultPortShouldNotBeFound("info.in=" + UPDATED_INFO);
    }

    @Test
    @Transactional
    public void getAllPortsByInfoIsNullOrNotNull() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        // Get all the portList where info is not null
        defaultPortShouldBeFound("info.specified=true");

        // Get all the portList where info is null
        defaultPortShouldNotBeFound("info.specified=false");
    }

    @Test
    @Transactional
    public void getAllPortsByLineCardIsEqualToSomething() throws Exception {
        // Initialize the database
        LineCard lineCard = LineCardResourceIntTest.createEntity(em);
        em.persist(lineCard);
        em.flush();
        port.setLineCard(lineCard);
        portRepository.saveAndFlush(port);
        Long lineCardId = lineCard.getId();

        // Get all the portList where lineCard equals to lineCardId
        defaultPortShouldBeFound("lineCardId.equals=" + lineCardId);

        // Get all the portList where lineCard equals to lineCardId + 1
        defaultPortShouldNotBeFound("lineCardId.equals=" + (lineCardId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultPortShouldBeFound(String filter) throws Exception {
        restPortMockMvc.perform(get("/api/ports?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(port.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].info").value(hasItem(DEFAULT_INFO)));

        // Check, that the count call also returns 1
        restPortMockMvc.perform(get("/api/ports/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultPortShouldNotBeFound(String filter) throws Exception {
        restPortMockMvc.perform(get("/api/ports?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPortMockMvc.perform(get("/api/ports/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingPort() throws Exception {
        // Get the port
        restPortMockMvc.perform(get("/api/ports/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePort() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        int databaseSizeBeforeUpdate = portRepository.findAll().size();

        // Update the port
        Port updatedPort = portRepository.findById(port.getId()).get();
        // Disconnect from session so that the updates on updatedPort are not directly saved in db
        em.detach(updatedPort);
        updatedPort
            .name(UPDATED_NAME)
            .info(UPDATED_INFO);
        PortDTO portDTO = portMapper.toDto(updatedPort);

        restPortMockMvc.perform(put("/api/ports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(portDTO)))
            .andExpect(status().isOk());

        // Validate the Port in the database
        List<Port> portList = portRepository.findAll();
        assertThat(portList).hasSize(databaseSizeBeforeUpdate);
        Port testPort = portList.get(portList.size() - 1);
        assertThat(testPort.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPort.getInfo()).isEqualTo(UPDATED_INFO);
    }

    @Test
    @Transactional
    public void updateNonExistingPort() throws Exception {
        int databaseSizeBeforeUpdate = portRepository.findAll().size();

        // Create the Port
        PortDTO portDTO = portMapper.toDto(port);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPortMockMvc.perform(put("/api/ports")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(portDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Port in the database
        List<Port> portList = portRepository.findAll();
        assertThat(portList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePort() throws Exception {
        // Initialize the database
        portRepository.saveAndFlush(port);

        int databaseSizeBeforeDelete = portRepository.findAll().size();

        // Delete the port
        restPortMockMvc.perform(delete("/api/ports/{id}", port.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Port> portList = portRepository.findAll();
        assertThat(portList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Port.class);
        Port port1 = new Port();
        port1.setId(1L);
        Port port2 = new Port();
        port2.setId(port1.getId());
        assertThat(port1).isEqualTo(port2);
        port2.setId(2L);
        assertThat(port1).isNotEqualTo(port2);
        port1.setId(null);
        assertThat(port1).isNotEqualTo(port2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PortDTO.class);
        PortDTO portDTO1 = new PortDTO();
        portDTO1.setId(1L);
        PortDTO portDTO2 = new PortDTO();
        assertThat(portDTO1).isNotEqualTo(portDTO2);
        portDTO2.setId(portDTO1.getId());
        assertThat(portDTO1).isEqualTo(portDTO2);
        portDTO2.setId(2L);
        assertThat(portDTO1).isNotEqualTo(portDTO2);
        portDTO1.setId(null);
        assertThat(portDTO1).isNotEqualTo(portDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(portMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(portMapper.fromId(null)).isNull();
    }
}
