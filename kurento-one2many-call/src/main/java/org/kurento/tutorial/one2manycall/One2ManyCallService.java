package org.kurento.tutorial.one2manycall;

import com.google.gson.JsonObject;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class One2ManyCallService {
    private static final Logger log = LoggerFactory.getLogger(CallHandler.class);

    @Autowired
    private KurentoClient kurento;

    public static final Map<String, One2ManyCall> ONE_2_MANY_CALL_MAP = new ConcurrentHashMap<>();
    public static final Map<WebSocketSession, One2ManyCall> sessionOne2ManyCallMap = new ConcurrentHashMap<>();

    public One2ManyCall getOne2ManyCall(String wName) {
        return ONE_2_MANY_CALL_MAP.get(wName);
    }

    public One2ManyCall creatOne2ManyCall(String wName, final WebSocketSession session, JsonObject jsonMessage) throws IOException {
        UserSession presenterUserSession = new UserSession(session);
        MediaPipeline pipeline = kurento.createMediaPipeline();
        presenterUserSession.setWebRtcEndpoint(new WebRtcEndpoint.Builder(pipeline).build());

        WebRtcEndpoint presenterWebRtc = presenterUserSession.getWebRtcEndpoint();

        presenterWebRtc.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {

            @Override
            public void onEvent(IceCandidateFoundEvent event) {
                JsonObject response = new JsonObject();
                response.addProperty("id", "iceCandidate");
                response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
                try {
                    synchronized (session) {
                        session.sendMessage(new TextMessage(response.toString()));
                    }
                } catch (IOException e) {
                    log.debug(e.getMessage());
                }
            }
        });

        String sdpOffer = jsonMessage.getAsJsonPrimitive("sdpOffer").getAsString();
        String sdpAnswer = presenterWebRtc.processOffer(sdpOffer);

        JsonObject response = new JsonObject();
        response.addProperty("id", "presenterResponse");
        response.addProperty("response", "accepted");
        response.addProperty("sdpAnswer", sdpAnswer);

        synchronized (session) {
            presenterUserSession.sendMessage(response);
        }
        presenterWebRtc.gatherCandidates();

        One2ManyCall one2ManyCall = new One2ManyCall(wName, pipeline, presenterUserSession);
        mapOne2ManyCall(wName, one2ManyCall);
        return ONE_2_MANY_CALL_MAP.get(wName);
    }

    public void mapOne2ManyCall(String wName, One2ManyCall one2ManyCall) {
        ONE_2_MANY_CALL_MAP.put(wName, one2ManyCall);
    }

    public void addViewer(One2ManyCall one2ManyCall, final WebSocketSession session, JsonObject jsonMessage) throws IOException {
        UserSession viewer = new UserSession(session);
        one2ManyCall.getViewers().put(session.getId(), viewer);
        sessionOne2ManyCallMap.put(session,one2ManyCall);

        WebRtcEndpoint nextWebRtc = new WebRtcEndpoint.Builder(one2ManyCall.getPipeline()).build();

        nextWebRtc.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {

            @Override
            public void onEvent(IceCandidateFoundEvent event) {
                JsonObject response = new JsonObject();
                response.addProperty("id", "iceCandidate");
                response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
                try {
                    synchronized (session) {
                        session.sendMessage(new TextMessage(response.toString()));
                    }
                } catch (IOException e) {
                    log.debug(e.getMessage());
                }
            }
        });

        viewer.setWebRtcEndpoint(nextWebRtc);
        one2ManyCall.getPresenterUserSession().getWebRtcEndpoint().connect(nextWebRtc);
        String sdpOffer = jsonMessage.getAsJsonPrimitive("sdpOffer").getAsString();
        String sdpAnswer = nextWebRtc.processOffer(sdpOffer);

        JsonObject response = new JsonObject();
        response.addProperty("id", "viewerResponse");
        response.addProperty("response", "accepted");
        response.addProperty("sdpAnswer", sdpAnswer);

        synchronized (session) {
            viewer.sendMessage(response);
        }
        nextWebRtc.gatherCandidates();
    }

    public One2ManyCall getOne2ManyCallBySession (WebSocketSession session) {
        return sessionOne2ManyCallMap.get(session);
    }
}
