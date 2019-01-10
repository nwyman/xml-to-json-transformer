package com.converter.model;

import java.util.Map;

public class TransformNode {
	private TransformType type;
	private String destinationName;
	private Map<String, String> valueTransforms;
	private String format;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public TransformType getType() {
		return type;
	}

	public void setType(TransformType type) {
		this.type = type;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public boolean hasDestinationName() {
		return destinationName != null;
	}

	public Map<String, String> getValueTransforms() {
		return valueTransforms;
	}

	public void setValueTransforms(Map<String, String> valueTransforms) {
		this.valueTransforms = valueTransforms;
	}

	public boolean hasValueTransforms(String value) {
		return this.valueTransforms != null && this.valueTransforms.get(value) != null;
	}
}
