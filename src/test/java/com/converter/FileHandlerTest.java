package com.converter;

import com.converter.model.TransformNode;
import com.converter.model.TransformNodeMap;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class FileHandlerTest {
	@Mock
	private DocumentBuilderFactory documentBuilderFactory;

	@Mock
	private DocumentBuilder documentBuilder;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private DefaultPrettyPrinter prettyPrinter;

	@Mock
	private Document document;

	@Mock
	private ObjectWriter objectWriter;

	@InjectMocks
	private FileHandler fileHandler;

	@Test
	public void readXmlShouldUseSuppliedFileNameAndReturnADocument() throws ParserConfigurationException, IOException, SAXException {
		String fileName = "test.xml";

		Mockito.when(documentBuilderFactory.newDocumentBuilder()).thenReturn(documentBuilder);
		Mockito.when(documentBuilder.parse(any(File.class))).thenReturn(document);

		Document result = fileHandler.readXML(fileName);

		ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
		Mockito.verify(documentBuilder).parse(captor.capture());

		assertEquals(fileName, captor.getValue().getName());
		assertEquals(document, result);
	}

	@Test
	public void writeJsonShouldOutputAFile() throws IOException {
		String fileName = "output.json";

		JsonNode jsonNode = JsonNodeFactory.instance.numberNode(1);

		Mockito.when(objectMapper.writer(any(PrettyPrinter.class))).thenReturn(objectWriter);
		Mockito.doNothing().when(objectWriter).writeValue(any(File.class), any(JsonNode.class));

		fileHandler.writeJson(fileName, jsonNode);

		ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
		ArgumentCaptor<JsonNode> nodeCaptor = ArgumentCaptor.forClass(JsonNode.class);
		Mockito.verify(objectWriter).writeValue(fileCaptor.capture(), nodeCaptor.capture());

		assertEquals(fileName, fileCaptor.getValue().getName());
		assertEquals(jsonNode, nodeCaptor.getValue());
	}

	@Test
	public void readJsonShouldUseSuppliedFileNameAndReturnNodeMap() throws IOException {
		String fileName = "test.json";

		TransformNodeMap nodeMap = new TransformNodeMap();
		TransformNode node = new TransformNode();
		node.setFormat("MM/dd/yyyy");

		Mockito.when(objectMapper.readValue(any(File.class), any(Class.class))).thenReturn(nodeMap);

		TransformNodeMap result = fileHandler.readTransformJson(fileName);

		ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
		Mockito.verify(objectMapper).readValue(captor.capture(), any(Class.class));

		assertEquals(fileName, captor.getValue().getName());
		assertEquals(nodeMap, result);
	}

	@Test(expected = IOException.class)
	public void readJsonShouldThrowAnErrorIfCannotParseFile() throws IOException {
		String fileName = "test.json";

		Mockito.when(objectMapper.readValue(any(File.class), any(Class.class))).thenThrow(JsonParseException.class);

		fileHandler.readTransformJson(fileName);
	}
}
