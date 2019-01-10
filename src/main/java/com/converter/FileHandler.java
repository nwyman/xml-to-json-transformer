package com.converter;

import com.converter.model.TransformNodeMap;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

class FileHandler {
	private DocumentBuilderFactory documentBuilderFactory;
	private ObjectMapper objectMapper;
	private DefaultPrettyPrinter prettyPrinter;

	FileHandler(DocumentBuilderFactory documentBuilderFactory, ObjectMapper objectMapper, DefaultPrettyPrinter prettyPrinter) {
		this.documentBuilderFactory = documentBuilderFactory;
		this.objectMapper = objectMapper;
		this.prettyPrinter = prettyPrinter;
	}

	Document readXML(String filePath) throws IOException, SAXException, ParserConfigurationException {
		File file = new File(filePath);
		Document document;

		try {
			DocumentBuilder documentBuilder = this.documentBuilderFactory.newDocumentBuilder();

			document = documentBuilder.parse(file);
		}
		catch(ParserConfigurationException | IOException | SAXException ex) {
			System.out.println("ERROR - Cannot parse input XML file.");
			throw ex;
		}

		return document;
	}

	TransformNodeMap readTransformJson(String filePath) throws IOException {
		File file = new File(filePath);
		TransformNodeMap nodeMap;

		try {
			nodeMap = objectMapper.readValue(file, TransformNodeMap.class);
		}
		catch (IOException ex) {
			System.out.println("ERROR - Cannot read transform file.");
			throw ex;
		}

		return nodeMap;
	}

	void writeJson(String filePath, JsonNode jsonNode) throws IOException {
		try {
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

			prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

			objectMapper.writer(prettyPrinter).writeValue(new File(filePath), jsonNode);
		}
		catch (IOException ex) {
			System.out.println("ERROR - Cannot write output JSON file.");
			throw ex;
		}
	}
}
