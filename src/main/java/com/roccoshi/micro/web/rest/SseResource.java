package com.roccoshi.micro.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.xml.transform.Source;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SseResource controller
 */
@RestController
@RequestMapping("/api/sse")
public class SseResource {

    private final Logger log = LoggerFactory.getLogger(SseResource.class);
    // save all sse connections
    private static final Map<String, SseEmitter> sseCache = new ConcurrentHashMap<>();

    /**
     * subscribe a sse
     */
    @GetMapping("/notification/subscribe")
    private SseEmitter push(@RequestParam String id) {
        // set time out
        SseEmitter sseEmitter = new SseEmitter(3600_000L); // 1h timeout
        sseCache.put(id, sseEmitter);
        sseEmitter.onTimeout(() -> sseCache.remove(id));
        sseEmitter.onCompletion(() -> System.out.println("complete"));
        try {
            test(id);
        } catch (Exception e) {
            System.out.println("==== un expected happen");
            over(id);
        }
        return sseEmitter;
    }

    // test for generate hello {i} within 10 seconds
    public void test(String id) throws IOException, InterruptedException {
        for (int i = 0; i < 10; ++i) {
            Thread.sleep(1000);
            String message = "hello" + i;
            push(id, message);
            log.info("now we send {}", message);
        }
        over(id);
    }

    /**
     * sse push
     * @param id sse id
     * @param content sse content
     */
    public void push(String id, String content) throws IOException {
        SseEmitter sseEmitter = sseCache.get(id);
        if (sseEmitter != null) {
            sseEmitter.send(content);
        }
    }

    /**
     * stop a sse
     * @param id sse id
     */
    public void over(String id) {
        SseEmitter sseEmitter = sseCache.get(id);
        if (sseEmitter != null) {
            sseEmitter.complete();
            sseCache.remove(id);
        }
    }

}
