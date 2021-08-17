package kr.co.test.property;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 17. 김대광	최초작성
 * </pre>
 * 
 *
 * @author 김대광
 */

@SpringBootTest
@Profile("local")
public class PropertyTest {

	@Autowired @Qualifier("application")
	private PropertiesFactoryBean application;
	
	@Test
	public void 프로퍼티_테스트() throws IOException {
		Properties prop = application.getObject();
		
		System.out.println(prop);
	}
	
}
