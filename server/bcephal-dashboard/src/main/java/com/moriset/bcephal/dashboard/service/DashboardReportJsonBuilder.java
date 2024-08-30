package com.moriset.bcephal.dashboard.service;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.moriset.bcephal.dashboard.domain.DashboardReportField;

public class DashboardReportJsonBuilder {
	public DashboardReportJsonBuilder() {

	}

	public ArrayNode build(List<DashboardReportField> fields, List<Object[]> rows) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();		
		for (Object obj : rows) {
			Object[] row = null;
			if (obj instanceof Object[]) {
				row = (Object[]) obj;
			} else {
				row = new Object[] { obj };
			}
			JsonNode node = build(fields, row, mapper);
			arrayNode.add(node);
		}
		return arrayNode;
	}

	private ObjectNode build(List<DashboardReportField> fields, Object[] row, ObjectMapper mapper) {
		ObjectNode node = mapper.createObjectNode();
		for (DashboardReportField field : fields) {
			Object value = row[field.getPosition()];
			put(node, field, value);
		}
		return node;
	}

	private void put(ObjectNode node, DashboardReportField field, Object value) {
		if (field.getType().isMeasure() || field.getType().isCalculatedMeasure()) {
			node.put(field.getDimensionName(), value != null ? new BigDecimal(value.toString()) : BigDecimal.ZERO);
		} else if (field.getType().isPeriod()) {
			node.put(field.getDimensionName(), value != null ? value.toString() : "");
		} else {
			node.put(field.getDimensionName(), value != null ? value.toString() : "");
		}
	}
}
