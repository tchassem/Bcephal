package com.moriset.bcephal.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.moriset.bcephal.messenger.model.Parameter;

public interface ParameterRepository extends JpaRepository<Parameter, Long>, JpaSpecificationExecutor<Parameter>, PagingAndSortingRepository<Parameter, Long> {

	Parameter findByCode(String code);

}
