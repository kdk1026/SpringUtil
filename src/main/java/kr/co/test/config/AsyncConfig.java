package kr.co.test.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 7. 31. 김대광	최초작성
 * </pre>
 * 
 * <pre>
 * 메일에 사용할려고 했으나 type을 주는 바람에... 비동기 사용 메소드는 void 여야 한다...
 * 예전에는 메일에 활용 했는데... 메일 먼저 구현 후, 적용할려고 하다 보니 나원...
 * 아무렴 참고용인데, 샘플로 메소드 한개 추가함
 * </pre>
 * @author 김대광
 */
@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("kdk-async-");
        executor.initialize();
        return executor;
	}
	
}
