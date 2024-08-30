/**
 * 
 */
package com.moriset.bcephal.domain.parameter;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.utils.JsonDateTimeDeserializer;
import com.moriset.bcephal.utils.JsonDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "IncrementalNumber")
@Table(name = "BCP_INCREMENTAL_NUMBER")
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class IncrementalNumber extends Persistent {

	private static final long serialVersionUID = -4471565603808042988L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "incremental_number_seq")
	@SequenceGenerator(name = "incremental_number_seq", sequenceName = "incremental_number_seq", initialValue = 1, allocationSize = 1)
	private Long id;

	private String name;

	private boolean visibleInShortcut;

	/**
	 * <p style="margin-top: 0">
	 * Creation date time.
	 * </p>
	 */
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp creationDate;

	/**
	 * <p style="margin-top: 0">
	 * Last modification date time
	 * </p>
	 */
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	@JsonDeserialize(using = JsonDateTimeDeserializer.class)
	@Column(nullable = false)
	private Timestamp modificationDate;

	/**
	 * Starting value from where the sequence starts. Initial value should be
	 * greater than or equal to minimum value and less than equal to maximum value.
	 */
	private long initialValue;

	/**
	 * Value by which sequence will increment itself. Increment value can be
	 * positive or negative.
	 */
	private long incrementValue;

	/**
	 * Minimum value of the sequence.
	 */
	private long minimumValue;

	/**
	 * Maximum value of the sequence.
	 */
	private long maximumValue;

	/**
	 * Current value of the sequence.
	 */
	private long currentValue;

	/**
	 * If TRUE : When sequence reaches its set_limit it starts from beginning. IF
	 * FALSE : An exception will be thrown if sequence exceeds its max_value.
	 */
	private boolean cycle;

	/**
	 * Sequence size.
	 */
	private int size;

	/**
	 * Sequence prefix.
	 */
	private String prefix;

	/**
	 * Sequence suffix.
	 */
	private String suffix;

	public IncrementalNumber() {
		creationDate = new Timestamp(System.currentTimeMillis());
		modificationDate = new Timestamp(System.currentTimeMillis());
		visibleInShortcut = true;
		this.initialValue = 1;
		this.incrementValue = 1;
		this.minimumValue = 0;
		this.maximumValue = Long.MAX_VALUE;
		this.currentValue = 0;
		this.size = 15;
		this.cycle = false;
		this.visibleInShortcut = true;
	}

	@JsonIgnore
	public String buildNextValue() {
		this.currentValue++;
		String n = String.format("%0" + size + "d", this.currentValue);
		String p = buildVariable(this.prefix);
		String s = buildVariable(this.suffix);
		return String.format("%s%s%s", p, n, s);
	}

	private String buildVariable(String parameter) {
		if(!StringUtils.hasText(parameter)) {
			return "";
		}
		String value = parameter;
		IncrementalNumberVariables variables = new IncrementalNumberVariables();
		
		value = value.replace(variables.YEAR, "" + Calendar.getInstance().get(Calendar.YEAR));
		value = value.replace(variables.MONTH, "" + Calendar.getInstance().get(Calendar.MONTH));
		value = value.replace(variables.DAY_OF_MONTH, "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		return value;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}

}
