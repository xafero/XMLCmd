package com.fhaidary.xmlcmd.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.Event.ID;
import org.yaml.snakeyaml.events.ScalarEvent;

import javanet.staxutils.IndentingXMLStreamWriter;

public class YamlTool {

	public static void convert2Xml(String file) throws IOException, XMLStreamException {
		// Set it up...
		StringWriter out = new StringWriter();
		XMLOutputFactory factory = XMLOutputFactory.newFactory();
		XMLStreamWriter writer = factory.createXMLStreamWriter(out);
		writer = new IndentingXMLStreamWriter(writer);
		// Work...
		InputStreamReader in = new InputStreamReader(new FileInputStream(file), "UTF8");
		Yaml yaml = new Yaml();
		convert2Xml(yaml, in, writer);
		// Finish...
		writer.close();
		// Output it!
		System.out.println(out.toString().trim());
	}

	private static void convert2Xml(Yaml yaml, Reader in, XMLStreamWriter xml) throws XMLStreamException {
		boolean inSequence = false;
		String itemName = null;
		Event reset = null;
		Event last = null;
		for (Event event : yaml.parse(in)) {
			if (event.is(ID.StreamStart) || event.is(ID.StreamEnd))
				continue;
			if (event.is(ID.DocumentStart)) {
				xml.writeStartDocument("UTF8", "1.0");
				last = event;
				reset = event;
				continue;
			}
			if (event.is(ID.DocumentEnd)) {
				xml.writeEndDocument();
				last = event;
				continue;
			}
			if (last.is(ID.Scalar) && event.is(ID.Scalar)) {
				String key = ((ScalarEvent) last).getValue();
				String val = ((ScalarEvent) event).getValue();
				xml.writeAttribute(key, val);
				last = reset;
				continue;
			}
			if (last.is(ID.Scalar) && event.is(ID.MappingStart)) {
				String txt = ((ScalarEvent) last).getValue();
				xml.writeStartElement(txt);
			} else if (inSequence && event.is(ID.MappingStart))
				xml.writeStartElement(itemName);
			else if (last.is(ID.Scalar) && event.is(ID.SequenceStart)) {
				String txt = ((ScalarEvent) last).getValue();
				if (txt.endsWith("s"))
					itemName = txt.substring(0, txt.length() - 1);
				else if (txt.endsWith("ren"))
					itemName = txt.substring(0, txt.length() - 3);
				else
					itemName = "item";
				xml.writeStartElement(txt);
				inSequence = true;
			} else if (event.is(ID.MappingEnd))
				try {
					xml.writeEndElement();
				} catch (Exception e) {
					// NO-OP!
				}
			else if (event.is(ID.SequenceEnd)) {
				xml.writeEndElement();
				inSequence = false;
			}
			last = event;
		}
	}
}