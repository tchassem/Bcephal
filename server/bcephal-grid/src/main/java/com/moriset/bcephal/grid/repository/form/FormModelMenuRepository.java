package com.moriset.bcephal.grid.repository.form;

import java.util.List;

import com.moriset.bcephal.grid.domain.form.FormModelMenu;
import com.moriset.bcephal.repository.PersistentRepository;

public interface FormModelMenuRepository extends PersistentRepository<FormModelMenu> {
	List<FormModelMenu> findByActive(boolean active);
}
