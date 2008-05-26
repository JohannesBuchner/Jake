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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * TextTranslationProvider.
 */
public class TextTranslationProvider {
	private static HashMap<String, String> strings;
	
	protected TextTranslationProvider() {
	}
	/**
	 * Loads "lang-"+lang+".txt" in the langFilesPath
	 * and interpretes each line as identifier=message pair
	 * @param lang
	 * @throws IOException
	 */
	public static void setLanguage(String langFile) throws IOException {
		System.out.println("Language was set to " + langFile);
		strings = new HashMap<String, String>();
		BufferedReader fr = new BufferedReader(new FileReader(langFile));
		String line = "";
		while(true){
			line = fr.readLine();
			if(line == null)
				break;
			if(line.endsWith("\r"))
				line.substring(0, line.length()-1);
			if(line.endsWith("\n"))
				line.substring(0, line.length()-1);
			
			if(line.contains("=")){
				String[] parts = line.split("=",2);
				strings.put(parts[0], parts[1]);
			}
		}
		fr.close();
	}
	
	public static String get(String identifier, String... placeholderValues) 
			throws IllegalNumberOfArgumentsException, UnknownIdentifierException
	{
		String result = strings.get(identifier);
		if(result == null)
			throw new UnknownIdentifierException(identifier);
		
		for(int i=0;i<placeholderValues.length;i++)
			result = result.replaceAll("%"+i+"%", placeholderValues[i]);
		
		if(result.matches(".*%[0-9]{1,}%.*"))
			throw new IllegalNumberOfArgumentsException(
					"The identifier '"+identifier+"' needs more arguments: " + 
					strings.get(identifier));
		
		return result;
	}
}
