package com.doublesignal.sepm.jake.gui.i18n;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/* This is more of a Singleton now */
public class TranslatorFactory {
	static ITranslationProvider provider = null;
	public static ITranslationProvider getTranslator(){
		if(provider == null){
			BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
			provider = (ITranslationProvider) factory.getBean("translationProvider");
		}
		return provider; 
	}
}
