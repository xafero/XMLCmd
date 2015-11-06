package com.fhaidary.xmlcmd.app;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.fhaidary.xmlcmd.core.JsonTool;
import com.fhaidary.xmlcmd.core.XPathTool;
import com.fhaidary.xmlcmd.core.XsltTool;

public class Program {

	private static final String APP_SHORT_NAME = "xmlcmd";
	private static final String APP_SRC_URL = "https://github.com/fhaidary/XMLCmd";

	private static final String S_INPUT = "i";
	private static final String S_XPATH = "p";
	private static final String S_XSLT = "s";
	private static final String S_2JSON = "j";

	public static void main(String[] args) throws Exception {
		// Define options
		Option help = new Option("?", "help", false, "print this message");
		Option file = Option.builder(S_INPUT).desc("specify input").argName("file").longOpt("input").hasArg().build();
		Option xpath = Option.builder(S_XPATH).desc("process expression").argName("XPath").longOpt("evaluate").hasArg()
				.build();
		Option xslt = Option.builder(S_XSLT).desc("process stylesheet").argName("XSLT").longOpt("transform").hasArg()
				.build();
		Option toJson = Option.builder(S_2JSON).desc("convert to JSON").longOpt("convertToJson").build();
		// Collect them
		Options options = new Options();
		options.addOption(help);
		options.addOption(file);
		options.addOption(xpath);
		options.addOption(xslt);
		options.addOption(toJson);
		// If nothing given, nothing will happen
		if (args == null || args.length < 1) {
			printHelp(options);
			return;
		}
		// Parse command line
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(options, args);
			// Work on it
			process(line, options);
		} catch (Throwable e) {
			System.err.printf("Error occurred: %n " + e.getMessage());
		}
	}

	private static void printHelp(Options options) {
		String header = String.format("A tool for working with XML files%n%n");
		String footer = String.format("%nPlease report issues at " + APP_SRC_URL);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(APP_SHORT_NAME, header, options, footer, true);
	}

	private static void process(CommandLine line, Options options) throws Exception {
		if (line.hasOption(S_XPATH)) {
			String file = line.getOptionValue(S_INPUT);
			String xpath = line.getOptionValue(S_XPATH);
			XPathTool.evaluate(file, xpath);
			return;
		}
		if (line.hasOption(S_XSLT)) {
			String file = line.getOptionValue(S_INPUT);
			String xslt = line.getOptionValue(S_XSLT);
			XsltTool.transform(file, xslt);
			return;
		}
		if (line.hasOption(S_2JSON)) {
			String file = line.getOptionValue(S_INPUT);
			JsonTool.convert2Json(file);
			return;
		}
	}
}