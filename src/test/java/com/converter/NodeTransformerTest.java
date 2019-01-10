package com.converter;

import com.converter.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class NodeTransformerTest {
	private NodeTransformer nodeTransformer;
	private Document document;

	@Test
	public void applyTransformShouldReturnAnArrayNode() {
		Element root = document.createElement("arrayTest");
		document.appendChild(root);

		JsonNode result = nodeTransformer.applyTransforms(document.getDocumentElement());

		assertTrue(result instanceof ArrayNode);
	}

	@Test
	public void applyTransformShouldAddChildrenToArrayNode() {
		Element root = document.createElement("arrayTest");
		document.appendChild(root);

		String testText1 = "string test text 1";
		Element stringElement1 = document.createElement("stringTest");
		root.appendChild(stringElement1);
		stringElement1.insertBefore(document.createTextNode(testText1), stringElement1.getLastChild());

		String testText2 = "string test text 2";
		Element stringElement2 = document.createElement("stringTest");
		root.appendChild(stringElement2);
		stringElement2.insertBefore(document.createTextNode(testText2), stringElement2.getLastChild());

		JsonNode result = nodeTransformer.applyTransforms(document.getDocumentElement());

		assertEquals(2, result.size());
		assertEquals(testText2, result.get(1).textValue());
	}

	@Test
	public void applyTransformShouldReturnAnObjectNode() {
		Element root = document.createElement("objectTest");
		document.appendChild(root);

		JsonNode result = nodeTransformer.applyTransforms(document.getDocumentElement());

		assertTrue(result instanceof ObjectNode);
	}

	@Test
	public void applyTransformShouldAddPropertiesToAnObjectNode() {
		Element root = document.createElement("objectTest");
		document.appendChild(root);

		String testText1 = "string test text 1";
		Element stringElement1 = document.createElement("property1Test");
		root.appendChild(stringElement1);
		stringElement1.insertBefore(document.createTextNode(testText1), stringElement1.getLastChild());

		String testText2 = "string test text 2";
		Element stringElement2 = document.createElement("property2Test");
		root.appendChild(stringElement2);
		stringElement2.insertBefore(document.createTextNode(testText2), stringElement2.getLastChild());

		JsonNode result = nodeTransformer.applyTransforms(document.getDocumentElement());

		assertEquals(testText1, result.get("property1Test").textValue());
		assertEquals(testText2, result.get("property2Test").textValue());
	}

	@Test
	public void applyTransformShouldRenamePropertiesOnAnObjectNode() {
		Element root = document.createElement("objectTest");
		document.appendChild(root);

		String testText1 = "string test text 1";
		Element stringElement1 = document.createElement("renamePropertyTest");
		root.appendChild(stringElement1);
		stringElement1.insertBefore(document.createTextNode(testText1), stringElement1.getLastChild());

		JsonNode result = nodeTransformer.applyTransforms(document.getDocumentElement());

		assertEquals(testText1, result.get("sampleRename").textValue());
	}

	@Test
	public void applyTransformShouldReturnATextNode() {
		String testText = "string test text";
		Element root = document.createElement("stringTest");
		document.appendChild(root);
		root.insertBefore(document.createTextNode(testText), root.getLastChild());

		JsonNode result = nodeTransformer.applyTransforms(document.getDocumentElement());

		assertTrue(result instanceof TextNode);
		assertEquals(testText, result.textValue());
	}

	@Test
	public void applyTransformShouldReturnAPropertyWithValueTransformed() {
		String testText = "m";
		Element root = document.createElement("valueTest");
		document.appendChild(root);
		root.insertBefore(document.createTextNode(testText), root.getLastChild());

		JsonNode result = nodeTransformer.applyTransforms(document.getDocumentElement());

		assertEquals("male", result.textValue());
	}

	@Test
	public void applyTransformShouldReturnAPropertyWithoutValueTransformedIfItDoesNotExist() {
		String testText = "f";
		Element root = document.createElement("valueTest");
		document.appendChild(root);
		root.insertBefore(document.createTextNode(testText), root.getLastChild());

		JsonNode result = nodeTransformer.applyTransforms(document.getDocumentElement());

		assertEquals("f", result.textValue());
	}

	@Test
	public void applyTransformShouldReturnAYearsSinceNumericNode() {
		String testDate = "12/15/2000";
		Element root = document.createElement("yearsSinceTest");
		document.appendChild(root);
		root.insertBefore(document.createTextNode(testDate), root.getLastChild());

		JsonNode result = nodeTransformer.applyTransforms(document.getDocumentElement());

		assertTrue(result instanceof NumericNode);
		assertEquals(18, result.intValue());
	}

	@Before
	public void setup() throws ParserConfigurationException {
		TransformNodeMap transformMap = new TransformNodeMap();

		TransformNode arrayTransformNode = new TransformNode();
		arrayTransformNode.setType(TransformType.ARRAY);
		transformMap.put("arrayTest", arrayTransformNode);

		TransformNode objectTransformNode = new TransformNode();
		objectTransformNode.setType(TransformType.OBJECT);
		transformMap.put("objectTest", objectTransformNode);

		TransformNode stringTransformNode = new TransformNode();
		stringTransformNode.setType(TransformType.STRING);
		transformMap.put("stringTest", stringTransformNode);

		TransformNode dateTransformNode = new TransformNode();
		dateTransformNode.setFormat("MM/dd/yyyy");
		dateTransformNode.setType(TransformType.YEARS_SINCE);
		transformMap.put("yearsSinceTest", dateTransformNode);

		TransformNode property1TransformNode = new TransformNode();
		property1TransformNode.setType(TransformType.STRING);
		transformMap.put("property1Test", property1TransformNode);

		TransformNode property2TransformNode = new TransformNode();
		property2TransformNode.setType(TransformType.STRING);
		transformMap.put("property2Test", property2TransformNode);

		TransformNode renamePropertyTransformNode = new TransformNode();
		renamePropertyTransformNode.setType(TransformType.STRING);
		renamePropertyTransformNode.setDestinationName("sampleRename");
		transformMap.put("renamePropertyTest", renamePropertyTransformNode);

		TransformNode valueTransformNode = new TransformNode();
		valueTransformNode.setType(TransformType.STRING);
		Map<String,String> valueMap = new HashMap<>();
		valueMap.put("m", "male");
		valueTransformNode.setValueTransforms(valueMap);
		transformMap.put("valueTest", valueTransformNode);

		LocalDate monday = LocalDate.of(2019, 1, 1);
		Clock clock = Clock.fixed(monday.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

		this.nodeTransformer = new NodeTransformer(transformMap, JsonNodeFactory.instance, clock);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
	}
}
