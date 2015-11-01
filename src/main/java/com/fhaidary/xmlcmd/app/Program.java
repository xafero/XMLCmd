package com.fhaidary.xmlcmd.app;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

public class Program {

	public static void main(String[] args) throws Exception {
		String dir = "/Users/farzaneh/Documents/test";
		String file = dir + '/' + "sometest.xml";
		String path = "/test";

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expression = xpath.compile(path);
		String result = expression.evaluate(new InputSource(file));

		System.out.println("=> " + result);
	}
}