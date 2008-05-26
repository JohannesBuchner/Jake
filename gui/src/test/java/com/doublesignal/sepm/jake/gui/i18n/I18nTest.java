package com.doublesignal.sepm.jake.gui.i18n;

import java.io.FileInputStream;
import java.io.InputStream;

import com.doublesignal.sepm.jake.gui.i18n.exceptions.IllegalNumberOfArgumentsException;
import com.doublesignal.sepm.jake.gui.i18n.exceptions.UnknownIdentifierException;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import junit.framework.TestCase;

public class I18nTest extends TestCase {
	public void testi18n() throws Exception{
		/*BeanFactory factory = new XmlBeanFactory(new FileSystemResource("beans.xml"));
        ITranslationProvider translator = (ITranslationProvider) factory.getBean("translationProvider");
        */
        TextTranslationProvider.setLanguage("src/main/resources/lang-test.txt");
		assertEquals(TextTranslationProvider.get("HELLO_WORLD"),"Hallo Welt!");
		assertEquals(TextTranslationProvider.get("HELLO", "Chris"),
				"Hallo Chris!");
		assertEquals(TextTranslationProvider.get("HELLO", "Franz"),
				"Hallo Franz!");
		assertEquals(TextTranslationProvider.get("NETWORK_TIMEOUT_ON_USER", 
				"chris@doublesignal.com", Integer.toString(100)),
			"Die Netzwerkverbindung zu chris@doublesignal.com brach nach 100 Sekunden ab.");
		
		try{
			TextTranslationProvider.get("NETWORK_TIMEOUT_ON_USER");
			fail("IllegalNumberOfArgumentsException");
		}catch (IllegalNumberOfArgumentsException e) {
		}
		try{
			TextTranslationProvider.get("NETWORK_TIMEOUT_ON_USER", "chris@doublesignal.com");
			fail("IllegalNumberOfArgumentsException");
		}catch (IllegalNumberOfArgumentsException e) {
		}
		try{
			TextTranslationProvider.get("HELLO");
			fail("IllegalNumberOfArgumentsException");
		}catch (IllegalNumberOfArgumentsException e) {
		}
		
		try{
			TextTranslationProvider.get("FOO_BAR_DO_NOT_KNOW");
			fail("UnknownIdentifierException");
		}catch(UnknownIdentifierException e) {
		}
	}
	
}
