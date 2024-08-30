package com.moriset.bcephal.loader.repository;

import java.util.List;

import com.moriset.bcephal.loader.domain.UserLoaderMenu;
import com.moriset.bcephal.repository.PersistentRepository;

public interface UserLoaderMenuRepository extends PersistentRepository<UserLoaderMenu> {
	List<UserLoaderMenu> findByActive(boolean active);

}
