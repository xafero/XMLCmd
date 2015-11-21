package com.fhaidary.xmlcmd.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.FileUtils;

public class ExtractTool {

	private static String buildXPath(Deque<String> names) {
		StringBuilder bld = new StringBuilder();
		for (String name : names)
			bld.insert(0, '/' + name);
		return bld.toString();
	}

	private static Collection<String> extractXPaths(File file) throws IOException, XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newFactory();
		XMLEventReader xml = factory.createXMLEventReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		return extractXPaths(xml);
	}

	private static Collection<String> extractXPaths(XMLEventReader xml) throws XMLStreamException {
		Set<String> xpaths = new LinkedHashSet<String>();
		Deque<String> names = new ArrayDeque<String>();
		while (xml.hasNext()) {
			XMLEvent evt = xml.nextEvent();
			switch (evt.getEventType()) {
			case XMLEvent.START_DOCUMENT:
				break;
			case XMLEvent.START_ELEMENT:
				StartElement start = evt.asStartElement();
				int index = 1;
				String local = start.getName().getLocalPart();
				String parent = buildXPath(names);
				String xpath;
				while (true) {
					String newName = local + "[" + index + "]";
					xpath = parent + '/' + newName;
					if (xpaths.contains(xpath)) {
						index++;
					} else {
						names.push(newName);
						break;
					}
				}
				xpaths.add(xpath);
				@SuppressWarnings("unchecked")
				Iterator<Attribute> attrs = start.getAttributes();
				for (Attribute attr = null; attrs.hasNext();) {
					attr = attrs.next();
					String apath = xpath + "/@" + attr.getName().getLocalPart();
					xpaths.add(apath);
				}
				break;
			case XMLEvent.CHARACTERS:
				break;
			case XMLEvent.END_ELEMENT:
				// EndElement end = evt.asEndElement();
				names.poll();
				break;
			case XMLEvent.END_DOCUMENT:
				xml.close();
				break;
			default:
				throw new UnsupportedOperationException("Unknown? " + evt);
			}
		}
		return xpaths;
	}

	public static void extractXPaths(String path, File output) throws IOException, XMLStreamException {
		Set<String> xpaths = new TreeSet<String>();
		File handle = new File(path);
		if (handle.isFile())
			extractXPaths(handle, xpaths);
		else
			for (File file : handle.listFiles()) {
				if (!file.getName().toLowerCase().endsWith(".xml"))
					continue;
				extractXPaths(file, xpaths);
			}
		FileUtils.writeLines(output, xpaths);
	}

	private static void extractXPaths(File file, Set<String> xpaths) throws IOException, XMLStreamException {
		System.out.printf("Processing '%s'...", file);
		Collection<String> currents = extractXPaths(file);
		System.out.println(" => " + currents.size() + " expressions extracted!");
		xpaths.addAll(currents);
	}
}