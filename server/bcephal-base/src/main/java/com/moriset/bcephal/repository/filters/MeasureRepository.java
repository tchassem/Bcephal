package com.moriset.bcephal.repository.filters;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.domain.dimension.Measure;

@Repository
public interface MeasureRepository extends DimensionRepository<Measure> {

}
