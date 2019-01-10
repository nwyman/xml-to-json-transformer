package com.converter;

import com.converter.model.TransformNode;
import com.converter.model.TransformNodeMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.YEARS;

class NodeTransformer {
	private TransformNodeMap transformMap;
	private JsonNodeFactory nodeFactory;
	private Clock clock;

	NodeTransformer (TransformNodeMap transformMap, JsonNodeFactory nodeFactory, Clock clock) {
		this.transformMap = transformMap;
		this.nodeFactory = nodeFactory;
		this.clock = clock;
	}

	JsonNode applyTransforms(Element elementNode) {
		TransformNode transform = transformMap.get(elementNode.getTagName());
		JsonNode transformedNode;

		switch (transform.getType()) {
			case ARRAY:
				ArrayNode arrayNode = nodeFactory.arrayNode();

				NodeList arrayNodeList = elementNode.getChildNodes();
				for (int i = 0; i < arrayNodeList.getLength(); i++) {
					Node currentNode = arrayNodeList.item(i);
					if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
						arrayNode.add(applyTransforms((Element) currentNode));
					}
				}

				transformedNode = arrayNode;
				break;
			case OBJECT:
				ObjectNode objectNode = nodeFactory.objectNode();

				NodeList objectNodeList = elementNode.getChildNodes();
				for (int i = 0; i < objectNodeList.getLength(); i++) {
					Node currentNode = objectNodeList.item(i);
					if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
						Element currentElement = (Element) currentNode;
						TransformNode childTransform = transformMap.get(currentElement.getTagName());

						objectNode.set(childTransform.hasDestinationName() ? childTransform.getDestinationName() : currentElement.getTagName(), applyTransforms(currentElement));
					}
				}

				transformedNode = objectNode;
				break;
			case STRING:
				String nodeValue = transform.hasValueTransforms(elementNode.getTextContent()) ?
					transform.getValueTransforms().get(elementNode.getTextContent()) :
					elementNode.getTextContent();

				transformedNode = nodeFactory.textNode(nodeValue);
				break;
			case YEARS_SINCE:
				LocalDate today = LocalDate.now(clock);
				LocalDate nodeDate = LocalDate.parse(elementNode.getTextContent(), DateTimeFormatter.ofPattern(transform.getFormat()));
				long years = YEARS.between(nodeDate, today);

				transformedNode = nodeFactory.numberNode(years);
				break;
			default:
				transformedNode = nodeFactory.nullNode();
				break;
		}

		return transformedNode;
	}
}
