package kr.co.test.jasypt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 5. 24. kdk	최초작성
 * </pre>
 * Junit 이 맛이 가서...버전 충돌?
 *
 * @author kdk
 */
class JasyptTest {

	@Test
	void test() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("jasypt.encryptor.password");
		encryptor.setAlgorithm("PBEWithMD5AndDES");

        String encryptedPassword = encryptor.encrypt("iccxhvxyoarneaks");
        System.out.println(encryptedPassword);

        assertEquals("iccxhvxyoarneaks", encryptor.decrypt(encryptedPassword));
	}

}
