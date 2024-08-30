package com.moriset.bcephal.planification.domain.script;

import com.moriset.bcephal.domain.MainObject;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Script")
@Table(name = "BCP_SCRIPT")
public class Script extends MainObject {

	private static final long serialVersionUID = -4053391760000710396L;

	private String script;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "script_seq")
	@SequenceGenerator(name = "script_seq", sequenceName = "script_seq", initialValue = 1,  allocationSize = 1)
	private Long id;

	@Override
	public Script copy() {
		Script copy = new Script();
		copy.setName(this.getName() + System.currentTimeMillis());
		copy.setDescription(getDescription());
		copy.setGroup(this.getGroup());
		copy.setVisibleInShortcut(isVisibleInShortcut());
		copy.setScript(getScript());
		return copy;
	}
	
}
