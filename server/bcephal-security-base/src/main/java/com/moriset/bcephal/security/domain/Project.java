package com.moriset.bcephal.security.domain;

import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author Georges WAKEU
 *
 */
@Data
@Entity(name = "SecurityProject_")
@Table(name = "BCP_SEC_PROJECT")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
public class Project extends Persistent{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3501437740834110587L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_project_seq")
	@SequenceGenerator(name = "sec_project_seq", sequenceName = "sec_project_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	private String code;
	private String name;
	private String description;
	private String path;
	private String dbname;
	private String username;
	
	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}
}
