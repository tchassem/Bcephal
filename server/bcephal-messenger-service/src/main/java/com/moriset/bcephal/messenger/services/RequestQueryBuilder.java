package com.moriset.bcephal.messenger.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.moriset.bcephal.messenger.model.ColumnFilter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import lombok.Data;

@Data
public class RequestQueryBuilder<B> {

	CriteriaBuilder criteriaBuilder;
	CriteriaQuery<?> query;
	Root<B> root;

	List<Predicate> predicates;

	public RequestQueryBuilder(EntityManager em, Class<B> clazz) {
		if (em != null) {
			setCriteriaBuilder(em.getCriteriaBuilder());
			setQuery(criteriaBuilder.createQuery(clazz));
			setRoot(query.from(clazz));
		}
		predicates = new ArrayList<>();
	}

	public RequestQueryBuilder(Root<B> root, CriteriaQuery<?> query2, CriteriaBuilder criteriaBuilder) {
		setCriteriaBuilder(criteriaBuilder);
		setQuery(query2);
		setRoot(root);
		predicates = new ArrayList<>();
	}

	public void add(Predicate predicate) {
		if (predicate != null) {
			predicates.add(predicate);
		}
	}

	public void addAll(List<? extends Predicate> predicate) {
		if (predicate != null) {
			predicates.addAll(predicate);
		}
	}

	public void addFilter(ColumnFilter filter) {
		if (filter != null) {
			if (!filter.isGrouped()) {
				Predicate fil = buildPredicateFilter(filter);
				if (fil != null) {
					predicates.add(fil);
				}
			} else {
				if (filter.getItems().size() > 0) {
					List<Predicate> predicates_ = new ArrayList<>();
					filter.getItems().forEach(itemFilter -> {
						Predicate fil = buildPredicateFilter(itemFilter);
						if (fil != null) {
							predicates_.add(fil);
						}
					});
					Predicate[] predicat = predicates_.toArray(new Predicate[predicates.size()]);
					if (filter.getOperation().trim().equalsIgnoreCase("or")) {
						predicates.add(criteriaBuilder.or(predicat));
					} else {
						predicates.add(criteriaBuilder.and(predicat));
					}
				}
			}
		}
	}

