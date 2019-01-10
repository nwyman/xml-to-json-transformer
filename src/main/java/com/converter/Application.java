package com.converter;

import com.converter.model.TransformNodeMap;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.commons.cli.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Clock;

public class Application {

	public static void main(String[] args) {
		Options options = buildOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;

		try {
			cmd = parser.parse( options, args);
		}
		catch(ParseException ex) {
			System.out.println("ERROR - Cannot parse arguments.");
			return;
		}

		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("XmlConverter", options);
			return;
		}

		if(!cmd.hasOption("inputFile")) {
			System.out.println("Please specify a XML file. Use --help for more details.");
			return;
		}

		if(!cmd.hasOption("transformFile")) {
			System.out.println("Please specify a transform file. Use --help for more details.");
			return;
		}

		try {
			FileHandler fileHandler = new FileHandler(DocumentBuilderFactory.newInstance(), new ObjectMapper(), new DefaultPrettyPrinter());

			Document document = fileHandler.readXML(cmd.getOptionValue("inputFile"));

			TransformNodeMap transforms = fileHandler.readTransformJson(cmd.getOptionValue("transformFile"));

			NodeTransformer transformer = new NodeTransformer(transforms, JsonNodeFactory.instance, Clock.systemDefaultZone());

			JsonNode jsonObject = transformer.applyTransforms(document.getDocumentElement());

			String outputFile = cmd.hasOption("outputFile") ? cmd.getOptionValue("outputFile") : "output.json";
			fileHandler.writeJson(outputFile, jsonObject);
		}
		catch(ParserConfigurationException | IOException | SAXException ex) {
			System.out.println("Could not transform your XML file, see message above.");
			return;
		}

		System.out.println("Your JSON file has been generated.");
	}

	private static Options buildOptions() {
		Options options = new Options();

		Option inputFile = Option.builder("i")
			.longOpt("inputFile")
			.argName("inputFile")
			.hasArg()
			.desc("XML input file name")
			.build();

		options.addOption(inputFile);

		Option outputFile = Option.builder("o")
			.longOpt("outputFile")
			.argName("outputFile")
			.hasArg()
			.desc("JSON output file name. If one is not provided, it will default to output.json.")
			.build();

		options.addOption(outputFile);

		Option transformFile = Option.builder("t")
			.longOpt("transformFile")
			.argName("transformFile")
			.hasArg()
			.desc("JSON transform file name.")
			.build();

		options.addOption(transformFile);

		options.addOption(new Option("h", "help", false, "Show arguments"));

		return options;
	}
}
