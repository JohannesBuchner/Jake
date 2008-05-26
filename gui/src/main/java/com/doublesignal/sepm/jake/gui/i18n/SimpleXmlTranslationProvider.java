package com.doublesignal.sepm.jake.gui.i18n;

import com.doublesignal.sepm.jake.gui.i18n.exceptions.IllegalNumberOfArgumentsException;
import com.doublesignal.sepm.jake.gui.i18n.exceptions.UnknownIdentifierException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;

/**
 * Simple XML implementation of TranslationProvider.
 */
public class SimpleXmlTranslationProvider implements ITranslationProvider {
	private String language;
	private Document document;
	private XPath xpath;

	public void setLanguageSourceFile(String filename) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(filename);
			XPathFactory xpfactory = XPathFactory.newInstance();
			xpath = xpfactory.newXPath();
		} catch (ParserConfigurationException e) {
			this.document = null;
		} catch (IOException e) {
			this.document = null;
		} catch (SAXException e) {
			this.document = null;
		}
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String get(String messageIdentifier, String... placeholderValues) throws IllegalNumberOfArgumentsException, UnknownIdentifierException {
		if(this.document == null) {
			return messageIdentifier;
		}
		try {
			XPathExpression expr = xpath.compile("/languages/language[@id=\""+ this.language +"\"]/message[@id=\""+ this.language +"\"]");
			Object results = expr.evaluate(document, XPathConstants.NODESET);
			NodeList nodes = (NodeList) results;
			if(nodes.getLength() == 0) {
				return messageIdentifier;
			}
			String langText = nodes.item(0).getTextContent();

			// TODO: Text replacement

			return langText;
		} catch (XPathExpressionException e) {
			return messageIdentifier;
		}

	}

}
