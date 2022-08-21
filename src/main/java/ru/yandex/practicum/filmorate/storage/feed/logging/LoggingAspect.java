package ru.yandex.practicum.filmorate.storage.feed.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    private final Map<String, IEventFeedLog> iEventFeedLogMap;
    private final Map<String, IEventFeedAroundLog> iEventFeedAroundLogMap;

    @Autowired
    public LoggingAspect(Map<String, IEventFeedLog> iEventFeedLogMap, Map<String, IEventFeedAroundLog> iEventFeedAroundLogMap) {
        this.iEventFeedLogMap = iEventFeedLogMap;
        this.iEventFeedAroundLogMap = iEventFeedAroundLogMap;
    }


    @AfterReturning(pointcut = "@annotation(ru.yandex.practicum.filmorate.storage.feed.logging.LogEventFeed)", returning = "result")
    public void logMethodCall(JoinPoint jp, Object result) {
        String eventFeedLog = "EventFeed" + jp.getSignature().toShortString().split("DbStorage")[0];
        log.info("eventFeedLog - {}", eventFeedLog);
        iEventFeedLogMap.get(eventFeedLog).log(result, jp.getSignature().getName(), jp.getArgs());
    }

    @Around("@annotation(LogEventFeedAround)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String eventFeedLog = "EventFeed" + joinPoint.getSignature().toShortString().split("DbStorage")[0];
        Object result = iEventFeedAroundLogMap.get(eventFeedLog).getResult(joinPoint.getArgs());
        Object proceed = joinPoint.proceed();
        iEventFeedLogMap.get(eventFeedLog).log(null, joinPoint.getSignature().getName(), joinPoint.getArgs(), result);
        return proceed;
    }
}
