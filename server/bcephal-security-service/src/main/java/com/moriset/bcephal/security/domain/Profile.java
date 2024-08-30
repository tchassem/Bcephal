/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Joseph Wambo
 *
 */
@Entity(name = "Profil")
@Table(name = "BCP_SEC_PROFIL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
public class Profile extends MainObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3745014979225291478L;
	
	private static final String SHORT_CUT_SEPARATOR = ";";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profil_seq")
	@SequenceGenerator(name = "profil_seq", sequenceName = "profil_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	private String code;
	
	private String description;
	
	@JsonIgnore
	private String client;
	
	@JsonIgnore
	private Long clientId;
	
	@Column(name="type_")
	@Enumerated(EnumType.STRING) 
	@NotNull(message = "{profil.type.validation.null.message}") 
	private ProfileType type;
	
	@Transient 
	private ListChangeHandler<Nameable> userListChangeHandler = new ListChangeHandler<>();
	
	@Transient 
	private ListChangeHandler<Right> rightListChangeHandler = new ListChangeHandler<>();
	
	@Transient
	private List<String> shortcuts = new ArrayList<>();
	@JsonIgnore
	private String shortcutsImpl;
		

	@Override
	public Profile copy() {
		Profile copy = new Profile();
		copy.setCode(getCode());
		copy.setDescription(getDescription());
		copy.setClient(getClient());
		copy.setClientId(getClientId());
		copy.setType(getType());
		for(Nameable item : getUserListChangeHandler().getItems()) {
			copy.getUserListChangeHandler().addNew(item);
		}
		for(Right item : getRightListChangeHandler().getItems()) {
			copy.getRightListChangeHandler().addNew(item);
		}
		copy.setShortcuts(getShortcuts());
		copy.setShortcutsImpl(getShortcutsImpl());
		return copy;
	}

	@JsonIgnore
	public GroupRepresentation getGroupRepresentation() {
		GroupRepresentation representation = new GroupRepresentation();
		representation.setName(getName());
		representation.setId(getCode());
		return representation;
	}
	
	@JsonIgnore
	public GroupRepresentation updateGroupRepresentation(GroupRepresentation representation) {
		representation.setName(getName());
		return representation;
	}
	
	@PostLoad
	public void initListChangeHandler() {
		this.shortcuts = new ArrayList<>();
		if(StringUtils.hasText(this.shortcutsImpl)) {
			String[] vals = this.shortcutsImpl.split(SHORT_CUT_SEPARATOR);
			for(String val : vals) {
				this.shortcuts.add(val);
			}
			this.shortcuts.sort(new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
		}
	}
	
	
	public void initShortCutImpl() {
		for(String s : new ArrayList<>(this.shortcuts)) {
			if(!StringUtils.hasText(s)) {
				this.shortcuts.remove(s);
			}
		}
		this.shortcutsImpl = String.join(SHORT_CUT_SEPARATOR, this.shortcuts);
	}	
	
}
