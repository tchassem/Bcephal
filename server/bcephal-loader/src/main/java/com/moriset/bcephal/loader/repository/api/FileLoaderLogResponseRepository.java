package com.moriset.bcephal.loader.repository.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.moriset.bcephal.loader.domain.api.FileLoaderLogResponse;


public interface FileLoaderLogResponseRepository extends JpaRepository<FileLoaderLogResponse, Long>, JpaSpecificationExecutor<FileLoaderLogResponse> {

	List<FileLoaderLogResponse> findByOperationCode(String operationCode);

}
