package kr.co.test.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 10. 26. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class SseEmitterUtil {

	private static final Logger logger = LoggerFactory.getLogger(SseEmitterUtil.class);

	private SseEmitterUtil() {

	}

	private static final Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

	public static SseEmitter subscribe(String uniqueId) {
		SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

		try {
			sseEmitter.send(SseEmitter.event().name("connect"));
		} catch (IOException e) {
			logger.error("", e);
		}

		sseEmitters.put(uniqueId, sseEmitter);

		sseEmitter.onCompletion(new Runnable() {

			@Override
			public void run() {
				sseEmitters.remove(uniqueId);
			}
		});

		sseEmitter.onTimeout(new Runnable() {

			@Override
			public void run() {
				sseEmitters.remove(uniqueId);
			}
		});

		sseEmitter.onError(new Consumer<Throwable>() {

			@Override
			public void accept(Throwable t) {
				sseEmitters.remove(uniqueId);
			}
		});

        return sseEmitter;
	}

	public static void sendEvent(String uniqueId, String eventName, String eventData) {
		SseEmitter sseEmitter = sseEmitters.get(uniqueId);

		if ( sseEmitter != null ) {
			try {
				byte[] utf8Bytes = eventData.getBytes(StandardCharsets.UTF_8);

				sseEmitter.send(SseEmitter.event().name(eventName).data(utf8Bytes, MediaType.APPLICATION_OCTET_STREAM));
			} catch (IOException e) {
				sseEmitters.remove(uniqueId);
				logger.error("", e);
			}
		}
	}

}
