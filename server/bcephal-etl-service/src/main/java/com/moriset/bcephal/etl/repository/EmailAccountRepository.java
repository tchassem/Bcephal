package com.moriset.bcephal.etl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.moriset.bcephal.etl.domain.EmailAccount;

public interface EmailAccountRepository extends JpaRepository<EmailAccount, Long>,JpaSpecificationExecutor<EmailAccount> {

}
