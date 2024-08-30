package com.moriset.bcephal.license.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.license.domain.ColumnFilter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import lombok.Data;
/**
 * 
 * @author MORISET-004
 *
 * @param <B>
 */

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

	public void addNoTInObjectId(List<Long> hidedObjectIds) {
		if (hidedObjectIds != null && hidedObjectIds.size() > 0) {
			Predicate predicate = root.get("id").as(Long.class).in(hidedObjectIds).not();
			predicates.add(predicate);
		}
	}

	private Predicate buildPredicateFilter(ColumnFilter filter) {
//		if (filter != null) {
//			boolean isPeriod = filter.getDimensionType() != null && filter.getDimensionType().isPeriod();
//			boolean isDate = filter.getType() != null && filter.getType().equals(Date.class);
//			boolean isDateValid = isPeriod || isDate;
//			GridFilterOperator gridFilterOperator = new GridFilterOperator();
//			if (gridFilterOperator.isEquals(filter.getOperation())) {
//				if (!filter.isJoin()) {
//					if (isDateValid) {
//						return Equals(buildPeriodValue(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && filter.getType().equals(Boolean.class)) {
//						return Equals(Boolean.valueOf(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return Equals(getEnumValue(filter), filter.getName());
//					}
//					return Equals(filter.getValue(), filter.getName());
//				} else {
//					if (isDateValid) {
//						return Equals(filter.getJoinName(), buildPeriodValue(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && filter.getType().equals(Boolean.class)) {
//						return Equals(filter.getJoinName(), Boolean.valueOf(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return Equals(filter.getJoinName(), getEnumValue(filter), filter.getName());
//					}
//					return Equals(filter.getJoinName(), filter.getValue(), filter.getName());
//				}
//			} else if (gridFilterOperator.isGreater(filter.getOperation())) {
//				if (!filter.isJoin()) {
//					if (isDateValid) {
//						return greaterCriteria(buildPeriodValue(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return greaterCriteria(getEnumValue(filter), filter.getName());
//					} else if (filter.getType() != null && BigDecimal.class.equals(filter.getType())) {
//						return greaterCriteria(new BigDecimal(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Long.class.equals(filter.getType())) {
//						return greaterCriteria(Long.valueOf(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Integer.class.equals(filter.getType())) {
//						return greaterCriteria(Integer.valueOf(filter.getValue()), filter.getName());
//					}
//					return greaterCriteria(filter.getValue(), filter.getName());
//				} else {
//					if (isDateValid) {
//						return greaterCriteria(filter.getJoinName(), buildPeriodValue(filter.getValue()),
//								filter.getName());
//					} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return greaterCriteria(filter.getJoinName(), getEnumValue(filter), filter.getName());
//					} else if (filter.getType() != null && BigDecimal.class.equals(filter.getType())) {
//						return greaterCriteria(filter.getJoinName(), new BigDecimal(filter.getValue()),
//								filter.getName());
//					} else if (filter.getType() != null && Long.class.equals(filter.getType())) {
//						return greaterCriteria(filter.getJoinName(), Long.valueOf(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Integer.class.equals(filter.getType())) {
//						return greaterCriteria(filter.getJoinName(), Integer.valueOf(filter.getValue()),
//								filter.getName());
//					}
//					return greaterCriteria(filter.getJoinName(), filter.getValue(), filter.getName());
//				}
//			} else if (gridFilterOperator.isGreaterOrEquals(filter.getOperation())) {
//				if (!filter.isJoin()) {
//					if (isDateValid) {
//						return greaterThanOrEqualToCriteria(buildPeriodValue(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return greaterThanOrEqualToCriteria(getEnumValue(filter), filter.getName());
//					} else if (filter.getType() != null && BigDecimal.class.equals(filter.getType())) {
//						return greaterThanOrEqualToCriteria(new BigDecimal(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Long.class.equals(filter.getType())) {
//						return greaterThanOrEqualToCriteria(Long.valueOf(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Integer.class.equals(filter.getType())) {
//						return greaterThanOrEqualToCriteria(Integer.valueOf(filter.getValue()), filter.getName());
//					}
//					return greaterThanOrEqualToCriteria(filter.getValue(), filter.getName());
//				} else {
//					if (isDateValid) {
//						return greaterThanOrEqualToCriteria(filter.getJoinName(), buildPeriodValue(filter.getValue()),
//								filter.getName());
//					} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return greaterThanOrEqualToCriteria(filter.getJoinName(), getEnumValue(filter),
//								filter.getName());
//					} else if (filter.getType() != null && BigDecimal.class.equals(filter.getType())) {
//						return greaterThanOrEqualToCriteria(filter.getJoinName(), new BigDecimal(filter.getValue()),
//								filter.getName());
//					} else if (filter.getType() != null && Long.class.equals(filter.getType())) {
//						return greaterThanOrEqualToCriteria(filter.getJoinName(), Long.valueOf(filter.getValue()),
//								filter.getName());
//					} else if (filter.getType() != null && Integer.class.equals(filter.getType())) {
//						return greaterThanOrEqualToCriteria(filter.getJoinName(), Integer.valueOf(filter.getValue()),
//								filter.getName());
//					}
//					return greaterThanOrEqualToCriteria(filter.getJoinName(), filter.getValue(), filter.getName());
//				}
//			} else if (gridFilterOperator.isLess(filter.getOperation())) {
//				if (!filter.isJoin()) {
//					if (isDateValid) {
//						return lessCriteria(buildPeriodValue(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return lessCriteria(getEnumValue(filter), filter.getName());
//					} else if (filter.getType() != null && BigDecimal.class.equals(filter.getType())) {
//						return lessCriteria(new BigDecimal(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Long.class.equals(filter.getType())) {
//						return lessCriteria(Long.valueOf(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Integer.class.equals(filter.getType())) {
//						return lessCriteria(Integer.valueOf(filter.getValue()), filter.getName());
//					}
//					return lessCriteria(filter.getValue(), filter.getName());
//				} else {
//					if (isDateValid) {
//						return lessCriteria(filter.getJoinName(), buildPeriodValue(filter.getValue()),
//								filter.getName());
//					} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return lessCriteria(filter.getJoinName(), getEnumValue(filter), filter.getName());
//					} else if (filter.getType() != null && BigDecimal.class.equals(filter.getType())) {
//						return lessCriteria(filter.getJoinName(), new BigDecimal(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Long.class.equals(filter.getType())) {
//						return lessCriteria(filter.getJoinName(), Long.valueOf(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Integer.class.equals(filter.getType())) {
//						return lessCriteria(filter.getJoinName(), Integer.valueOf(filter.getValue()), filter.getName());
//					}
//					return lessCriteria(filter.getJoinName(), filter.getValue(), filter.getName());
//
//				}
//			} else if (gridFilterOperator.isLessOrEquals(filter.getOperation())) {
//				if (!filter.isJoin()) {
//					if (isDateValid) {
//						return lessThanOrEqualToCriteria(buildPeriodValue(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return lessThanOrEqualToCriteria(getEnumValue(filter), filter.getName());
//					} else if (filter.getType() != null && BigDecimal.class.equals(filter.getType())) {
//						return lessThanOrEqualToCriteria(new BigDecimal(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Long.class.equals(filter.getType())) {
//						return lessThanOrEqualToCriteria(Long.valueOf(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Integer.class.equals(filter.getType())) {
//						return lessThanOrEqualToCriteria(Integer.valueOf(filter.getValue()), filter.getName());
//					}
//					return lessThanOrEqualToCriteria(filter.getValue(), filter.getName());
//				} else {
//					if (isDateValid) {
//						return lessThanOrEqualToCriteria(filter.getJoinName(), buildPeriodValue(filter.getValue()),
//								filter.getName());
//					} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return lessThanOrEqualToCriteria(filter.getJoinName(), getEnumValue(filter), filter.getName());
//					} else if (filter.getType() != null && BigDecimal.class.equals(filter.getType())) {
//						return lessThanOrEqualToCriteria(filter.getJoinName(), new BigDecimal(filter.getValue()),
//								filter.getName());
//					} else if (filter.getType() != null && Long.class.equals(filter.getType())) {
//						return lessThanOrEqualToCriteria(filter.getJoinName(), Long.valueOf(filter.getValue()),
//								filter.getName());
//					} else if (filter.getType() != null && Integer.class.equals(filter.getType())) {
//						return lessThanOrEqualToCriteria(filter.getJoinName(), Integer.valueOf(filter.getValue()),
//								filter.getName());
//					}
//					return lessThanOrEqualToCriteria(filter.getJoinName(), filter.getValue(), filter.getName());
//				}
//			} else if (gridFilterOperator.isNotEquals(filter.getOperation())) {
//				if (!filter.isJoin()) {
//					if (isDateValid) {
//						return notEqualCriteria(buildPeriodValue(filter.getValue()), filter.getName());
//					} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return notEqualCriteria(getEnumValue(filter), filter.getName());
//					}
//					return notEqualCriteria(filter.getValue(), filter.getName());
//				} else {
//					if (isDateValid) {
//						return notEqualCriteria(filter.getJoinName(), buildPeriodValue(filter.getValue()),
//								filter.getName());
//					} else if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return notEqualCriteria(filter.getJoinName(), getEnumValue(filter), filter.getName());
//					}
//					return notEqualCriteria(filter.getJoinName(), filter.getValue(), filter.getName());
//				}
//			} else if (gridFilterOperator.isStartsWith(filter.getOperation())) {
//				if (!filter.isJoin()) {
//					if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return buildEnumCriteria(getStartWithEnumValues(filter), filter.getName());
//					}
//					return startWithCriteria(filter.getValue(), filter.getName());
//				} else {
//					if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return buildEnumCriteria(filter.getJoinName(), getStartWithEnumValues(filter),
//								filter.getName());
//					}
//					return startWithCriteria(filter.getJoinName(), filter.getValue(), filter.getName());
//				}
//			} else if (gridFilterOperator.isEndsWith(filter.getOperation())) {
//				if (!filter.isJoin()) {
//					if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return buildEnumCriteria(getEndsWithEnumValues(filter), filter.getName());
//					}
//					return endsWithCriteria(filter.getValue(), filter.getName());
//				} else {
//					if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return buildEnumCriteria(filter.getJoinName(), getEndsWithEnumValues(filter), filter.getName());
//					}
//					return endsWithCriteria(filter.getJoinName(), filter.getValue(), filter.getName());
//				}
//			} else if (gridFilterOperator.isContains(filter.getOperation())) {
//				if (!filter.isJoin()) {
//					if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return buildEnumCriteria(getLikeEnumValues(filter), filter.getName());
//					}
//					return likeCriteria(addWildCards(filter.getValue()), filter.getName());
//				} else {
//					if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return buildEnumCriteria(filter.getJoinName(), getLikeEnumValues(filter), filter.getName());
//					}
//					return likeCriteria(filter.getJoinName(), addWildCards(filter.getValue()), filter.getName());
//				}
//			} else if (gridFilterOperator.isNotContains(filter.getOperation())) {
//				if (!filter.isJoin()) {
//					if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return buildEnumCriteria(getNotLikeEnumValues(filter), filter.getName());
//					}
//					return notLikeCriteria(addWildCards(filter.getValue()), filter.getName());
//				} else {
//					if (filter.getType() != null && Enum.class.isAssignableFrom(filter.getType())) {
//						return buildEnumCriteria(filter.getJoinName(), getNotLikeEnumValues(filter), filter.getName());
//					}
//					return notLikeCriteria(filter.getJoinName(), addWildCards(filter.getValue()), filter.getName());
//				}
//			} else if (gridFilterOperator.isNotNullOrEmpty(filter.getOperation())) {
//				if (!filter.isJoin()) {
//					return isNotNull(filter.getName());
//				} else {
//					return isNotNull(filter.getJoinName(), filter.getName());
//				}
//			} else if (gridFilterOperator.isNullOrEmpty(filter.getOperation())) {
//				if (!filter.isJoin()) {
//					return isNull(filter.getName());
//				} else {
//					return isNull(filter.getJoinName(), filter.getName());
//				}
//			}
//		}
		return null;
	}

	protected Timestamp buildPeriodValue(String value) {
		if (StringUtils.hasText(value) && value.contains(":")) {
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

	public Predicate Equals(Object value, String filedName) {
		return criteriaBuilder.equal(root.get(filedName), value);
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	Expression getJoinAlias(String joinAttributeName,String filedName) {
		return (Expression) root.join(joinAttributeName).get(filedName).alias(joinAttributeName + "_" + filedName);
	}
	
	@SuppressWarnings({ "rawtypes" })
	Expression getJoinAliasAsDate(String joinAttributeName,String filedName) {
		return (Expression) root.join(joinAttributeName).get(filedName).as(Date.class).alias(joinAttributeName + "_" + filedName);
	}
	
	@SuppressWarnings({ "rawtypes" })
	Expression getJoinAliasAsBigDecimal(String joinAttributeName,String filedName) {
		return (Expression) root.join(joinAttributeName).get(filedName).as(BigDecimal.class).alias(joinAttributeName + "_" + filedName);
	}
	
	@SuppressWarnings({ "rawtypes" })
	Expression getJoinAliasAsLong(String joinAttributeName,String filedName) {
		return (Expression) root.join(joinAttributeName).get(filedName).as(Long.class).alias(joinAttributeName + "_" + filedName);
	}
	
	@SuppressWarnings({ "rawtypes" })
	Expression getJoinAliasAsInteger(String joinAttributeName,String filedName) {
		return (Expression) root.join(joinAttributeName).get(filedName).as(Integer.class).alias(joinAttributeName + "_" + filedName);
	}
	
	@SuppressWarnings({ "rawtypes" })
	Expression getJoinAliasAsTimestamp(String joinAttributeName,String filedName) {
		return (Expression) root.join(joinAttributeName).get(filedName).as(Timestamp.class).alias(joinAttributeName + "_" + filedName);
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	Expression getJoinAliasAsEnum(String joinAttributeName,String filedName) {
		return (Expression) root.join(joinAttributeName).get(filedName).as(Enum.class).alias(joinAttributeName + "_" + filedName);
	}
	
	
	public Predicate Equals(String joinAttributeName, Object value, String filedName) {
		return criteriaBuilder.equal(getJoinAlias(joinAttributeName, filedName), value);
	}

	public Predicate isNull(String joinAttributeName, String filedName) {
		return criteriaBuilder.isNull(getJoinAlias(joinAttributeName, filedName));
	}

	public Predicate isNotNull(String joinAttributeName, String filedName) {
		return criteriaBuilder.isNotNull(getJoinAlias(joinAttributeName, filedName));
	}

	@SuppressWarnings({ "unchecked" })
	public Predicate isEmpty(String joinAttributeName, String filedName) {
		return criteriaBuilder.isEmpty(getJoinAlias(joinAttributeName, filedName));
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate isNotEmpty(String joinAttributeName, String filedName) {
		return criteriaBuilder.isNotEmpty(getJoinAlias(joinAttributeName, filedName));
	}

	public Predicate isNullOrEmpty(String joinAttributeName, String filedName) {
		Predicate[] predicat = new Predicate[] { isNull(joinAttributeName, filedName),
				isEmpty(joinAttributeName, filedName) };
		return criteriaBuilder.or(predicat);
	}

	public Predicate isNotNullOrEmpty(String joinAttributeName, String filedName) {
		Predicate[] predicat = new Predicate[] { isNotNull(joinAttributeName, filedName),
				isNotEmpty(joinAttributeName, filedName) };
		return criteriaBuilder.or(predicat);
	}

	@SuppressWarnings({ "unchecked" })
	public Predicate likeCriteria(String joinAttributeName, String criteria, String filedName) {
		return criteriaBuilder.like(criteriaBuilder.lower(getJoinAlias(joinAttributeName, filedName)),
				criteria.toLowerCase());
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate lessCriteria(String joinAttributeName, String criteria, String filedName) {
		return criteriaBuilder.lessThan(criteriaBuilder.lower(getJoinAlias(joinAttributeName, filedName)),
				criteria.toLowerCase());
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate lessThanOrEqualToCriteria(String joinAttributeName, String criteria, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.lower(getJoinAlias(joinAttributeName, filedName)),
				criteria.toLowerCase());
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate lessCriteria(String joinAttributeName, Date date, String filedName) {
		return criteriaBuilder.lessThan(getJoinAliasAsDate(joinAttributeName, filedName), date);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate lessCriteria(String joinAttributeName, BigDecimal amount, String filedName) {
		return criteriaBuilder.lessThan(getJoinAliasAsBigDecimal(joinAttributeName, filedName), amount);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate lessCriteria(String joinAttributeName, Long value, String filedName) {
		return criteriaBuilder.lessThan(getJoinAliasAsLong(joinAttributeName, filedName), value);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate lessCriteria(String joinAttributeName, Integer value, String filedName) {
		return criteriaBuilder.lessThan(getJoinAliasAsInteger(joinAttributeName, filedName), value);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate lessCriteria(String joinAttributeName, Timestamp date, String filedName) {
		return criteriaBuilder.lessThan(getJoinAliasAsTimestamp(joinAttributeName, filedName), date);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate lessCriteria(String joinAttributeName, Enum value, String filedName) {
		return criteriaBuilder.lessThan(getJoinAliasAsEnum(joinAttributeName, filedName), value);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate lessThanOrEqualToCriteria(String joinAttributeName, Date date, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(getJoinAliasAsDate(joinAttributeName, filedName), date);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate lessThanOrEqualToCriteria(String joinAttributeName, BigDecimal amount, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(getJoinAliasAsBigDecimal(joinAttributeName, filedName),
				amount);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate lessThanOrEqualToCriteria(String joinAttributeName, Long value, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(getJoinAliasAsLong(joinAttributeName, filedName), value);
	}
	
	@SuppressWarnings({ "unchecked"})
	public Predicate lessThanOrEqualToCriteria(String joinAttributeName, Integer value, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(getJoinAliasAsInteger(joinAttributeName, filedName), value);
	}
	
	@SuppressWarnings({ "unchecked"})
	public Predicate lessThanOrEqualToCriteria(String joinAttributeName, Timestamp date, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(getJoinAliasAsTimestamp(joinAttributeName, filedName), date);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate lessThanOrEqualToCriteria(String joinAttributeName, Enum value, String filedName) {
		return criteriaBuilder.lessThanOrEqualTo(getJoinAliasAsEnum(joinAttributeName, filedName), value);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate greaterCriteria(String joinAttributeName, String criteria, String filedName) {
		return criteriaBuilder.greaterThan(criteriaBuilder.lower(getJoinAlias(joinAttributeName, filedName)),
				criteria.toLowerCase());
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate greaterCriteria(String joinAttributeName, Date date, String filedName) {
		return criteriaBuilder.greaterThan(getJoinAliasAsDate(joinAttributeName, filedName), date);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate greaterCriteria(String joinAttributeName, BigDecimal amount, String filedName) {
		return criteriaBuilder.greaterThan(getJoinAliasAsBigDecimal(joinAttributeName, filedName), amount);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate greaterCriteria(String joinAttributeName, Long value, String filedName) {
		return criteriaBuilder.greaterThan(getJoinAliasAsLong(joinAttributeName, filedName), value);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate greaterCriteria(String joinAttributeName, Integer value, String filedName) {
		return criteriaBuilder.greaterThan(getJoinAliasAsInteger(joinAttributeName, filedName), value);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate greaterCriteria(String joinAttributeName, Enum value, String filedName) {
		return criteriaBuilder.greaterThan(getJoinAliasAsEnum(joinAttributeName, filedName), value);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate greaterCriteria(String joinAttributeName, Timestamp date, String filedName) {
		return criteriaBuilder.greaterThan(getJoinAliasAsTimestamp(joinAttributeName, filedName), date);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate greaterThanOrEqualToCriteria(String joinAttributeName, String criteria, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.lower(getJoinAlias(joinAttributeName, filedName)),
				criteria.toLowerCase());
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate greaterThanOrEqualToCriteria(String joinAttributeName, Date date, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(getJoinAliasAsDate(joinAttributeName, filedName), date);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate greaterThanOrEqualToCriteria(String joinAttributeName, BigDecimal amount, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(getJoinAliasAsBigDecimal(joinAttributeName, filedName),
				amount);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate greaterThanOrEqualToCriteria(String joinAttributeName, Long value, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(getJoinAliasAsLong(joinAttributeName, filedName), value);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate greaterThanOrEqualToCriteria(String joinAttributeName, Integer value, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(getJoinAliasAsInteger(joinAttributeName, filedName),
				value);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate greaterThanOrEqualToCriteria(String joinAttributeName, Timestamp date, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(getJoinAliasAsTimestamp(joinAttributeName, filedName),
				date);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate greaterThanOrEqualToCriteria(String joinAttributeName, Enum value, String filedName) {
		return criteriaBuilder.greaterThanOrEqualTo(getJoinAliasAsEnum(joinAttributeName, filedName), value);
	}

	@SuppressWarnings({ "unchecked"})
	public Predicate notLikeCriteria(String joinAttributeName, String criteria, String filedName) {
		return criteriaBuilder.notLike(criteriaBuilder.lower(getJoinAlias(joinAttributeName, filedName)),
				criteria.toLowerCase());
	}

	public Predicate notEqualCriteria(String joinAttributeName, Object criteria, String filedName) {
		return criteriaBuilder.notEqual(getJoinAlias(joinAttributeName, filedName), criteria);
	}


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

	public Predicate endsWithCriteria(String value, String name) {
		return likeCriteria("%".concat(value), name);
	}

	public Predicate startWithCriteria(String value, String name) {
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
