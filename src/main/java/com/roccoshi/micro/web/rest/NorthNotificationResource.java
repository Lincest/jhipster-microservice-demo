package com.roccoshi.micro.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.roccoshi.micro.domain.NorthNotification;
import com.roccoshi.micro.repository.NorthNotificationRepository;
import com.roccoshi.micro.service.NorthNotificationService;
import com.roccoshi.micro.web.rest.errors.BadRequestAlertException;
import com.roccoshi.micro.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing NorthNotification.
 */
@RestController
@RequestMapping("/restconf")
public class NorthNotificationResource {

    private final Logger log = LoggerFactory.getLogger(NorthNotificationResource.class);

    private static final String ENTITY_NAME = "microNameNorthNotification";

    private final NorthNotificationService northNotificationService;
    private final NorthNotificationRepository northNotificationRepository;

    public NorthNotificationResource(NorthNotificationService northNotificationService, NorthNotificationRepository northNotificationRepository) {
        this.northNotificationService = northNotificationService;
        this.northNotificationRepository = northNotificationRepository;
    }

    /**
     * 建立新订阅
     * {
     * "establish-subscription": {
     * "input": {
     * "encoding": "encode-json", // 默认
     * "subscription": {
     * "topic": "service", // service, alarm, resources可选
     * "object-type": "", // 非必须，对象类型
     * }
     * }
     * }
     * }
     * <p>
     * 返回值
     * {
     * "output": {
     * "identifier": "" // uuid: 用户标识
     * }
     * }
     *
     * @param body (body above)
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/operations/notification-action:establish-subscription")
    public ResponseEntity<JSONObject> createNorthNotification(@RequestBody JSONObject body) throws URISyntaxException {
        log.debug("REST request to save NorthNotification : {}", body);
        if (body == null) {
            throw new BadRequestAlertException("A new northNotification cannot be null", ENTITY_NAME, "null");
        }
        JSONObject input = body.getJSONObject("establish-subscription").getJSONObject("input");
        String encoding = null;
        if (input.containsKey("encoding")) {
            encoding = input.getString("encoding");
        }
        JSONObject subscription = input.getJSONObject("subscription");
        String topic = subscription.getString("topic");
        String objectType = null;
        if (subscription.containsKey("object-type")) {
            objectType = subscription.getString("object-type");
        }
        NorthNotification northNotification = new NorthNotification();
        String identifier = UUID.randomUUID().toString();
        northNotification.setIdentifier(identifier);
        northNotification.setEncoding(encoding);
        if (encoding == null) {
            northNotification.setEncoding("encode-json");
        }
        northNotification.setTopic(topic);
        northNotification.setObjectType(objectType);
        northNotificationRepository.save(northNotification);
        JSONObject response = new JSONObject();
        JSONObject output = new JSONObject();
        output.put("identifier", identifier);
        response.put("output", output);
        return ResponseEntity.created(new URI("/restconf/operations/notification-action:establish-subscription"))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, northNotification.getId().toString()))
            .body(response);
    }


    /**
     * GET  /restconf/streams/identifier/{identifier}
     *
     * @return whether establish notification is successful
     */
    @GetMapping("/streams/identifier/{identifier}")
    public SseEmitter establishNotification(@PathVariable String identifier) {
        log.debug("REST request to establish a notification for identifier : {}", identifier);
        SseEmitter emitter = northNotificationService.handleSubscribe(identifier);
        northNotificationService.testGenerateRandomInfo();
        return emitter;
    }

    /**
     * 去订阅
     * {
     * 	"delete-subscription": {
     * 		"input": {
     * 			"identifier": "" // 前面返回的uuid
     *                }
     *     }
     * }
     * 返回值
     *
     * @param body (body above)
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/operations/notification-action:delete-subscription")
    public ResponseEntity<Void> unsubscribeNorthNotification(@RequestBody JSONObject body) throws URISyntaxException {
        log.debug("REST request to delete NorthNotification : {}", body);
        if (body == null) {
            throw new BadRequestAlertException("A new northNotification cannot be null", ENTITY_NAME, "null");
        }
        String identifier = body.getJSONObject("delete-subscription").getJSONObject("input").getString("identifier");
        northNotificationService.handleUnsubscribe(identifier);
        return ResponseEntity.ok().build();
    }

}
