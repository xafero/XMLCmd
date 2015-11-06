package com.fhaidary.xmlcmd.core;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

import com.fhaidary.xmlcmd.util.EasyNamespaceContext;

public final class XPathTool {

	public static void evaluate(String file, String path) throws XPathExpressionException {
		// Set up input
		InputSource source = new InputSource(file);
		// Create new factory for XPath
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		// Find default name space
		EasyNamespaceContext ctx = new EasyNamespaceContext();
		ctx.setDefaultNamespace(xpath.evaluate("namespace-uri(/*)", source));
		xpath.setNamespaceContext(ctx);
		// Evaluate XPath expression
		XPathExpression expr = xpath.compile(path);
		String result = expr.evaluate(source);
		// Output it!
		System.out.println(result.trim());
	}
}