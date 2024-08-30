/**
 * 
 */
package com.moriset.bcephal.sheet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.sheet.domain.SpreadSheetCell;

/**
 * @author Joseph Wambo
 *
 */
@Repository
public interface SpreadSheetCellRepository extends PersistentRepository<SpreadSheetCell> {

	Optional<SpreadSheetCell> findBySpreadSheetAndColAndRowAndSheetIndex(Long spreadSheet, int col, int row,
			int sheetIndex);

	List<SpreadSheetCell> findBySpreadSheet(Long spreadSheet);

}
