package com.fhaidary.xmlcmd.app;

import javax.xml.xpath.*;
import org.xml.sax.*;

public class Program {

	public static void main(String[] args) throws Exception {
		String dir = "/Users/farzaneh/Documents/xmlCewe";
		String file = dir + '/' + "tkr_2015-04-14T16_00_39_SN_811311_LOC_52082201_v4.9.5_success.xml.xml";
		String path = "/digifoto/tracker/terminal/ticket/user";

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expression = xpath.compile(path);
		String result = expression.evaluate(new InputSource(file));

		System.out.println("=> " + result);
	}
}