package com.fhaidary.xmlcmd.core;

import java.io.File;
import java.io.StringWriter;
import java.util.Iterator;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FilenameUtils;

public class DsvTool {

	public static void convert2Xml(String path) throws Exception {
		File file = new File(path);
		String root = FilenameUtils.getBaseName(path);
		String ext = FilenameUtils.getExtension(path);
		// Set it up...
		StringWriter out = new StringWriter();
		XMLOutputFactory factory = XMLOutputFactory.newFactory();
		XMLStreamWriter writer = factory.createXMLStreamWriter(out);
		writer = new IndentingXMLStreamWriter(writer);
		// Work...
		CSVFormat fmt = CSVFormat.DEFAULT.withHeader();
		if (ext.equalsIgnoreCase("TSV"))
			fmt = CSVFormat.TDF.withHeader();
		else if (ext.equalsIgnoreCase("SSV"))
			fmt = fmt.withDelimiter(';');
		CSVParser csv = CSVParser.parse(file, Charsets.UTF_8, fmt);
		convert2Xml(root, csv, writer);
		// Finish...
		csv.close();
		writer.close();
		// Output it!
		System.out.println(out.toString().trim());
	}

	private static void convert2Xml(String root, CSVParser csv,
			XMLStreamWriter xml) throws XMLStreamException {
		xml.writeStartDocument("UTF8", "1.0");
		xml.writeStartElement(root);
		Iterator<CSVRecord> it = csv.iterator();
		while (it.hasNext()) {
			CSVRecord row = it.next();
			xml.writeStartElement("entry");
			for (String key : csv.getHeaderMap().keySet()) {
				String column = key.trim();
				if (column.isEmpty())
					continue;
				String value = row.get(key);
				value = value.trim();
				if (value.isEmpty())
					continue;
				xml.writeStartElement(column);
				xml.writeCharacters(value);
				xml.writeEndElement();
			}
			xml.writeEndElement();
		}
		xml.writeEndElement();
		xml.writeEndDocument();
	}
}