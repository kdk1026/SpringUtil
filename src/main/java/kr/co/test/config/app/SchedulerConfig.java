package kr.co.test.config.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 10. 16. 김대광	최초작성
 * </pre>
 *
 * <pre>
 * 기본적으로 단일 쓰레드
 * - ThreadPoolTaskScheduler를 설정하면 여러 스케줄 작업을 동시에 처리 가능
 * - 웹 요청과 충돌하지 않도록 분리해서 처리 가능
 * </pre>
 *
 * @author 김대광
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean
    TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); 	// 병렬로 처리할 수 있는 쓰레드 수
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.initialize();
        return scheduler;
    }

}
