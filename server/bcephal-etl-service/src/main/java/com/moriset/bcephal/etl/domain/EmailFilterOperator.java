package com.moriset.bcephal.etl.domain;

public enum EmailFilterOperator {
	CONTAINS, NOT_CONTAINS, NULL, NOT_NULL, EQUALS, NOT_EQUALS, STARTS_WITH, ENDS_WITH;

	public boolean isContains() {
		return this == CONTAINS;
	}

	public boolean isNotContains() {
		return this == NOT_CONTAINS;
	}

	public boolean isNull() {
		return this == NULL;
	}

	public boolean isNotNull() {
		return this == NOT_NULL;
	}

	public boolean isEquals() {
		return this == EQUALS;
	}

	public boolean isNotEquals() {
		return this == NOT_EQUALS;
	}

	public boolean isStartsWith() {
		return this == STARTS_WITH;
	}

	public boolean isEndsWith() {
		return this == ENDS_WITH;
	}
	
	public String buidSql(String value) {
		String sql = null;
		String amount = value != null ? value : "";
		amount = amount.replace("'", "''");
		if (isContains()) {
			sql = "LIKE ".concat("'%").concat(amount).concat("%'");
		} else if (isNotContains()) {
			sql = "NOT LIKE ".concat("'%").concat(amount).concat("%'");
		} else if (isNull()) {
			sql = "IS NULL";
		} else if (isNotNull()) {
			sql = "IS NOT NULL";
		} else if (isEquals()) {
			sql = "= ".concat("'").concat(amount).concat("'");
		} else if (isNotEquals()) {
			sql = "!= ".concat("'").concat(amount).concat("'");
		} else if (isEndsWith()) {
			sql = "LIKE ".concat("'%").concat(amount).concat("'");
		} else if (isStartsWith()) {
			sql = "LIKE ".concat("'").concat(amount).concat("%'");
		}
		return sql;
	}
	
	public String buidSqlForNullValue(String col) {
		String sql = null;
		if (isContains()) {
			sql = "true";
		} else if (isNotContains()) {
			sql = "false";
		} else if (isNull()) {
			sql = "(" + col + " IS NULL OR " +  col + " = '')";
		} else if (isNotNull()) {
			sql = "(" + col + " IS NOT NULL AND " +  col + " != '')";
		} else if (isEquals()) {
			sql = "(" + col + " IS NULL OR " +  col + " = '')";
		} else if (isNotEquals()) {
			sql = "(" + col + " IS NOT NULL AND " +  col + " != '')";
		}else if (isEndsWith()) {
			sql = "true";
		} else if (isStartsWith()) {
			sql = "true";
		}
		return sql;
	}
	
	public String buidColSql(String col) {
		String sql = null;
		if (isContains()) {
			sql = "LIKE ".concat("'%' || ").concat(col).concat(" || '%'");
		} else if (isNotContains()) {
			sql = "NOT LIKE ".concat("'%' || ").concat(col).concat(" || '%'");
		} else if (isNull()) {
			sql = "IS NULL";
		} else if (isNotNull()) {
			sql = "IS NOT NULL";
		} else if (isEquals()) {
			sql = "= ".concat(col);
		} else if (isNotEquals()) {
			sql = "!= ".concat(col);
		}
		if (isEndsWith()) {
			sql = "LIKE ".concat("'%' || ").concat(col);
		} else if (isStartsWith()) {
			sql = "LIKE ".concat(col).concat(" || '%'");
		}
		return sql;
	}
	
	public String buidColSqlForNullValue(String col) {
		String sql = null;
		if (isContains()) {
			sql = "(" + col + " IS NULL OR " +  col + " = '')";
		} else if (isNotContains()) {
			sql = "NOT LIKE ".concat("'%' || ").concat(col).concat(" || '%'");
		} else if (isNull()) {
			sql = "true";
		} else if (isNotNull()) {
			sql = "false";
		} else if (isEquals()) {
			sql = "(" + col + " IS NULL OR " +  col + " = '')";
		} else if (isNotEquals()) {
			sql = "(" + col + " IS NOT NULL AND " +  col + " != '')";
		} else if (isEndsWith()) {
			sql = "(" + col + " IS NULL OR " +  col + " = '')";
		} else if (isStartsWith()) {
			sql = "(" + col + " IS NULL OR " +  col + " = '')";
		}
		return sql;
	}

	
	public String asSql(String column, String value1) {
    	if(isNull()) return "(" + column + " IS NULL OR " + column + " = '')";
    	if(isNotNull()) return "(" + column + " IS NOT NULL AND " + column + " <> '')";
    	
    	if(isEquals()) return column + " = '" + value1 + "'";
    	if(isNotEquals()) return column + " != '" + value1 + "'";
    	
    	if (isStartsWith()) return "UPPER(" + column + ") LIKE '" + value1.toUpperCase() + "%'";
       // if (!isStartsWith()) return "UPPER(" + column + ") NOT LIKE '" + value1.toUpperCase() + "%'";
        if (isEndsWith()) return "UPPER(" + column + ") LIKE '%" + value1.toUpperCase() + "'";
        //if (!isEndsWith()) return "UPPER(" + column + ") NOT LIKE '%" + value1.toUpperCase() + "'";
        
        if (isContains()) {
        	String value = "%" + value1 + "%";			
			return "UPPER(" + column + ") LIKE '" + value.toUpperCase() + "'";
        }
        if (isNotContains()) {
        	String value = "%" + value1 + "%";			
			return "UPPER(" + column + ") NOT LIKE '" + value.toUpperCase() + "'";
        }
    	return null;
    }

}
