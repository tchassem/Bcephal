/**
 * 
 */
package com.moriset.bcephal.service.condition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.domain.condition.ConditionalExpression;
import com.moriset.bcephal.domain.condition.ConditionalExpressionItem;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.MeasureOperator;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.repository.filters.SpotRepository;
import com.moriset.bcephal.service.SpotEvaluator;
import com.moriset.bcephal.service.filters.ISpotService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;

/**
 * @author EMMENI Emmanuel
 *
 */
@Component
@Data
@Slf4j
public class ConditionExpressionEvaluator {
			
	@Autowired
	SpotEvaluator spotEvaluator;
	
	@Autowired
	SpotRepository spotRepository;
	
	@Autowired
	ISpotService spotService;

	
	/**
	 * 
	 * @param item
	 * @return
	 */
	public double isValidCondExprItem(ConditionalExpressionItem item) {
		
		String tmpExp = buildItemString(item);
		Expression exp = new ExpressionBuilder(tmpExp).operator(getCustomOp(item.getOperator())).build();

		return exp.evaluate();
	}
	
	/**
	 * 
	 * @param expr
	 * @return
	 */
	public  boolean isValidCondExpr(ConditionalExpression expr) {
		log.debug("Evaluate expression...");
		String finalExprItems = "";		
		if (expr == null) {
			log.debug("Expression is NULL!");
			return true;
		}
		List<ConditionalExpressionItem> items = expr.getItemListChangeHandler().getItems();
		log.debug("Expression items count : {}", items.size());
		if(items.size() == 0) {
			log.debug("Expression items is empty!");
			return true;
		}
		log.trace("Sorting expression items...");
		Collections.sort(items, new Comparator<ConditionalExpressionItem>() {
			@Override
			public int compare(ConditionalExpressionItem o1, ConditionalExpressionItem o2) {
				return ((Integer)o1.getPosition()).compareTo(o2.getPosition());
			}
		});
		for (ConditionalExpressionItem item : items) {
			if(!finalExprItems.isEmpty()) {
				finalExprItems = finalExprItems.concat(" ");
				if (item.getFilterVerb().name().equalsIgnoreCase(FilterVerb.AND.toString())) {
					finalExprItems = finalExprItems.concat("&");
				} else {
					finalExprItems = finalExprItems.concat("|");
				}
				finalExprItems = finalExprItems.concat(" ");
			}
			if (item.getOpenBrackets() != null && !item.getOpenBrackets().isEmpty()) {
				finalExprItems = finalExprItems.concat(item.getOpenBrackets());				
			}
			
			try {
				finalExprItems = finalExprItems.concat(String.valueOf(isValidCondExprItem(item)));
			} catch (NullPointerException e) {
				log.error("NullPointer expression: " + e.getMessage());
				return false;
			} catch (Exception e) {
				log.error("Some conditional expression error appear: " + e.getMessage());
				return false;
			}
			
			if (item.getCloseBrackets() != null && !item.getCloseBrackets().isEmpty()) {
				finalExprItems = finalExprItems.concat(item.getCloseBrackets());			
			}
		}

		List<Operator> operators = new ArrayList<>();
		operators.add(getBoolOp("&"));
		operators.add(getBoolOp("|"));
		
		Expression exp = null;
		try {
			exp = new ExpressionBuilder(finalExprItems).operator(operators).build();
		} catch (EmptyStackException e) {
			log.error("Malformatted expression: " + e.getMessage());
			return false;
		} catch (Exception e) {
			log.error("Some conditional expression error appear: " + e.getMessage());
			return false;
		}
		
		double res = exp.evaluate();
		return res == 1 ? true : false;
	}
	
	/**
	 * 
	 * @param currItem
	 * @return
	 */
	private String buildItemString(ConditionalExpressionItem currItem) {
		
		Optional<Spot> spot1 = spotRepository.findById(currItem.getSpotId1());
		List<UniverseFilter> filters = new ArrayList<>();
		if(spot1.isPresent()) {
			filters = spotService.buildSpotReportFilter(spot1.get());
		}
		BigDecimal valSpot1 = null;
		BigDecimal valSpot2 = null;
		try {
			valSpot1 = spotEvaluator.evaluate(spot1.isPresent() ? spot1.get() : null, filters);
			if (currItem.getSpotId2() != null) {
				Optional<Spot> spot2 = spotRepository.findById(currItem.getSpotId2());
				valSpot2 = spotEvaluator.evaluate(spot2.isPresent() ? spot2.get() : null,filters);
			}
		} catch (Exception e) {
			log.error("An error occur during the spot evaluation: {}", e.getMessage());
		}
		
		return valSpot1.toString() + currItem.getOperator() + 
					(currItem.getValue2Type().equals(DimensionType.FREE) ? currItem.getValue().toString() : valSpot2.toString());
	}
	
	/**
	 * 
	 * @param expr
	 * @return
	 */
	public  Operator getBoolOp(String op) {
		
		return new Operator(op, 2, true, 0) {
			
			@Override
			public double apply(double... args) {
				if(op.equals("&")){
					if(args[0] == 1 && args[1] == 1){
						return 1;
					}
					return 0;
				} else {
					if(args[0] == 0 && args[1] == 0){
						return 0;
					}
					return 1;
				}
			}
		};
	}
	
	/**
	 * 
	 * @param expr
	 * @return
	 */
	public  Operator getCustomOp(String op) {
		
		Operator operator = new Operator(op, 2, true, 0) {
			
			@Override
			public double apply(double... args) {
				if(MeasureOperator.EQUALS.equalsIgnoreCase(op)){
					if(args[0] == args[1]) {
						return 1d;
					}
					return 0d;
				}
				else if(MeasureOperator.NOT_EQUALS.equalsIgnoreCase(op)){
					if(args[0] != args[1]) {
						return 1d;
					}
					return 0d;
				}
				else if(MeasureOperator.GRETTER_OR_EQUALS.equalsIgnoreCase(op)){
					if(args[0] >= args[1]) {
						return 1d;
					}
					return 0d;
				}
				else if(MeasureOperator.LESS_OR_EQUALS.equalsIgnoreCase(op)){
					if(args[0] <= args[1]) {
						return 1d;
					}
					return 0d;
				}
				else if(MeasureOperator.LESS_THAN.equalsIgnoreCase(op)){
					if(args[0] < args[1]) {
						return 1d;
					}
					return 0d;
				}
				else if(MeasureOperator.GRETTER_THAN.equalsIgnoreCase(op)){
					if(args[0] > args[1]) {
						return 1d;
					}
					return 0d;
				}
				return 0d;
			}
		};
		
		return operator;
	}

}
