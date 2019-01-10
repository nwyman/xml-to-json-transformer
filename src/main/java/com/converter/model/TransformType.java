package com.converter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransformType {
	@JsonProperty("array")
	ARRAY,
	@JsonProperty("object")
	OBJECT,
	@JsonProperty("string")
	STRING,
	@JsonProperty("yearsSince")
	YEARS_SINCE
}
