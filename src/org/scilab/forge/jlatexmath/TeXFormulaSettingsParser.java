/* TeXFormulaSettingsParser.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
 * Copyright (C) 2009 DENIZET Calixte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 */

package org.scilab.forge.jlatexmath;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Parses predefined TeXFormula's from an XML-file.
 */
public class TeXFormulaSettingsParser {
    
    public static final String RESOURCE_NAME = "TeXFormulaSettings.xml";
    public static final String CHARTODEL_MAPPING_EL = "Map";
    
    private Element root;
    
    public TeXFormulaSettingsParser() throws ResourceParseException {
	this(GlueSettingsParser.class.getResourceAsStream(RESOURCE_NAME), RESOURCE_NAME);
    }

    public TeXFormulaSettingsParser(InputStream file, String name) throws ResourceParseException {
	try {
   	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setIgnoringElementContentWhitespace(true);
	    factory.setIgnoringComments(true);
	    root = factory.newDocumentBuilder().parse(file).getDocumentElement();         
	} catch (Exception e) { // JDOMException or IOException
            throw new XMLResourceParseException(name, e);
        }
    }

    public void parseSymbolToFormulaMappings(String[] mappings) throws ResourceParseException {
        Element charToSymbol = (Element)root.getElementsByTagName("CharacterToFormulaMappings").item(0);
        if (charToSymbol != null) // element present
            addFormulaToMap(charToSymbol.getElementsByTagName("Map"), mappings);
    }

    public String[] parseSymbolToFormulaMappings() throws ResourceParseException {
        String[] mappings = new String[65536];
	parseSymbolToFormulaMappings(mappings);
        return mappings;
    }
    
    public void parseSymbolMappings(String[] mappings) throws ResourceParseException {
        Element charToSymbol = (Element)root.getElementsByTagName("CharacterToSymbolMappings").item(0);
        if (charToSymbol != null) // element present
            addToMap(charToSymbol.getElementsByTagName("Map"), mappings);
    }
    
    public String[] parseSymbolMappings() throws ResourceParseException {
        String[] mappings = new String[65536];
	parseSymbolMappings(mappings);
        return mappings;
    }

    public String[] parseDelimiterMappings() throws ResourceParseException {
        String[] mappings = new String[FontInfo.NUMBER_OF_CHAR_CODES];
        Element charToDelimiter = (Element)root.getElementsByTagName("CharacterToDelimiterMappings").item(0);
        if (charToDelimiter != null) // element present
            addToMap(charToDelimiter.getElementsByTagName(CHARTODEL_MAPPING_EL),
                    mappings);
        return mappings;
    }
    
    private static void addToMap(NodeList mapList, String[] table) throws ResourceParseException {
        for (int i = 0; i < mapList.getLength(); i++) {
            Element map = (Element)mapList.item(i);
            String ch = map.getAttribute("char");
            String symbol = map.getAttribute("symbol");
            // both attributes are required!
            if (ch.equals(""))
                throw new XMLResourceParseException(RESOURCE_NAME, map.getTagName(),
                        "char", null);
            else if (symbol.equals(""))
                throw new XMLResourceParseException(RESOURCE_NAME, map.getTagName(),
                        "symbol", null);
            if (ch.length() == 1) // valid element found
                table[ch.charAt(0)] =  symbol;
            else
                // only single-character mappings allowed, ignore others
                throw new XMLResourceParseException(RESOURCE_NAME, map.getTagName(),
                        "char",
                        "must have a value that contains exactly 1 character!");
        }
    }

    private static void addFormulaToMap(NodeList mapList, String[] table) throws ResourceParseException {
        for (int i = 0; i < mapList.getLength(); i++) {
            Element map = (Element)mapList.item(i);
            String ch = map.getAttribute("char");
            String formula = map.getAttribute("formula");
            // both attributes are required!
            if (ch.equals(""))
                throw new XMLResourceParseException(RESOURCE_NAME, map.getTagName(),
                        "char", null);
            else if (formula.equals(""))
                throw new XMLResourceParseException(RESOURCE_NAME, map.getTagName(),
                        "formula", null);
            if (ch.length() == 1) {// valid element found
		table[ch.charAt(0)] = formula;
	    }
            else
                // only single-character mappings allowed, ignore others
                throw new XMLResourceParseException(RESOURCE_NAME, map.getTagName(),
                        "char",
                        "must have a value that contains exactly 1 character!");
        }
    }
}
