package kr.co.test.marshaller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 7. 30. 김대광	최초작성
 * </pre>
 *
 * XML은 사장된거나 마찬가지라 전혀 의미 없을 듯....
 *
 * <pre>
 * @see <a href="https://github.com/kdk1026/SpringBootPractice/blob/master/SpringBootPractice/src/main/java/kr/co/test/config/spring/MvcConfig.java">설정 참고</a>
 * extends WebMvcConfigurerAdapter 시절 까지만...
 * 	jaxbMarshaller(), jaxbView()
 * implements WebMvcConfigurer 에서의 설정은 모름
 * </pre>
 * @author 김대광
 */
public class Jaxb2MarshallerCustom {

	private static final Logger logger = LoggerFactory.getLogger(Jaxb2MarshallerCustom.class);

	private String jaxbBasePackage;

	public Jaxb2Marshaller jaxb2Marshaller(Class<?>... paramClasses) {
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setClassesToBeBound(paramClasses);
		return jaxb2Marshaller;
	}

	public Jaxb2Marshaller jaxb2Marshaller(String toScanPackage) {
		this.jaxbBasePackage = toScanPackage;

		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();

		jaxb2Marshaller.setClassesToBeBound(getXmlRootElementClasses());

		return jaxb2Marshaller;
	}

	private Class<?>[] getXmlRootElementClasses() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(XmlRootElement.class));
		scanner.addIncludeFilter(new AnnotationTypeFilter(XmlType.class));
		scanner.addIncludeFilter(new AnnotationTypeFilter(XmlSeeAlso.class));
		scanner.addIncludeFilter(new AnnotationTypeFilter(XmlEnum.class));
		scanner.addIncludeFilter(new AnnotationTypeFilter(XmlRegistry.class));

		List<Class<?>> classes = new ArrayList<>();
		String basePackage = this.jaxbBasePackage;

		Set<BeanDefinition> definitions = scanner.findCandidateComponents(basePackage);
		String className = "";
		for (BeanDefinition definition : definitions) {
			className = definition.getBeanClassName();

			try {
				classes.add(Class.forName(className));
			} catch (ClassNotFoundException e) {
				logger.error("", e);
			}
		}

		return classes.toArray(new Class[0]);
	}

}
