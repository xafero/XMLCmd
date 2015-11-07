package com.fhaidary.xmlcmd.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

public final class JsonTool {

	private static final JsonParser parser = new JsonParser();

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
			String itemName;
			switch (event) {
			case XMLStreamReader.START_DOCUMENT:
				json.beginObject();
				break;
			case XMLStreamReader.START_ELEMENT:
				String fix = xml.getPrefix();
				String name = xml.getLocalName();
				itemName = fix == null || fix.isEmpty() ? name : fix + "_" + name;
				json.name(itemName);
				json.beginObject();
				for (int i = 0; i < xml.getAttributeCount(); i++) {
					String attrFix = xml.getAttributePrefix(i);
					String attrName = xml.getAttributeLocalName(i);
					String attrVal = xml.getAttributeValue(i);
					attrName = attrFix == null || attrFix.isEmpty() ? attrName : attrFix + "_" + attrName;
					json.name(attrName);
					json.value(attrVal);
				}
				for (int i = 0; i < xml.getNamespaceCount(); i++) {
					String nsName = xml.getNamespacePrefix(i);
					nsName = nsName == null ? "xmlns" : "xmlns_" + nsName;
					String nsVal = xml.getNamespaceURI(i);
					json.name(nsName);
					json.value(nsVal);
				}
				break;
			case XMLStreamReader.CHARACTERS:
				String txt = xml.getText().trim();
				if (!txt.isEmpty()) {
					try {
						JsonElement elem = parser.parse('{' + txt + '}');
						json.name("#sub");
						json.jsonValue(elem.toString());
					} catch (JsonSyntaxException jse) {
						json.name("#txt");
						json.value(txt);
					}
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				itemName = xml.getLocalName();
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