package com.roccoshi.micro.web.rest;

import com.roccoshi.micro.MicroNameApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Test class for the SseResource REST controller.
 *
 * @see SseResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MicroNameApp.class)
public class SseResourceIntTest {

    private MockMvc restMockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        SseResource sseResource = new SseResource();
        restMockMvc = MockMvcBuilders
            .standaloneSetup(sseResource)
            .build();
    }

    /**
     * Test notification
     */
    @Test
    public void testNotification() throws Exception {
        restMockMvc.perform(get("/api/sse/notification"))
            .andExpect(status().isOk());
    }
}
