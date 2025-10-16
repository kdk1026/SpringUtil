package kr.co.test.config.app;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 10. 16. 김대광	최초작성
 * </pre>
 *
 * <pre>
 * 비동기 작업 수행 메소드
 *  - 타입은 void 여야 함
 *  - @Async 어노테이션 필요
 * </pre>
 *
 * @author 김대광
 */
@Configuration
@EnableAsync
public class AsyncConfig {

	@Bean
    Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);		// 핵심 스레드 수
        executor.setMaxPoolSize(10);		// 최대 스레드 수
        executor.setQueueCapacity(100);		// 대기 큐 용량
        executor.setKeepAliveSeconds(60);	// 유휴 스레드가 60초 동안 유지됨

        /*
         * AbortPolicy()			: Exception Throw (Default)
         * CallerRunsPolicy()		: 해당 Application이 과부하 상태일 경우 TaskExecutor에 의해서가 아닌 Thread에서 직접 Task를 실행시킬 수 있게 한다
         * DiscardPolicy()			: 해당 Application이 과부하 상태일 경우 현재 Task의 실행을 Skip
         * DiscardOldestPolicy()	: 해당 Application이 과부하 상태일 경우 Queue의 Head에 있는 Task의 실행을 Skip
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("async-task-");
        executor.initialize();
        return executor;
	}

}
