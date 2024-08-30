/**
 * 
 */
package com.moriset.bcephal.security.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.project.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.domain.ProjectBrowserData;
import com.moriset.bcephal.security.domain.ProjectEditorData;
import com.moriset.bcephal.security.repository.ProfileRepository;
import com.moriset.bcephal.security.repository.ProjectBrowserDataRepository;
import com.moriset.bcephal.security.repository.ProjectProfileRepository;
import com.moriset.bcephal.security.repository.ProjectRepository;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * Project service
 * 
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class ProjectService {

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	ProjectProfileRepository projectProfileRepository;

	@Autowired
	ProjectBrowserDataRepository projectBrowserDataRepository;

	@Autowired
	SubscriptionService subscriptionService;

	@Autowired
	ProfileRepository profileRepository;
	
	@Autowired
	MessageSource messageSource;
	

	public EditorData<Project> getEditorData(EditorDataFilter filter, Locale locale) {
		ProjectEditorData data = new ProjectEditorData();

		if (filter.isNewData()) {
			data.setItem(getNewItem());
		} else {
			Optional<Project> item = findById(filter.getId());
			if (!item.isEmpty()) {
				data.setItem(item.get());
			}
		}
		data.clients = subscriptionService.findAll();
		return data;
	}

	protected Project getNewItem() {
		Project project = new Project();
		String baseName = "Project ";
		int i = 1;
		project.setName(baseName + i);
		while (!findByName(project.getName()).isEmpty()) {
			i++;
			project.setName(baseName + i);
		}
		return project;
	}

	public Sort getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getColumnFilters() != null) {

			if (filter.getColumnFilters().isSortFilter()) {
				build(filter.getColumnFilters());
				if (filter.getColumnFilters().getLink() != null
						&& filter.getColumnFilters().getLink().equals(BrowserDataFilter.SortByDesc)) {

					if (!filter.getColumnFilters().isJoin()) {
						return Sort.by(Order.desc(filter.getColumnFilters().getName()));
					} else {
						String name = filter.getColumnFilters().getJoinName() + "_"
								+ filter.getColumnFilters().getName();
						return Sort.by(Order.desc(name));
					}
				} else {
					if (!filter.getColumnFilters().isJoin()) {
						return Sort.by(Order.asc(filter.getColumnFilters().getName()));
					} else {
						String name = filter.getColumnFilters().getJoinName() + "_"
								+ filter.getColumnFilters().getName();
						return Sort.by(Order.asc(name));
					}
				}
			} else {
				if (filter.getColumnFilters().getItems() != null && filter.getColumnFilters().getItems().size() > 0) {
					List<Order> orders = new ArrayList<Order>();
					for (ColumnFilter columnFilter : filter.getColumnFilters().getItems()) {
						if (columnFilter.isSortFilter()) {
							build(columnFilter);
							if (columnFilter.getLink() != null
									&& columnFilter.getLink().equals(BrowserDataFilter.SortByDesc)) {
								if (!columnFilter.isJoin()) {
									orders.add(Order.desc(columnFilter.getName()));
								} else {
									String name = columnFilter.getJoinName() + "_" + columnFilter.getName();
									orders.add(Order.desc(name));
								}
							} else {
								if (!columnFilter.isJoin()) {
									orders.add(Order.asc(columnFilter.getName()));
								} else {
									String name = columnFilter.getJoinName() + "_" + columnFilter.getName();
									orders.add(Order.asc(name));
								}
							}
						}
					}
					if(orders.size() > 0) {
						return Sort.by(orders);
					}
				}
			}
		}
		return Sort.by(Order.asc("name"));
	}
	
	public BrowserDataPage<ProjectBrowserData> search(BrowserDataFilter filter, Long clientId, Long profileId,
			java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<ProjectBrowserData> page = new BrowserDataPage<ProjectBrowserData>();
		page.setPageSize(filter.getPageSize());

		Optional<Profile> response = profileRepository.findById(profileId);
		if (response.isEmpty()) {
			return page;
		} else {
			if (response.get().getType().isAdministratorOrSuperUser()) {
				Specification<ProjectBrowserData> specification = getBrowserDatasSpecification(filter, clientId,
						profileId, locale);
				if (filter.isShowAll()) {
					List<ProjectBrowserData> items = projectBrowserDataRepository.findAll(specification,getBrowserDatasSort(filter, locale));
					page.setItems(items);

					page.setCurrentPage(1);
					page.setPageCount(1);
					page.setTotalItemCount(items.size());

					if (page.getCurrentPage() > page.getPageCount()) {
						page.setCurrentPage(page.getPageCount());
					}
					page.setPageFirstItem(1);
					page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
				} else {
					Page<ProjectBrowserData> oPage = projectBrowserDataRepository.findAll(specification,
							getPageable(filter, getBrowserDatasSort(filter, locale)));
					page.setItems(oPage.getContent());

					page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
					page.setPageCount(oPage.getTotalPages());
					page.setTotalItemCount(Long.valueOf(oPage.getTotalElements()).intValue());

					if (page.getCurrentPage() > page.getPageCount()) {
						page.setCurrentPage(page.getPageCount());
					}
					page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
					page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
				}

				return page;
			} else {
				List<ProjectBrowserData> items = projectBrowserDataRepository.getBySubscriptionIdAndProfileId(clientId,
						profileId);
				page.setItems(items);
				page.setCurrentPage(1);
				page.setPageCount(1);
				page.setTotalItemCount(items.size());
				if (page.getCurrentPage() > page.getPageCount()) {
					page.setCurrentPage(page.getPageCount());
				}
				page.setPageFirstItem(1);
				page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
				return page;
			}
		}
	}

	protected Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}

	protected Specification<ProjectBrowserData> getBrowserDatasSpecification(BrowserDataFilter filter, Long clientId,
			Long profileId, java.util.Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<ProjectBrowserData> qBuilder = new RequestQueryBuilder<ProjectBrowserData>(root, query,
					cb);
			qBuilder.select(ProjectBrowserData.class);
			if (clientId != null) {
				qBuilder.addEquals("subscriptionId", clientId);
			}
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
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

	private void build(ColumnFilter columnFilter) {
		
		if ("name".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("name");
			columnFilter.setType(String.class);
		} else if ("DefaultProject".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("defaultProject");
			columnFilter.setType(Boolean.class);
		}  else if ("CreationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creationDate");
			columnFilter.setType(Date.class);
		} else if ("ModificationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("modificationDate");
			columnFilter.setType(Date.class);
		}
	}
	/**
	 * Retrieves project with the given ID.
	 * 
	 * @param id ID of project to find
	 * @return Project with the given ID
	 */
	public Optional<Project> findById(Long id) {
		log.trace("Find security project by ID: {}", id);
		return projectRepository.findById(id);
	}

	/**
	 * Saves the given project in the DB.
	 * 
	 * @param project Project to save
	 * @return saved Project
	 */
	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER, propagation = Propagation.REQUIRES_NEW)
	public Project save(Project project) {
		log.trace("Try to save project : Name = {} ; Code = {}", project.getName(), project.getCode());
		if (project.getId() == null && project.getCreationDate() == null) {
			project.setCreationDate(new Timestamp(System.currentTimeMillis()));
		}
		validateBeforeSave(project, Locale.ENGLISH);
		return projectRepository.saveAndFlush(project);
	}

	/**
	 * Renames the project with the given ID.
	 * 
	 * @param id      ID of project project to rename.
	 * @param newName The new name
	 * @return Renamed project
	 * @throws BcephalException If there is no project with the given ID.
	 */
	public Optional<Project> rename(Long id, String newName) throws BcephalException {
		log.trace("Try to rename project: ID = {} ; New name = {}", id, newName);
		Optional<Project> project = projectRepository.findById(id);
		if (!project.isEmpty()) {
			project.get().setName(newName);
			return project;
		}
		log.debug("Project not found: ID = {}", id);
		throw new BcephalException(HttpStatus.NOT_FOUND.value(), "project.not.found");
	}
	
	public List<Project> getAllByName(String name) {
		log.debug("Try to  get by name : {}", name);
		if (projectRepository == null) {
			return null;
		}
		return projectRepository.findByName(name);
	}
	
	protected void validateBeforeSave(Project entity, Locale locale) {
		List<Project> objects = getAllByName(entity.getName());
		for(Project obj : objects) {
			if(!obj.getId().equals(entity.getId())) {
				String message = messageSource.getMessage("duplicate.name", new Object[] { entity.getName() },
						locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}
		}
	}
	

	/**
	 * Deletes the project with the given code.
	 * 
	 * @param code Code of project project to delete.
	 * @return
	 */
	public int delete(String code) {
		log.trace("Try to delete project with code : {}", code);
		return projectRepository.deleteByCode(code);
	}

	/**
	 * Deletes the project with the given id.
	 * 
	 * @param id of project project to delete.
	 * @return
	 */
	public void deleteById(Long id) {
		log.trace("Try to delete project with id : {}", id);
		projectRepository.deleteById(id);
	}

	/**
	 * Find project by code
	 * 
	 * @param code
	 * @return
	 */
	public Optional<ProjectBrowserData> findByCode(String code) {
		log.trace("Try to retrieve project by code id : {}", code);
		return projectBrowserDataRepository.findByCode(code);
	}

	/**
	 * Find project by subscription Id
	 * 
	 * @param subscriptionId
	 * @return
	 */
	public List<ProjectBrowserData> findBySubcription(Long subscriptionId, Long profileId) {
		log.trace("Try to retrieve project by subscription id : {}", subscriptionId);
		return projectBrowserDataRepository.getBySubscriptionIdAndProfileId(subscriptionId, profileId);
	}

	/**
	 * Find project by subscription Id
	 * 
	 * @param subscriptionId
	 * @return
	 */
	public List<ProjectBrowserData> findBySubcriptionAndName(Long subscriptionId, String projectName) {
		log.trace("Try to retrieve project by subscription id : {} and name : {}", subscriptionId, projectName);
		return projectBrowserDataRepository.findBySubscriptionIdAndNameIgnoreCase(subscriptionId, projectName);
	}

	public List<ProjectBrowserData> findBySubcriptionAndDefault(Long subscriptionId, Long profileId,
			Boolean defaultProject, String username) {
		log.trace("Try to retrieve default projects by subscription id : {} ", subscriptionId);
		return projectBrowserDataRepository.findBySubscriptionIdAndDefaultProject(subscriptionId, profileId,
				defaultProject);
	}

	public Optional<ProjectBrowserData> findBySubcriptionAndCode(Long subscriptionId, String projectCode) {
		log.trace("Try to retrieve project by subscription id : {} and code : {}", subscriptionId, projectCode);
		return projectBrowserDataRepository.findBySubscriptionIdAndCode(subscriptionId, projectCode);
	}

	/**
	 * Find all projects
	 * 
	 * @return
	 */
	public List<ProjectBrowserData> findAll() {
		log.trace("Try to retrieve all projects");
		List<ProjectBrowserData> projects = projectBrowserDataRepository.findAll();
		return projects;
	}

	public List<Project> findByNameOrCode(String name, String projectCode) {
		log.trace("Try to retrieve all projects by name : {} or code :{}", name, projectCode);
		return projectRepository.findByNameOrCode(name, projectCode);
	}

	public List<Project> findByName(String name) {
		log.trace("Try to retrieve all projects by name : {}", name);
		return projectRepository.findByName(name);
	}
	
	public Optional<Project> findByCode_(String name) {
		log.trace("Try to retrieve all projects by name : {}", name);
		return projectRepository.findByCode(name);
	}

	public boolean existsByName(String name) {
		log.trace("Test if project exist by name : {}", name);
		return projectRepository.existsByName(name);
	}

	public boolean existsByCode(String code) {
		log.trace("Test if project exist by code : {}", code);
		return projectRepository.existsByCode(code);
	}

	public Optional<ProjectBrowserData> findBySubcriptionAndProjectId(Long subscriptionId, Long projectId) {
		log.trace("Try to retrieve project by subscription id : {} and project id : {}", subscriptionId, projectId);
		return projectBrowserDataRepository.findBySubscriptionIdAndId(subscriptionId, projectId);
	}
}
