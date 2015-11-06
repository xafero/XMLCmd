package com.fhaidary.xmlcmd.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.google.gson.stream.JsonWriter;

public final class JsonTool {

	public static void convert2Json(String file) throws IOException, XMLStreamException {
		// Set it up...
		StringWriter out = new StringWriter();
		JsonWriter writer = new JsonWriter(out);
		writer.setIndent("  ");
		// Work...
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
		XMLStreamReader xml = factory.createXMLStreamReader(new FileInputStream(file), "UTF8");
		convert2Json(xml, writer);
		// Finish...
		xml.close();
		writer.close();
		// Output it!
		System.out.println(out.toString().trim());
	}

	private static void convert2Json(XMLStreamReader xml, JsonWriter json) throws XMLStreamException, IOException {
		for (int event = xml.getEventType();; event = xml.next()) {
			String localName;
			switch (event) {
			case XMLStreamReader.START_DOCUMENT:
				json.beginObject();
				break;
			case XMLStreamReader.START_ELEMENT:
				localName = xml.getLocalName();
				json.name(localName);
				json.beginObject();
				break;
			case XMLStreamReader.CHARACTERS:
				String txt = xml.getText().trim();
				if (!txt.isEmpty()) {
					json.name("text");
					json.value(txt);
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				localName = xml.getLocalName();
				json.endObject();
				break;
			case XMLStreamReader.END_DOCUMENT:
				json.endObject();
				return;
			default:
				throw new UnsupportedOperationException(findConstant(event) + "!");
			}
		}
	}

	private static String findConstant(int code) {
		try {
			for (Field field : XMLStreamConstants.class.getFields()) {
				int value = (Integer) field.get(null);
				if (code == value)
					return field.getName();
			}
			return null;
		} catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}
}