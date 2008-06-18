package com.doublesignal.sepm.jake.gui.i18n;

import com.doublesignal.sepm.jake.gui.i18n.exceptions.IllegalNumberOfArgumentsException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * TextTranslationProvider.
 */
public class TextTranslationProvider implements ITranslationProvider {
	private static HashMap<String, String> strings;

	private Resource file;

	/**
	 * Loads the languageFile and interpretes each line as identifier=message pair
	 * @param lang
	 */
	public void setLanguage(String langFile) throws IOException{
		strings = new HashMap<String, String>();
		file = new ClassPathResource(langFile);

		BufferedReader fr = new BufferedReader(new InputStreamReader(file.getInputStream()));

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
	
	public String get(String identifier, String... placeholderValues) 
			throws IllegalNumberOfArgumentsException
	{
		String result = strings.get(identifier);
		if(result == null)
			return identifier;
		
		for(int i=0;i<placeholderValues.length;i++){
			if(placeholderValues[i] == null)
				placeholderValues[i] = "(null)";
			result = result.replaceAll("%"+i+"%", placeholderValues[i]);
		}
		if(result.matches(".*%[0-9]{1,}%.*"))
			throw new IllegalNumberOfArgumentsException(
					"The identifier '"+identifier+"' needs more arguments: " + 
					strings.get(identifier));
		
		return result;
	}
}
