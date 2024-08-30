/**
 * 
 */
package com.moriset.bcephal.security.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moriset.bcephal.domain.Persistent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Moriset
 *
 */
@Entity(name = "ClientFunctionality")
@Table(name = "BCP_SEC_CLIENT_FUNCTIONALITY")
@Data 
@EqualsAndHashCode(callSuper=false)
public class ClientFunctionality extends Persistent {

	private static final long serialVersionUID = 3427233209627779197L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_functionality_seq")
	@SequenceGenerator(name = "client_functionality_seq", sequenceName = "client_functionality_seq", initialValue = 1,  allocationSize = 1)
	private Long id;
	
	@JsonIgnore
	private Long clientId;
	
	@JsonIgnore
	private String client;
	
	private String code;
	
    private boolean active;
    
    private int position;
    
    @Transient
    private List<RightLevel> actions;
    
    @Transient
    private List<RightLevel> lowLevelActions;
    
    @JsonIgnore
    private String actionsAsString;
    
    @Transient
    private String Name;
    
    public ClientFunctionality()
    {
        actions = new ArrayList<RightLevel>();
    }
    
    public ClientFunctionality(String code, int position, boolean active) 
    {
        this();
        this.code = code;
        this.position = position;
        this.active = active;
    }
    
    public ClientFunctionality(String code, int position, boolean active, List<RightLevel> actions, List<RightLevel> lowLevelActions) 
    {
        this();
        this.code = code;
        this.position = position;
        this.active = active;
        this.actions = actions;
        this.lowLevelActions = lowLevelActions;
    }


    @PrePersist @PreUpdate
    public void buildActionAsString() {
    	this.actionsAsString = "";
    	actions.forEach(action -> {
    		String separator = StringUtils.hasText(this.actionsAsString) ? ";" : "";
    		this.actionsAsString += separator + action.name();
    	});
    	lowLevelActions.forEach(action -> {
    		if(!actions.contains(action)) {
	    		String separator = StringUtils.hasText(this.actionsAsString) ? ";" : "";
	    		this.actionsAsString += separator + action.name();
    		}
    	});
    }
    
    @PostLoad
    public void buildActionFromString() {
    	this.actions = new ArrayList<RightLevel>();
    	this.lowLevelActions = new ArrayList<RightLevel>();
    	if(StringUtils.hasText(this.actionsAsString)) {
    		String separator =";";
    		String[] names = this.actionsAsString.split(separator);
    		for(String name : names) {
    			if(StringUtils.hasText(name)) {
    				RightLevel level = RightLevel.valueOf(name);
    				this.actions.add(level);
    				this.lowLevelActions.add(level);
    			}
    		}
    	}
    }
        
	@Override
	public Persistent copy() {
		return null;
	}
	
}
