/**
 * 
 */
package com.moriset.bcephal.domain.filters;

/**
 * @author Joseph Wambo
 *
 */
public class GridFilterOperator {
	
	public static String LIKE_OPERATOR = "Like";
	public static String LIKE_OPERATOR1 = "'Like'";
	public static String NOT_LIKE_OPERATOR = "NotLike";
	public static String NOT_LIKE_OPERATOR1 = "Not'Like'";
	
	public boolean isGreater(String operator) {
		return "Greater".equalsIgnoreCase(operator)
				|| ">".equalsIgnoreCase(operator);
	}
	
	public boolean isGreaterOrEquals(String operator) {
		return "GreaterOrEqual".equalsIgnoreCase(operator)
				|| ">=".equalsIgnoreCase(operator);
	}
	
	public boolean isLess(String operator) {
		return "Less".equalsIgnoreCase(operator)
				|| "<".equalsIgnoreCase(operator);
	}
	
	public boolean isLessOrEquals(String operator) {
		return "LessOrEqual".equalsIgnoreCase(operator)
				|| "<=".equalsIgnoreCase(operator);
	}
	
	public boolean isContains(String operator) {
		return "Contains".equalsIgnoreCase(operator);
	}
	
	public boolean isNotContains(String operator) {
		return "NotContains".equalsIgnoreCase(operator)
				|| "Not_Contains".equalsIgnoreCase(operator);
	}
	
	public boolean isEndsWith(String operator) {
		return "EndsWith".equalsIgnoreCase(operator)
				|| "Ends_With".equalsIgnoreCase(operator);
	}
	
	public boolean isStartsWith(String operator) {
		return "StartsWith".equalsIgnoreCase(operator)
				|| "Starts_With".equalsIgnoreCase(operator);
	}
			
	public boolean isNull(String operator) {
		return "NULL".equalsIgnoreCase(operator)
				|| "ISNULL".equalsIgnoreCase(operator);
	}
	
	public boolean isNotNull(String operator) {
		return "NOT NULL".equalsIgnoreCase(operator)
				|| "NOT_NULL".equalsIgnoreCase(operator)
				|| "ISNOTNULL".equalsIgnoreCase(operator)
				|| "NotIsNull".equalsIgnoreCase(operator);
		
	}
	
	public boolean isNullOrEmpty(String operator) {
		return "IsNullOrEmpty".equalsIgnoreCase(operator)
				|| "Null_or_empty".equalsIgnoreCase(operator);
	}
	
	public boolean isNotNullOrEmpty(String operator) {
		return "NotIsNullOrEmpty".equalsIgnoreCase(operator)
			|| "not_null_or_empty".equalsIgnoreCase(operator);
	}
	
	public boolean isEquals(String operator) {
		return "=".equalsIgnoreCase(operator)
				|| "=".equalsIgnoreCase(operator)
				|| "Equal".equalsIgnoreCase(operator)
				|| "Equals".equalsIgnoreCase(operator);
	}
	
	public boolean isNotEquals(String operator) {
		return "=".equalsIgnoreCase(operator)
				|| "!=".equalsIgnoreCase(operator)
				|| "<>".equalsIgnoreCase(operator)
				|| "NotEqual".equalsIgnoreCase(operator)
				|| "NotEquals".equalsIgnoreCase(operator)
				|| "Not_Equal".equalsIgnoreCase(operator)
				|| "Not_Equals".equalsIgnoreCase(operator);
	}
	

}
