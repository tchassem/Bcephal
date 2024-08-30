package com.moriset.bcephal.dashboard.repository;

import java.util.List;

import com.moriset.bcephal.dashboard.domain.Dashboard;
import com.moriset.bcephal.repository.MainObjectRepository;

public interface DashboardRepository extends MainObjectRepository<Dashboard> {

	List<Dashboard> findByPublished(boolean published);

}