	private Predicate buildPredicateFilter(ColumnFilter filter) {
		if (filter != null) {
			boolean isPeriod = filter.getDimensionType() != null && filter.getDimensionType().isPeriod();
			boolean isDate = filter.getType() != null && filter.getType().equals(Date.class);
			boolean isDateValid = isPeriod || isDate;
			if (filter.getOperation().equalsIgnoreCase(GridFilterOperator.EQUALS_OPERATOR)
					|| filter.getOperation().equalsIgnoreCase(GridFilterOperator.EQUAL_OPERATOR)) {
				if (isDateValid) {
					return Equals(buildPeriodValue(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && filter.getType().equals(Boolean.class)) {
					return Equals(Boolean.valueOf(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
					return Equals(getEnumValue(filter), filter.getName());
				}
				return Equals(filter.getValue(), filter.getName());
			} else if (filter.getOperation().equalsIgnoreCase(GridFilterOperator.GREATER)) {
				if (isDateValid) {
					return greaterCriteria(buildPeriodValue(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
					return greaterCriteria(getEnumValue(filter), filter.getName());
				} else if (filter.getType() != null && BigDecimal.class.equals(filter.getType())) {
					return greaterCriteria(new BigDecimal(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Long.class.equals(filter.getType())) {
					return greaterCriteria(Long.valueOf(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Integer.class.equals(filter.getType())) {
					return greaterCriteria(Integer.valueOf(filter.getValue()), filter.getName());
				}
				return greaterCriteria(filter.getValue(), filter.getName());
			} else if (filter.getOperation().equalsIgnoreCase(GridFilterOperator.GREATER_OR_EQUALS_OPERATOR)) {
				if (isDateValid) {
					return greaterThanOrEqualToCriteria(buildPeriodValue(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
					return greaterThanOrEqualToCriteria(getEnumValue(filter), filter.getName());
				} else if (filter.getType() != null && BigDecimal.class.equals(filter.getType())) {
					return greaterThanOrEqualToCriteria(new BigDecimal(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Long.class.equals(filter.getType())) {
					return greaterThanOrEqualToCriteria(Long.valueOf(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Integer.class.equals(filter.getType())) {
					return greaterThanOrEqualToCriteria(Integer.valueOf(filter.getValue()), filter.getName());
				}
				return greaterThanOrEqualToCriteria(filter.getValue(), filter.getName());
			} else if (filter.getOperation().equalsIgnoreCase(GridFilterOperator.LESS)) {
				if (isDateValid) {
					return lessCriteria(buildPeriodValue(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
					return lessCriteria(getEnumValue(filter), filter.getName());
				} else if (filter.getType() != null && BigDecimal.class.equals(filter.getType())) {
					return lessCriteria(new BigDecimal(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Long.class.equals(filter.getType())) {
					return lessCriteria(Long.valueOf(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Integer.class.equals(filter.getType())) {
					return lessCriteria(Integer.valueOf(filter.getValue()), filter.getName());
				}
				return lessCriteria(filter.getValue(), filter.getName());
			} else if (filter.getOperation().equalsIgnoreCase(GridFilterOperator.LESS_OR_EQUALS_OPERATOR)) {
				if (isDateValid) {
					return lessThanOrEqualToCriteria(buildPeriodValue(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
					return lessThanOrEqualToCriteria(getEnumValue(filter), filter.getName());
				} else if (filter.getType() != null && BigDecimal.class.equals(filter.getType())) {
					return lessThanOrEqualToCriteria(new BigDecimal(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Long.class.equals(filter.getType())) {
					return lessThanOrEqualToCriteria(Long.valueOf(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Integer.class.equals(filter.getType())) {
					return lessThanOrEqualToCriteria(Integer.valueOf(filter.getValue()), filter.getName());
				}
				return lessThanOrEqualToCriteria(filter.getValue(), filter.getName());
			} else if (filter.getOperation().equalsIgnoreCase(GridFilterOperator.NOT_EQUAL)
					|| filter.getOperation().equalsIgnoreCase(GridFilterOperator.NOT_EQUALS)) {
				if (isDateValid) {
					return notEqualCriteria(buildPeriodValue(filter.getValue()), filter.getName());
				} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
					return notEqualCriteria(getEnumValue(filter), filter.getName());
				}
				return notEqualCriteria(filter.getValue(), filter.getName());
			} else if (filter.getOperation().equalsIgnoreCase(GridFilterOperator.STARTS_WITH_OPERATOR)) {
				if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
					return buildEnumCriteria(getStartWithEnumValues(filter), filter.getName());
				}
				return startWithCriteria(filter.getValue(), filter.getName());
			} else if (filter.getOperation().equalsIgnoreCase(GridFilterOperator.ENDS_WITH_OPERATOR)) {
				if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
					return buildEnumCriteria(getEndsWithEnumValues(filter), filter.getName());
				}
				return endsWithCriteria(filter.getValue(), filter.getName());
			} else if (filter.getOperation().equalsIgnoreCase(GridFilterOperator.CONTAINS_OPERATOR)) {
				if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
					return buildEnumCriteria(getLikeEnumValues(filter), filter.getName());
				}
				return likeCriteria(addWildCards(filter.getValue()), filter.getName());
			} else if (filter.getOperation().equalsIgnoreCase(GridFilterOperator.NOT_CONTAINS_OPERATOR)) {
				if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
					return buildEnumCriteria(getNotLikeEnumValues(filter), filter.getName());
				}
				return notLikeCriteria(addWildCards(filter.getValue()), filter.getName());
			} else if (filter.getOperation().equalsIgnoreCase(GridFilterOperator.IS_NOT_NULL_OR_EMPTY)) {
				return isNotNull(filter.getName());
			} else if (filter.getOperation().equalsIgnoreCase(GridFilterOperator.IS_NULL_OR_EMPTY)) {
				return isNull(filter.getName());
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private Predicate buildEnumCriteria(List<Enum> startWithEnumValues, String name) {
		if (startWithEnumValues != null && startWithEnumValues.size() > 0) {
			List<Predicate> predicat_ = new ArrayList<>();
			startWithEnumValues.forEach(value -> {
				predicat_.add(Equals(value, name));
			});
			Predicate[] predicat = predicat_.toArray(new Predicate[predicat_.size()]);
			return criteriaBuilder.or(predicat);
		}
		return null;
	}

	private interface filterCondition {
		boolean check(String code, String value);
	}

	@SuppressWarnings("rawtypes")
	private List<Enum> getNotLikeEnumValues(ColumnFilter filter) {
		return getEnumValues(filter, (code, value) -> code != null && value != null && !code.contains(value));
	}

	@SuppressWarnings("rawtypes")
	private List<Enum> getLikeEnumValues(ColumnFilter filter) {
		return getEnumValues(filter, (code, value) -> code != null && value != null && code.contains(value));
	}

	@SuppressWarnings("rawtypes")
	private List<Enum> getEndsWithEnumValues(ColumnFilter filter) {
		return getEnumValues(filter, (code, value) -> code != null && value != null && code.endsWith(value));
	}

	@SuppressWarnings("rawtypes")
	private List<Enum> getStartWithEnumValues(ColumnFilter filter) {
		return getEnumValues(filter, (code, value) -> code != null && value != null && code.startsWith(value));
	}

	@SuppressWarnings("rawtypes")
	private List<Enum> getEnumValues(ColumnFilter filter, filterCondition condition) {
		if (filter == null || filter.getType() == null || filter.getValue() == null) {
			return new ArrayList<>();
		}
		String value = filter.getValue().toUpperCase();
		List<Enum> objs = new ArrayList<>();
		for (Object object : filter.getType().getEnumConstants()) {
			if (condition.check(((Enum) object).name().toUpperCase(), value)) {
				objs.add((Enum) object);
			}
		}
		return objs;
	}

	@SuppressWarnings("rawtypes")
	private Enum getEnumValue(ColumnFilter filter) {
		if (filter == null || filter.getType() == null || filter.getValue() == null) {
			return null;
		}
		Enum obj = null;
		for (Object object : filter.getType().getEnumConstants()) {
			if (((Enum) object).name().equalsIgnoreCase(filter.getValue())) {
				obj = (Enum) object;
				break;
			}
		}
		return obj;
	}

	protected Timestamp buildPeriodValue(String value) {
		if (!StringUtils.isBlank(value) && value.contains(":")) {
			return buildPeriodValue_(value);
		}
		Timestamp periodValue = null;
		Date date = null;
		SimpleDateFormat[] Formats = new SimpleDateFormat[] { new SimpleDateFormat("dd-MM-yyyy"),
				new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"), new SimpleDateFormat("dd/MM/yyyy"),
				new SimpleDateFormat("dd/MM/yyyy hh:mm:ss") };
		int i = Formats.length;
		while (date == null && i > 0) {
			SimpleDateFormat f = Formats[i - 1];
			try {
				date = f.parse(value);
				if (date != null) {
					SimpleDateFormat fd = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
					return new Timestamp(fd.parse(fd.format(date)).getTime());
				}
			} catch (Exception e) {
			}
			i--;
		}

		return periodValue;
	}

	protected Timestamp buildPeriodValue_(String value) {
		Timestamp periodValue = null;
		Date date = null;
		SimpleDateFormat[] Formats = new SimpleDateFormat[] { new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"),
				new SimpleDateFormat("dd/MM/yyyy hh:mm:ss") };
		int i = Formats.length;
		while (date == null && i > 0) {
			SimpleDateFormat f = Formats[i - 1];
			try {
				date = f.parse(value);
				if (date != null) {
					SimpleDateFormat fd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					return new Timestamp(fd.parse(fd.format(date)).getTime());
				}
			} catch (Exception e) {
			}
			i--;
		}

		return periodValue;
	}

	public CriteriaQuery<?> buildQuery() {
		query.where(predicates.toArray(new Predicate[predicates.size()]));
		return query;
	}

	public <T> void select(Class<T> clazz, Selection<?>... selections) {
		query.multiselect(criteriaBuilder.construct(clazz, selections));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <P> Predicate joinById(Long id, String attributeName, String filedName, Class<P> obj) {
		Join<B, P> join = null;
		if (query.getResultType() != Long.class && query.getResultType() != long.class) {
			join = (Join) root.fetch(attributeName);
		} else {
			join = root.join(attributeName);
		}
		return criteriaBuilder.equal(join.get(filedName), id);
	}

	public Predicate isNull(String filedName) {
		return criteriaBuilder.isNull(root.get(filedName));
	}

	public Predicate isNotNull(String filedName) {
		return criteriaBuilder.isNotNull(root.get(filedName));
	}

	public Predicate isEmpty(String filedName) {
		return criteriaBuilder.isEmpty(root.get(filedName));
	}

	public Predicate isNotEmpty(String filedName) {
		return criteriaBuilder.isNotEmpty(root.get(filedName));
	}

	public Predicate isNullOrEmpty(String filedName) {
		Predicate[] predicat = new Predicate[] { isNull(filedName), isEmpty(filedName) };
		return criteriaBuilder.or(predicat);
	}

	public Predicate isNotNullOrEmpty(String filedName) {
		Predicate[] predicat = new Predicate[] { isNotNull(filedName), isNotEmpty(filedName) };
		return criteriaBuilder.or(predicat);
	}

	public Predicate likeCriteria(String criteria, String filedName) {
		return criteriaBuilder.like(criteriaBuilder.lower(root.get(filedName)), criteria.toLowerCase());
	}

	public Predicate lessCriteria(String criteria, String filedName) {
		return criteriaBuilder.lessThan(criteriaBuilder.lower(root.get(filedName)), criteria.toLowerCase());
	}

	public Predicate lessThanOrEqualToCriteria(String criteria, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.lower(root.get(filedName)), criteria.toLowerCase());
	}

	public Predicate lessCriteria(Date date, String filedName) {
		return criteriaBuilder.lessThan(root.get(filedName).as(Date.class), date);
	}

	public Predicate lessCriteria(BigDecimal amount, String filedName) {
		return criteriaBuilder.lessThan(root.get(filedName).as(BigDecimal.class), amount);
	}

	public Predicate lessCriteria(Long value, String filedName) {
		return criteriaBuilder.lessThan(root.get(filedName).as(Long.class), value);
	}

	public Predicate lessCriteria(Integer value, String filedName) {
		return criteriaBuilder.lessThan(root.get(filedName).as(Integer.class), value);
	}

	public Predicate lessCriteria(Timestamp date, String filedName) {
		return criteriaBuilder.lessThan(root.get(filedName).as(Timestamp.class), date);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate lessCriteria(Enum value, String filedName) {
		return criteriaBuilder.lessThan(root.get(filedName).as(Enum.class), value);
	}

	public Predicate lessThanOrEqualToCriteria(Date date, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(root.get(filedName).as(Date.class), date);
	}

	public Predicate lessThanOrEqualToCriteria(BigDecimal amount, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(root.get(filedName).as(BigDecimal.class), amount);
	}

	public Predicate lessThanOrEqualToCriteria(Long value, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(root.get(filedName).as(Long.class), value);
	}

	public Predicate lessThanOrEqualToCriteria(Integer value, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(root.get(filedName).as(Integer.class), value);
	}

	public Predicate lessThanOrEqualToCriteria(Timestamp date, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(root.get(filedName).as(Timestamp.class), date);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate lessThanOrEqualToCriteria(Enum value, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(root.get(filedName).as(Enum.class), value);
	}

	public Predicate greaterCriteria(String criteria, String filedName) {
		return criteriaBuilder.greaterThan(criteriaBuilder.lower(root.get(filedName)), criteria.toLowerCase());
	}

	public Predicate greaterCriteria(Date date, String filedName) {
		return criteriaBuilder.greaterThan(root.get(filedName).as(Date.class), date);
	}

	public Predicate greaterCriteria(BigDecimal amount, String filedName) {
		return criteriaBuilder.greaterThan(root.get(filedName).as(BigDecimal.class), amount);
	}

	public Predicate greaterCriteria(Long value, String filedName) {
		return criteriaBuilder.greaterThan(root.get(filedName).as(Long.class), value);
	}

	public Predicate greaterCriteria(Integer value, String filedName) {
		return criteriaBuilder.greaterThan(root.get(filedName).as(Integer.class), value);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate greaterCriteria(Enum value, String filedName) {
		return criteriaBuilder.greaterThan(root.get(filedName).as(Enum.class), value);
	}

	public Predicate greaterCriteria(Timestamp date, String filedName) {
		return criteriaBuilder.greaterThan(root.get(filedName).as(Timestamp.class), date);
	}

	public Predicate greaterThanOrEqualToCriteria(String criteria, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.lower(root.get(filedName)), criteria.toLowerCase());
	}

	public Predicate greaterThanOrEqualToCriteria(Date date, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(root.get(filedName).as(Date.class), date);
	}

	public Predicate greaterThanOrEqualToCriteria(BigDecimal amount, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(root.get(filedName).as(BigDecimal.class), amount);
	}

	public Predicate greaterThanOrEqualToCriteria(Long value, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(root.get(filedName).as(Long.class), value);
	}

	public Predicate greaterThanOrEqualToCriteria(Integer value, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(root.get(filedName).as(Integer.class), value);
	}

	public Predicate greaterThanOrEqualToCriteria(Timestamp date, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(root.get(filedName).as(Timestamp.class), date);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate greaterThanOrEqualToCriteria(Enum value, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(root.get(filedName).as(Enum.class), value);
	}

	public Predicate notLikeCriteria(String criteria, String filedName) {
		return criteriaBuilder.notLike(criteriaBuilder.lower(root.get(filedName)), criteria.toLowerCase());
	}

	public Predicate notEqualCriteria(Object criteria, String filedName) {
		return criteriaBuilder.notEqual(root.get(filedName), criteria);
	}

	public Predicate EqualsCriteria(Long criteria, String filedName) {
		return criteriaBuilder.equal(root.get(filedName), criteria);
	}

	private Predicate Equals(Object value, String filedName) {
		return criteriaBuilder.equal(root.get(filedName), value);
	}

	private Predicate Equals(Date value, String filedName) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(value);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date date1 = calendar.getTime();
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.MILLISECOND, 59);
		Date date2 = calendar.getTime();
		return criteriaBuilder.between(root.get(filedName).as(Date.class), date1, date2);
	}
//	
//	private Predicate notEqualCriteria(Date value, String filedName) {
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(value);
//		calendar.set(Calendar.HOUR, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.MILLISECOND, 0);		
//		Date date1 = calendar.getTime();
//		calendar.set(Calendar.HOUR, 23);
//		calendar.set(Calendar.MINUTE, 59);
//		calendar.set(Calendar.MILLISECOND, 59);
//		Date date2 = calendar.getTime();
//		return criteriaBuilder.between(root.get(filedName).as(Date.class), date1, date2);
//	}

	public void addEqualsCriteria(String filedName, Long criteria) {
		add(EqualsCriteria(criteria, filedName));
	}

	public void addEquals(String filedName, Object value) {
		add(Equals(value, filedName));
	}

	public void addLikeCriteria(String filedName, String criteria) {
		add(likeCriteria(addWildCards(criteria), filedName));
	}

	public void addIsNullCriteria(String filedName) {
		add(isNull(filedName));
	}

	public void addIsNotNullCriteria(String filedName, String criteria) {
		add(isNotNull(filedName));
	}

	public <P> void addJoinById(String filedName, String attributeName, Long id, Class<P> obj) {
		if (id != null) {
			add(joinById(id, attributeName, filedName, obj));
		}
	}

	private Predicate endsWithCriteria(String value, String name) {
		return likeCriteria("%".concat(value), name);
	}

	private Predicate startWithCriteria(String value, String name) {
		return likeCriteria(value + "%", name);
	}

	public String addWildCards(String param) {
		return '%' + param + '%';
	}

	public Predicate[] getPredicates() {
		Predicate[] predicatesArray = new Predicate[predicates.size()];
		return predicates.toArray(predicatesArray);
	}

	public Predicate build() {
		return criteriaBuilder.and(getPredicates());
	}

}
