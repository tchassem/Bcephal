package com.moriset.bcephal.etl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.moriset.bcephal.etl.domain.EmailFilter;

public interface EmailFilterRepository extends JpaRepository<EmailFilter, Long>,JpaSpecificationExecutor<EmailFilter> {

}
