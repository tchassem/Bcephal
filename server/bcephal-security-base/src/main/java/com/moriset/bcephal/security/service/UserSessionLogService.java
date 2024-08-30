/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.domain.filters.GridFilterOperator;
import com.moriset.bcephal.security.domain.ClientBase;
import com.moriset.bcephal.security.domain.ProfileData;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.domain.RightLevel;
import com.moriset.bcephal.security.domain.UserData;
import com.moriset.bcephal.security.domain.UserSessionLog;
import com.moriset.bcephal.security.domain.UserSessionLogBrowserData;
import com.moriset.bcephal.security.repository.ClientDataRepository;
import com.moriset.bcephal.security.repository.ProfileDataRepository;
import com.moriset.bcephal.security.repository.ProjectRepository;
import com.moriset.bcephal.security.repository.UserDataRepository;
import com.moriset.bcephal.security.repository.UserSessionLogRepository;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class UserSessionLogService extends MainObjectService<UserSessionLog, UserSessionLogBrowserData>  {
	
	@Autowired
	UserSessionLogRepository repository;
	
	@Autowired
	UserDataRepository userRepository;
	
	@Autowired
	ClientDataRepository clientRepository;
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	ProfileDataRepository proFileDataRepository;
		
	@PersistenceContext
	EntityManager session;
	
	@Override
	public UserSessionLogRepository getRepository() {
		return repository;
	}
	
	
	@Override
	protected UserSessionLogBrowserData getNewBrowserData(UserSessionLog log) {
		return new UserSessionLogBrowserData(log);
	}
	
	public boolean disconnectedUser(List<Long> ids, Locale locale) {
		log.debug("Try to  disconnect users : {} messages", ids.size());
		try {
			if (ids == null || ids.size() == 0) {
				String message = messageSource.getMessage("unable.to.disconnected.empty.list", new Object[] { ids }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			List<UserSessionLog> usersSL = repository.findAllById(ids);
			usersSL.forEach(userS -> userS.setStatus("DISCONNECTED"));
			repository.saveAll(usersSL);
			log.debug("{} users successfully disconnected ", ids.size());
			return true;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while disconnect users messages : {}", ids.size(), e);
			String message = messageSource.getMessage("unable.to.disconnect", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	protected Specification<UserSessionLog> getBrowserDatasSpecification(BrowserDataFilter filter, Long clientId, Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<UserSessionLog> qBuilder = new RequestQueryBuilder<UserSessionLog>(root, query, cb);
			qBuilder.select(BrowserData.class, root.get("id"), root.get("name"), root.get("creationDate"), root.get("modificationDate"));
			if (clientId != null) {
				qBuilder.addEquals("clientId", clientId);
			}
			if (StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addEquals("functionality", filter.getCriteria());
			}
			
			if(filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
		    	filter.getColumnFilters().getItems().forEach(filte ->{
		    		build(filte);
		    	});
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}
	
	
	protected void build(ColumnFilter columnFilter) {
		if ("userId".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("userId");
			columnFilter.setType(String.class);
		} else if ("username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		} else if ("lastoperationDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("lastoperationDate");
			columnFilter.setType(Date.class);
		} else if ("endDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("endDate");
			columnFilter.setType(Date.class);
		} else if ("username".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("username");
			columnFilter.setType(String.class);
		} else if ("status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(String.class);
		} else if ("rightLevel".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("rightLevel");
			columnFilter.setType(RightLevel.class);
		} else if ("projectCode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("projectCode");
			columnFilter.setType(String.class);
		} else if ("clientName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("clientName");
			columnFilter.setType(String.class);
		} 
		else if ("functionality".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("functionality");
			columnFilter.setType(String.class);
		} 
		else if ("projectName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("projectName");
			columnFilter.setType(String.class);
		} 
		else if ("profile".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("profile");
			columnFilter.setType(String.class);
		} 
		else if ("userType".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("userType");
			columnFilter.setType(String.class);
		} 
		super.build(columnFilter);
	}


	@Override
	protected UserSessionLog getNewItem() {
		return new UserSessionLog();
	}
	
	public UserSessionLog getUserForSessionLog(UserSessionLog userSessionLog, Locale locale) {
		log.debug("Try to  save userSessionLog : {} messages", userSessionLog);
		try {
			if (userSessionLog == null) {
				String message = messageSource.getMessage("unable.to.save.null.object", new Object[] { userSessionLog }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			UserData user = userRepository.findByUsername(userSessionLog.getUsername()).get();
			UserSessionLog userS = new UserSessionLog();
			userS.setUserId(user.getId());
			userS.setUsersession(userSessionLog.getUsersession());
			userS.setUsername(userSessionLog.getUsername());
			userS.setStatus("CONNECTED");
			repository.save(userS);
			log.debug("{} userSessionLog successfully saved ", userSessionLog);
			return userSessionLog;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save userSessionLog messages : {}", userSessionLog, e);
			String message = messageSource.getMessage("unable.to.save", new Object[] { userSessionLog }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	public UserSessionLog updateUserSessionLog(Project project, Locale locale, String username, Long clientId) {
		log.debug("Try to  update userSessionLog with project {}", project);
		try {
			if (project == null) {
				String message = messageSource.getMessage("unable.to.update.null.object", new Object[] { project }, locale);
				throw new BcephalException(message);
			}
			ClientBase client = clientRepository.findById(clientId).get();
			UserSessionLog userL = repository.findByName(username).get(0);
			userL.setClientId(client.getId());
			userL.setClientName(client.getName());
			userL.setProjectId(project.getId());
			userL.setProjectCode(project.getCode());
			userL.setProjectName(project.getName());
			repository.save(userL);
			log.debug("{} userSessionLog successfully saved ", project);
			return userL;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save userSessionLog messages : {}", project, e);
			String message = messageSource.getMessage("unable.to.save", new Object[] { project }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession,
			Long objectId, String functionalityCode,String rightLevel, Long profileId) {
		log.debug("Try to  save user Session Log with client {} project {}",clientId , projectCode);
		try {
			if (!StringUtils.hasText(projectCode) || !StringUtils.hasText(username) || clientId == null) {
				String message = messageSource.getMessage("unable.to.update.null.object", new Object[] { clientId , projectCode,clientId }, Locale.ENGLISH);
				throw new BcephalException(message);
			}
			Optional<ClientBase> client = clientRepository.findById(clientId);
			Optional<UserData> user = userRepository.findByUsername(username);
			Optional<Project> project = projectRepository.findByCode(projectCode);
			Optional<ProfileData> profile = proFileDataRepository.findById(profileId);
			UserSessionLog userL = new UserSessionLog();
			
			if(user.isPresent()) {
				userL.setUserId(user.get().getId());
				userL.setUserType(user.get().getType());
			}			
			userL.setStatus("CONNECTED");
			if(client.isPresent()) {
				userL.setClientId(client.get().getId());
				userL.setClientName(client.get().getName());
			}
			userL.setProjectCode(projectCode);
			if(profile.isPresent()) {
				userL.setProfile(profile.get().getName());
			}
			if(project.isPresent()) {
				userL.setProjectId(project.get().getId());			
				userL.setProjectName(project.get().getName());
			}			
			userL.setFunctionality(functionalityCode);
			userL.setName("Operation_Log");
			userL.setObjectId(objectId);
			userL.setRightLevel(RightLevel.valueOf(rightLevel));
			userL.setUsername(username);
			userL.setUsersession(usersession);
			Timestamp dat = new Timestamp(System.currentTimeMillis());
			userL.setLastoperationDate(dat);
			userL.setCreationDate(dat);
			userL.setModificationDate(dat);	
			userL.setEndDate(dat);	
			repository.save(userL);
			log.debug("{} userSessionLog successfully saved client {} project {}",clientId , projectCode);
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save user Session Log messages client {} project {} : {}", clientId , projectCode, e);
			String message = messageSource.getMessage("unable.to.save", new Object[] {  clientId , projectCode}, Locale.ENGLISH);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	public UserSessionLog disconnectUserSessionLog(String username, String usersession, Locale locale) {
		log.debug("Try to  update userSessionLog with username {}", username);
		try {
			if (username == null) {
				String message = messageSource.getMessage("unable.to.update.null.object", new Object[] { username }, locale);
				throw new BcephalException(message);
			}
			UserSessionLog userL = repository.findByName(username).get(0);
			if(userL.getStatus().equals("CONNECTED"))userL.setStatus("DISCONNECTED");
			repository.save(userL);
			log.debug("{} userSessionLog successfully updated ", username);
			return userL;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while update userSessionLog messages : {}", username, e);
			String message = messageSource.getMessage("unable.to.update", new Object[] { username }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}


	public boolean disconnectUserSessionLog(String username, Locale locale, Long clientId, String projectCode, String sessionId) {
		log.debug("Try to  update userSessionLog with username {}", username);
		try {
			if (username == null) {
				String message = messageSource.getMessage("unable.to.update.null.object", new Object[] { username }, locale);
				throw new BcephalException(message);
			}
			UserSessionLog userL = new UserSessionLog();
			userL.setStatus("DISCONNECTED");
			userL.setClientId(clientId);
			userL.setProjectCode(projectCode);
			userL.setUsername(username);
			userL.setUsersession(sessionId);			
			repository.save(userL);
			log.debug("{} userSessionLog successfully updated ", username);
			return true;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while update userSessionLog messages : {}", username, e);
			String message = messageSource.getMessage("unable.to.update", new Object[] { username }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	String whereSql ="";
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean deleteByFilter(BrowserDataFilter filter, Locale locale, Long clientId, String projectCode) {
		log.debug("Try to  delete userSessionLog by filter {}", filter);
		try {
			if (filter == null) {
				String message = messageSource.getMessage("unable.to.update.null.object", new Object[] { filter }, locale);
				throw new BcephalException(message);
			}

			String sql = "DELETE FROM com.moriset.bcephal.security.domain.UserSessionLog ";
			whereSql ="";
			if(filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
		    	filter.getColumnFilters().getItems().forEach(filte ->{
		    		build(filte);
		    	});
				if(!filter.getColumnFilters().isGrouped()) {
					whereSql = "WHERE " + filter.getColumnFilters().getName() + getOperator(filter.getColumnFilters()) +" :" + filter.getColumnFilters().getName();
				}else {
					filter.getColumnFilters().getItems().forEach(filte ->{
						if(!StringUtils.hasText(whereSql)) {
							whereSql = "WHERE " + filte.getName() + getOperator(filte) + " :" + filte.getName();
						}else {
							whereSql += " AND " + filte.getName() + getOperator(filte) +" :" + filte.getName();
						}
			    	});
				}
			}
			
			Query query = session.createNativeQuery(sql);
			GridFilterOperator gridFilterOperator = new GridFilterOperator();
			if(filter.getColumnFilters() != null) {
				if(!filter.getColumnFilters().isGrouped()) {
					String op = getOperator(filter.getColumnFilters());
					String value_ = filter.getColumnFilters().getValue();
					if(op.equals("LIKE")) {
						if(gridFilterOperator.isContains(filter.getColumnFilters().getOperation())) {
							value_ = "%" + value_ + "%";
						}else if(gridFilterOperator.isStartsWith(filter.getColumnFilters().getOperation())) {
							value_ = value_ + "%";
						}else if(gridFilterOperator.isEndsWith(filter.getColumnFilters().getOperation())) {
							value_ = "%" + value_;
						}
					}
					TypedParameterValue value = new TypedParameterValue(StandardBasicTypes.STRING, value_);
					if(filter.getColumnFilters().getType().equals(Date.class)) {
						value = new TypedParameterValue(StandardBasicTypes.DATE, value_);
					}else
						if(filter.getColumnFilters().getType().equals(Boolean.class)) {
							value = new TypedParameterValue(StandardBasicTypes.BOOLEAN, value_);
						}
					query.setParameter(filter.getColumnFilters().getName(),value);
				}else {
					filter.getColumnFilters().getItems().forEach(filte ->{
						String op = getOperator(filte);
						String value_ = filte.getValue();
						if(op.equals("LIKE")) {
							if(gridFilterOperator.isContains(filte.getOperation())) {
								value_ = "%" + value_ + "%";
							}else if(gridFilterOperator.isStartsWith(filte.getOperation())) {
								value_ = value_ + "%";
							}else if(gridFilterOperator.isEndsWith(filte.getOperation())) {
								value_ = "%" + value_;
							}
						}
						TypedParameterValue value = new TypedParameterValue(StandardBasicTypes.STRING, value_);
						if(filte.getType().equals(Date.class)) {
							value = new TypedParameterValue(StandardBasicTypes.DATE, value_);
						}else
							if(filte.getType().equals(Boolean.class)) {
								value = new TypedParameterValue(StandardBasicTypes.BOOLEAN, value_);
							}
						query.setParameter(filte.getName(), value);
			    	});
				}
			}
			query.executeUpdate();
			log.debug("{} delete userSessionLog successfully ", filter);
			return true;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while update userSessionLog messages : {}", filter, e);
			String message = messageSource.getMessage("unable.to.update", new Object[] { filter }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}


	private String getOperator(ColumnFilter filte) {
		if(filte == null|| !StringUtils.hasText(filte.getOperation())) {
			return "=";
		}
		GridFilterOperator gridFilterOperator = new GridFilterOperator();
		if(gridFilterOperator.isEquals(filte.getOperation())) {
			return "=";
		}else
			if(gridFilterOperator.isNotEquals(filte.getOperation())) {
				return "!=";
			}
			else
				if(gridFilterOperator.isLess(filte.getOperation())) {
					return "<";
				}
				else
					if(gridFilterOperator.isLessOrEquals(filte.getOperation())) {
						return "<=";
					}
					else
						if(gridFilterOperator.isGreater(filte.getOperation())) {
							return ">";
						}
						else
							if(gridFilterOperator.isGreaterOrEquals(filte.getOperation())) {
								return ">=";
							}
							else
								if(gridFilterOperator.isContains(filte.getOperation()) 
										|| gridFilterOperator.isEndsWith(filte.getOperation()) 
										|| gridFilterOperator.isStartsWith(filte.getOperation())) {
									return "LIKE";
								}
								else
									if(gridFilterOperator.isNotContains(filte.getOperation())) {
										return "NOT LIKE";
									}
		return "=";
	}
	
}
