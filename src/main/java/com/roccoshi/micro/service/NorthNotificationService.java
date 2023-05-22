package com.roccoshi.micro.service;

import com.alibaba.fastjson.JSONObject;
import com.roccoshi.micro.domain.NorthNotification;
import com.roccoshi.micro.domain.NorthNotificationEvents;
import com.roccoshi.micro.repository.NorthNotificationEventsRepository;
import com.roccoshi.micro.repository.NorthNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.management.Notification;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class NorthNotificationService {

    private final Logger log = LoggerFactory.getLogger(NorthNotificationService.class);

    private final NorthNotificationRepository northNotificationRepository;

    private final NorthNotificationEventsRepository northNotificationEventsRepository;

    // 当前正在订阅的消息
    private static final Map<NorthNotification, SseEmitter> sseCache = new ConcurrentHashMap<>(); // should be identifier(NorthNotification) => sseEmitter

    public NorthNotificationService(NorthNotificationRepository northNotificationRepository, NorthNotificationEventsRepository northNotificationEventsRepository) {
        this.northNotificationRepository = northNotificationRepository;
        this.northNotificationEventsRepository = northNotificationEventsRepository;
    }

    // 处理订阅
    public SseEmitter handleSubscribe(String identifier) {
        NorthNotification notification = northNotificationRepository.findByIdentifier(identifier).get();
        SseEmitter sseEmitter = new SseEmitter();
        sseCache.put(notification, sseEmitter);
        sseEmitter.onTimeout(() -> sseCache.remove(notification));
        sseEmitter.onCompletion(() -> System.out.println("complete"));
        return sseEmitter;
    }

    // 取消订阅
    public void handleUnsubscribe(String identifier) {
        NorthNotification notification = northNotificationRepository.findByIdentifier(identifier).get();
        if (sseCache.containsKey(notification)) {
            sseCache.get(notification).complete();
            sseCache.remove(notification);
        }
        northNotificationRepository.delete(notification); // 需要考虑级联的问题
    }

    // 推送消息

    /**
     * @param operation    事件类型，add / delete / update
     * @param operState    "up" / "down"
     * @param currentState 非必须, "normal" / "pathchange" / "ratechange_successful" / "ratechange_fail" / "connect_fail"
     * @param topic        所属主题 (service, alarm, resources)
     * @param objectType   非必须，对象类型
     * @throws IOException
     */
    public void pushNotification(String operation, String operState, String currentState, String topic, String objectType) throws IOException {
        // 遍历sseCache, 向所有符合条件( (northNotification.topic = topic,northNotification.objectType = objectType) 或 objectType === null)的订阅者推送消息, 并将消息保存到数据库
        for (Map.Entry<NorthNotification, SseEmitter> entry : sseCache.entrySet()) {
            NorthNotification northNotification = entry.getKey();
            SseEmitter sseEmitter = entry.getValue();
            if (northNotification.getTopic().equals(topic)) {
                if (objectType != null && !northNotification.getObjectType().equals(objectType)) {
                    continue;
                }
                NorthNotificationEvents event = new NorthNotificationEvents();
                // ISO8601格式, 当前时间
                LocalDateTime now = LocalDateTime.now();
                event.setEventTime(now.format(DateTimeFormatter.ISO_DATE_TIME));
                event.setPatchId(UUID.randomUUID().toString());
                event.setTargetId(UUID.randomUUID().toString());
                event.setOperState(operState);
                event.setCurrentState(currentState);
                event.setOperation(operation);
                event.setNotification(northNotification);
                northNotificationEventsRepository.save(event);
                // 构造 DTO 并发送
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("eventTime", event.getEventTime());
                jsonObject.put("identifier", northNotification.getIdentifier());
                jsonObject.put("patch-id", event.getPatchId());
                jsonObject.put("topic", northNotification.getTopic());
                jsonObject.put("object-type", northNotification.getObjectType());
                jsonObject.put("operation", event.getOperation());
                JSONObject value = new JSONObject();
                value.put("target-id", event.getTargetId());
                value.put("oper-state", event.getOperState());
                value.put("current-state", event.getCurrentState());
                jsonObject.put("value", value);
                sseEmitter.send(jsonObject.toString());
                log.debug("To identifier {} Pushed notification: {}", northNotification.getIdentifier(), jsonObject.toString());
            }
        }
    }

    // FIXME: only for test
    public void testGenerateRandomInfo() {
        // 每隔10秒钟随机上传一条消息
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3_000L);
                    String[] operations = {"add", "delete", "update"};
                    String[] operStates = {"up", "down"};
                    String[] currentStates = {"normal", "pathchange", "ratechange_successful", "ratechange_fail", "connect_fail"};
                    String[] topics = {"service", "alarm"};
                    String operation = operations[(int) (Math.random() * 3)];
                    String operState = operStates[(int) (Math.random() * 2)];
                    String currentState = currentStates[(int) (Math.random() * 5)];
                    String topic = topics[(int) (Math.random() * 2)];
                    pushNotification(operation, operState, currentState, topic, null);
                } catch (Exception e) {
                    break;
                }
            }
        }).start();
    }
}
