package com.moriset.bcephal.service.filters;

import java.util.List;

import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.domain.filters.UniverseFilter;

public interface ISpotService {

	public List<UniverseFilter> buildSpotReportFilter(Spot spot);
}
