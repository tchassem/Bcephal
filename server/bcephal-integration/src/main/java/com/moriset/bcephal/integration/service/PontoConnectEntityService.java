package com.moriset.bcephal.integration.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.ibanity.apis.client.products.ponto_connect.models.Account;
import com.ibanity.apis.client.products.ponto_connect.models.FinancialInstitution;
import com.ibanity.apis.client.products.ponto_connect.models.Transaction;
import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.integration.domain.EntityColumn;
import com.moriset.bcephal.integration.domain.PontoConnectBrowserData;
import com.moriset.bcephal.integration.domain.PontoConnectEntity;
import com.moriset.bcephal.integration.domain.SslEntity;
import com.moriset.bcephal.integration.repository.EntityColumnRepository;
import com.moriset.bcephal.integration.repository.PontoConnectEntityRepository;
import com.moriset.bcephal.integration.repository.PontoConnectOauth2EntityRepository;
import com.moriset.bcephal.integration.repository.SslEntityRepository;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class PontoConnectEntityService extends PersistentService<PontoConnectEntity, BrowserData> {

	@Autowired
	private SslEntityRepository sslEntityRepository;

	@Autowired
	private PontoConnectOauth2EntityRepository pontoConnectOauth2EntityRepository;

	@Autowired
	private PontoConnectEntityRepository pontoConnectEntityRepository;

	@Autowired
	private EntityColumnRepository entityColumnRepository;

	@Autowired
	private PontoConnectProperties pontoConnectProperties;

	@Autowired
	UserSessionLogService logService;

	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession,
			Long objectId, String functionalityCode, String rightLevel, Long profileId) {
		logService.saveUserSessionLog(username, clientId, projectCode, usersession, objectId, functionalityCode,
				rightLevel, profileId);
	}

	public String getBrowserFunctionalityCode() {
		return FunctionalityCodes.SETTINGS_INTEGRATION_SERVICE;
	}

	public EditorData<PontoConnectEntity> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		EditorData<PontoConnectEntity> data = new EditorData<>();
		if (filter.isNewData()) {
			PontoConnectEntity grid = getNewItem();
			grid.setAuthorizationUrl(pontoConnectProperties.getSandboxAuthorizationUrl());
			grid.setApiEndpoint(pontoConnectProperties.getApiEndpoint());
			
			data.setItem(grid);
		} else {
			data.setItem(getById(filter.getId()));
		}
		addValue(data.getItem());
		addAccountName(data.getItem());
		addFinancialInstitutionName(data.getItem());
		return data;
	}

	private void addValue(PontoConnectEntity entity) {
		Field[] fields = Transaction.class.getDeclaredFields();
		for (Field field : fields) {
			if (!(field.getName().equalsIgnoreCase("RESOURCE_TYPE")
					|| field.getName().equalsIgnoreCase("API_URL_TAG_ID"))) {
				if(entity.getId() == null) {
					EntityColumn column = new EntityColumn();
					column.setName(field.getName());
					column.setCopyReferenceName(field.getName());
					column.setType(DimensionType.ATTRIBUTE);
					try {
						if (field.getType().equals(BigDecimal.class)) {
							column.setType(DimensionType.MEASURE);
						} else {
							if (field.getType().equals(Instant.class)) {
								column.setType(DimensionType.PERIOD);
							}
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
					column.setPosition(entity.getColumnListChangeHandler().getItems().size());
					entity.getColumnListChangeHandler().addNew(column);		
				}
				entity.getTransactionColumns().add(field.getName());
			}
		}
	}
	
	private void addAccountName(PontoConnectEntity entity) {
		Field[] fields = Account.class.getDeclaredFields();
		for (Field field : fields) {
			if (!(field.getName().equalsIgnoreCase("RESOURCE_TYPE")
					|| field.getName().equalsIgnoreCase("API_URL_TAG_ID"))) {				
				entity.getAccountColumns().add(field.getName());				
			}
		}
	}
	
	private void addFinancialInstitutionName(PontoConnectEntity entity) {
		Field[] fields = FinancialInstitution.class.getDeclaredFields();
		for (Field field : fields) {
			if (!(field.getName().equalsIgnoreCase("RESOURCE_TYPE")
					|| field.getName().equalsIgnoreCase("API_URL_TAG_ID"))) {				
				entity.getFinancialInstitutionColumns().add(field.getName());				
			}
		}
	}
	
	public PontoConnectEntity getByName(String name) {
		List<PontoConnectEntity> PontoConnectEntities = getAllByName(name);
		return PontoConnectEntities.size() > 0 ? PontoConnectEntities.get(0) : null;
	}

	public List<PontoConnectEntity> getAllByName(String name) {
		log.debug("Try to  get by name : {}", name);
		if (getRepository() == null) {
			return null;
		}
		return pontoConnectEntityRepository.findByName(name);
	}

	protected PontoConnectEntity getNewItem(String baseName, boolean startWithOne) {
		PontoConnectEntity entity = new PontoConnectEntity();
		int i = 0;
		entity.setName(baseName);
		if (startWithOne) {
			i = 1;
			entity.setName(baseName + i);
		}
		while (getByName(entity.getName()) != null) {
			i++;
			entity.setName(baseName + i);
		}
		return entity;
	}

	protected PontoConnectEntity getNewItem(String baseName) {
		return getNewItem(baseName, true);
	}

	protected PontoConnectEntity getNewItem() {
		return getNewItem("Ponto Connect ");
	}

	@Override
	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER,propagation = Propagation.REQUIRED)
	public PontoConnectEntity save(PontoConnectEntity pontoConnectEntity, Locale locale) {
		log.debug("Try to  Save PontoConnectEntity : {}", pontoConnectEntity);
		try {
			if (pontoConnectEntity == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.pontoConnectEntity",
						new Object[] { pontoConnectEntity }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasLength(pontoConnectEntity.getApiEndpoint())) {
				String message = getMessageSource().getMessage(
						"unable.to.save.pontoConnectEntity.with.empty.ApiEndpoint",
						new String[] { pontoConnectEntity.getApiEndpoint() }, locale);
				throw new BcephalException(message);
			}
			if (pontoConnectEntity.getOauth2() == null
					|| !StringUtils.hasLength(pontoConnectEntity.getOauth2().getClientId())) {
				String message = getMessageSource().getMessage("unable.to.save.pontoConnectEntity.with.empty.ClientId",
						new String[] { pontoConnectEntity.getAuthorizationUrl() }, locale);
				throw new BcephalException(message);
			}

			if (pontoConnectEntity.getOauth2() == null
					|| !StringUtils.hasLength(pontoConnectEntity.getOauth2().getClientSecret())) {
				String message = getMessageSource().getMessage(
						"unable.to.save.pontoConnectEntity.with.empty.ClientSecret",
						new String[] { pontoConnectEntity.getAuthorizationUrl() }, locale);
				throw new BcephalException(message);
			}

			if (pontoConnectEntity.getClientTls() == null
					|| !StringUtils.hasLength(pontoConnectEntity.getOauth2().getClientSecret())) {
				String message = getMessageSource().getMessage("unable.to.save.pontoConnectEntity.with.empty.ClientTls",
						new String[] { pontoConnectEntity.getAuthorizationUrl() }, locale);
				throw new BcephalException(message);
			}

			if (pontoConnectEntity.getClientTls() != null) {
				LoadBytes(pontoConnectEntity.getClientTls());
				pontoConnectEntity.setClientTls(sslEntityRepository.save(pontoConnectEntity.getClientTls()));
			}
			if (pontoConnectEntity.getClientTlsCa() != null) {
				LoadBytes(pontoConnectEntity.getClientTlsCa());
				pontoConnectEntity.setClientTlsCa(sslEntityRepository.save(pontoConnectEntity.getClientTlsCa()));
			}
			if (pontoConnectEntity.getClientSignature() != null) {
				LoadBytes(pontoConnectEntity.getClientSignature());
				pontoConnectEntity
						.setClientSignature(sslEntityRepository.save(pontoConnectEntity.getClientSignature()));
			}

			if (pontoConnectEntity.getOauth2() != null) {
				pontoConnectEntity.setOauth2(pontoConnectOauth2EntityRepository.save(pontoConnectEntity.getOauth2()));
			}

			pontoConnectEntity.setModificationDate(new Timestamp(System.currentTimeMillis()));

			ListChangeHandler<EntityColumn> columns = pontoConnectEntity.getColumnListChangeHandler();

			if(pontoConnectEntity.getStartDate() == null) {
				pontoConnectEntity.setStartDate(new Timestamp(System.currentTimeMillis()));
			}
			
			pontoConnectEntity = pontoConnectEntityRepository.save(pontoConnectEntity);

			PontoConnectEntity id = pontoConnectEntity;
			columns.getNewItems().forEach(item -> {
				log.trace("Try to save EntityColumn : {}", item);
				item.setConnectEntity(id);
				entityColumnRepository.save(item);
				log.trace("GrilleColumn saved : {}", item.getId());
			});
			columns.getUpdatedItems().forEach(item -> {
				log.trace("Try to save EntityColumn : {}", item);
				item.setConnectEntity(id);
				entityColumnRepository.save(item);
				log.trace("GrilleColumn saved : {}", item.getId());
			});
			columns.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete EntityColumn : {}", item);
					entityColumnRepository.deleteById(item.getId());
					log.trace("EntityColumn deleted : {}", item.getId());
				}
			});

			return pontoConnectEntity;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save pontoConnectEntity : {}", pontoConnectEntity, e);
			String message = getMessageSource().getMessage("unable.to.save.pontoConnectEntity",
					new Object[] { pontoConnectEntity }, locale);
			throw new BcephalException(message);
		}
	}

	public void refreshToken(PontoConnectEntity pontoConnectEntity) {
		if(pontoConnectEntity.isPersistent()) {
			PontoConnectEntity lastEntity = getById(pontoConnectEntity.getId());
			pontoConnectEntity.setAccessToken(lastEntity.getAccessToken());
			pontoConnectEntity.setRefreshToken(lastEntity.getRefreshToken());
			pontoConnectEntity.setExpiresIn(lastEntity.getExpiresIn());
			pontoConnectEntity.setScope(lastEntity.getScope());
			pontoConnectEntity.setTokenType(lastEntity.getTokenType());
		}		
	}

	private void LoadBytes(SslEntity clientTls) throws IOException {
		if (clientTls != null) {
			Optional<SslEntity> client = Optional.empty();
			if (clientTls.getId() != null) {
				client = sslEntityRepository.findById(clientTls.getId());
			}
			if (StringUtils.hasText(clientTls.getCertificatePath())) {
				byte[] keyDataBytes = Files.readAllBytes(Paths.get(clientTls.getCertificatePath()));
			    String keyDataString = new String(keyDataBytes, StandardCharsets.UTF_8);
				if (StringUtils.hasText(keyDataString)) {
					clientTls.setCertificate(keyDataString);
				}else if (client.isPresent()) {
					clientTls.setCertificate(client.get().getCertificate());
				}
			} else if (client.isPresent()) {
				clientTls.setCertificate(client.get().getCertificate());
			}

			if (StringUtils.hasText(clientTls.getPrivateKeyPath())) {
				byte[] keyDataBytes = Files.readAllBytes(Paths.get(clientTls.getPrivateKeyPath()));
			    String keyDataString = new String(keyDataBytes, StandardCharsets.UTF_8);
				if (StringUtils.hasText(keyDataString)) {
					clientTls.setPrivateKey(keyDataString);
				} else if (client.isPresent()) {
					clientTls.setPrivateKey(client.get().getPrivateKey());
				}
			} else if (client.isPresent()) {
				clientTls.setPrivateKey(client.get().getPrivateKey());
			}

		}
	}
	
	public void writeBytesTofile(String bytes, File file) throws IOException {
		Files.write(file.toPath(), bytes.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	@Transactional
	public void delete(PontoConnectEntity pontoConnectEntity) {
		log.debug("Try to delete pontoConnectEntity : {}", pontoConnectEntity);
		if (pontoConnectEntity == null || pontoConnectEntity.getId() == null) {
			return;
		}

		ListChangeHandler<EntityColumn> columns = pontoConnectEntity.getColumnListChangeHandler();
		columns.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete EntityColumn : {}", item);
				entityColumnRepository.deleteById(item.getId());
				log.trace("EntityColumn deleted : {}", item.getId());
			}
		});

		pontoConnectEntityRepository.deleteById(pontoConnectEntity.getId());
		if (pontoConnectEntity.getOauth2() != null) {
			pontoConnectOauth2EntityRepository.delete(pontoConnectEntity.getOauth2());
		}
		if (pontoConnectEntity.getClientSignature() != null) {
			sslEntityRepository.delete(pontoConnectEntity.getClientSignature());
		}
		if (pontoConnectEntity.getClientTlsCa() != null) {
			sslEntityRepository.delete(pontoConnectEntity.getClientTlsCa());
		}
		if (pontoConnectEntity.getClientTls() != null) {
			sslEntityRepository.delete(pontoConnectEntity.getClientTls());
		}
		return;
	}

	@Transactional
	public void deleteById(Long id) {
		PontoConnectEntity pontoConnectEntity = getById(id);
		log.debug("Try to delete pontoConnectEntity : {}", pontoConnectEntity);
		if (pontoConnectEntity == null || pontoConnectEntity.getId() == null) {
			return;
		}

		ListChangeHandler<EntityColumn> columns = pontoConnectEntity.getColumnListChangeHandler();
		columns.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete EntityColumn : {}", item);
				entityColumnRepository.deleteById(item.getId());
				log.trace("EntityColumn deleted : {}", item.getId());
			}
		});

		pontoConnectEntityRepository.deleteById(pontoConnectEntity.getId());
		if (pontoConnectEntity.getOauth2() != null) {
			pontoConnectOauth2EntityRepository.delete(pontoConnectEntity.getOauth2());
		}
		if (pontoConnectEntity.getClientSignature() != null) {
			sslEntityRepository.delete(pontoConnectEntity.getClientSignature());
		}
		if (pontoConnectEntity.getClientTlsCa() != null) {
			sslEntityRepository.delete(pontoConnectEntity.getClientTlsCa());
		}
		if (pontoConnectEntity.getClientTls() != null) {
			sslEntityRepository.delete(pontoConnectEntity.getClientTls());
		}
		return;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(List<Long> ids, Locale locale) {
		log.debug("Try to  delete : {} entities", ids.size());
		if (getRepository() == null) {
			return;
		}
		try {
			if (ids == null || ids.size() == 0) {
				String message = messageSource.getMessage("unable.to.delete.empty.list", new Object[] { ids }, locale);
				throw new BcephalException(message);
			}
			ids.forEach(this::deleteById);
			log.debug("{} entities successfully deleted ", ids.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete entities : {}", ids.size(), e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	public PersistentRepository<PontoConnectEntity> getRepository() {
		return pontoConnectEntityRepository;
	}

	protected Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}

	protected Specification<PontoConnectEntity> getBrowserDatasSpecification(BrowserDataFilter filter, Long clientId,
			Long profileId, java.util.Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<PontoConnectEntity> qBuilder = new RequestQueryBuilder<PontoConnectEntity>(root, query,
					cb);
			qBuilder.select(PontoConnectBrowserData.class);
			if (clientId != null) {
				qBuilder.addEquals("clientId", clientId);
			}
			if (filter != null && !org.apache.commons.lang3.StringUtils.isBlank(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}

			if (filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
				filter.getColumnFilters().getItems().forEach(filte -> {
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
		} else if ("CreationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("creationDate");
			columnFilter.setType(Date.class);
		} else if ("ModificationDateTime".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("modificationDate");
			columnFilter.setType(Date.class);
		} else if ("projectCode".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("projectCode");
			columnFilter.setType(String.class);
		}
	}

	public Sort getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getColumnFilters() != null
				&& filter.getColumnFilters().getOperation().equals(BrowserDataFilter.SortBy)) {
			build(filter.getColumnFilters());
			if (filter.getColumnFilters().getLink() != null
					&& filter.getColumnFilters().getLink().equals(BrowserDataFilter.SortByDesc)) {
				return Sort.by(Order.desc(filter.getColumnFilters().getName()));
			} else {
				return Sort.by(Order.asc(filter.getColumnFilters().getName()));
			}

		}
		return Sort.by(Order.asc("name"));
	}

	protected List<PontoConnectBrowserData> buildBrowserData(List<PontoConnectEntity> contents) {
		List<PontoConnectBrowserData> items = new ArrayList<PontoConnectBrowserData>(0);
		if (contents != null) {
			contents.forEach(item -> {
				PontoConnectBrowserData element = new PontoConnectBrowserData(item);
				if (element != null) {
					items.add(element);
				}

			});
		}
		return items;
	}

	public BrowserDataPage<PontoConnectBrowserData> search(BrowserDataFilter filter, Long clientId, Long profileId,
			java.util.Locale locale) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<PontoConnectBrowserData> page = new BrowserDataPage<PontoConnectBrowserData>();
		page.setPageSize(filter.getPageSize());

		Specification<PontoConnectEntity> specification = getBrowserDatasSpecification(filter, clientId, profileId,
				locale);
		if (filter.isShowAll()) {
			List<PontoConnectEntity> items = pontoConnectEntityRepository.findAll(specification,
					getBrowserDatasSort(filter, locale));
			page.setItems(buildBrowserData(items));

			page.setCurrentPage(1);
			page.setPageCount(1);
			page.setTotalItemCount(items.size());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		} else {
			Page<PontoConnectEntity> oPage = pontoConnectEntityRepository.findAll(specification,
					getPageable(filter, getBrowserDatasSort(filter, locale)));
			page.setItems(buildBrowserData(oPage.getContent()));

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
	}

}
