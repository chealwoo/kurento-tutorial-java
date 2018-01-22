package org.kurento.tutorial.one2manycall;

import org.kurento.client.MediaPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class One2ManyCall {
    private static final Logger log = LoggerFactory.getLogger(CallHandler.class);

    private final ConcurrentHashMap<String, UserSession> viewers = new ConcurrentHashMap<>();
    private final MediaPipeline pipeline;
    private final UserSession presenterUserSession;
    private final String wName;

    public One2ManyCall(String wName, MediaPipeline pipeline, UserSession presenterUserSession) {
        this.wName = wName;
        this.presenterUserSession = presenterUserSession;
        this.pipeline = pipeline;
    }

    public String getwName() {
        return wName;
    }

    public MediaPipeline getPipeline() {
        return pipeline;
    }

    public UserSession getPresenterUserSession() {
        return presenterUserSession;
    }

    public ConcurrentHashMap<String, UserSession> getViewers() {
        return viewers;
    }

    public UserSession getViewer(String id) {
        return viewers.get(id);
    }

    public UserSession addViewer(String id, UserSession session) {
        return viewers.put(id, session);
    }

}
