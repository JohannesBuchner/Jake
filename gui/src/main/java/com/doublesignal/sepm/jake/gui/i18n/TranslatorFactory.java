package com.doublesignal.sepm.jake.gui.i18n;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class TranslatorFactory {
	public static ITranslationProvider getTranslator(){
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
		return (ITranslationProvider) factory.getBean("translationProvider");
	}
}
