/**
 * 6 févr. 2024 - UserLoaderFactory.java
 *
 */
package com.moriset.bcephal.scheduler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.moriset.bcephal.loader.domain.UserLoader;
import com.moriset.bcephal.loader.domain.UserLoaderCondition;
import com.moriset.bcephal.loader.domain.UserLoaderItemType;
import com.moriset.bcephal.loader.domain.UserLoaderMenu;
import com.moriset.bcephal.loader.domain.UserLoaderScheduler;
import com.moriset.bcephal.loader.domain.UserLoaderSchedulerType;
import com.moriset.bcephal.loader.domain.UserLoaderTreatment;

/**
 * @author Emmanuel Emmeni
 *
 */
public class UserLoaderFactory {

	public UserLoader build() {
		UserLoader userLoader = new UserLoader();
		userLoader.setName("User Loader Test Factory");
		userLoader.initListChangeHandler();
		userLoader.setTreatment(buildTreatment());
		userLoader.setCreationDate(new Timestamp(System.currentTimeMillis()));
		userLoader.setModificationDate(new Timestamp(System.currentTimeMillis()));
		userLoader.setDescription(String.format("User loader builded at %s", System.currentTimeMillis()));
		userLoader.setMenu(buildMenu(userLoader.getName()));
		/*List<UserLoaderCondition> conds = buildConditions();
		conds.forEach(cond ->{
			userLoader.getConditionListChangeHandler().addNew(cond);
		});*/
		buildConditions().forEach(cond -> userLoader.getConditionListChangeHandler().addNew(cond));
		userLoader.getSchedulerListChangeHandler().addNew(buildScheduler());
		return userLoader;
	}	
	
	public UserLoaderMenu buildMenu(String name) {
		UserLoaderMenu menu = new UserLoaderMenu();
		menu.initialize(name);
		return menu;
	}	
	
	public UserLoaderTreatment buildTreatment() {
		UserLoaderTreatment treatment = UserLoaderTreatment.values()[new Random().nextInt(UserLoaderTreatment.values().length)];
		return treatment;
	}	
	
	public List<UserLoaderCondition> buildConditions() {
		List<UserLoaderCondition> conditions = new ArrayList<UserLoaderCondition>();
		UserLoaderCondition item = new UserLoaderCondition();
		item.setType(UserLoaderItemType.CHECK_DUPLICATE);
		conditions.add(item);
		item = new UserLoaderCondition();
		item.setType(UserLoaderItemType.COLUMN_COUNT);
		item.setComparator(">");
		item.setLongValue(5L);
		conditions.add(item);
		item = new UserLoaderCondition();
		item.setType(UserLoaderItemType.FILE_NAME);
		item.setComparator("EQUALS");
		item.setStringValue("activities");
		item.setErrorMessage("Be careful with this loader!!!");
		conditions.add(item);
		return conditions;
	}	
	
	public UserLoaderScheduler buildScheduler() {
		UserLoaderScheduler scheduler = new UserLoaderScheduler();
		scheduler.setType(UserLoaderSchedulerType.AFTER);
		scheduler.setSchedulerId(1L);
		return scheduler;
	}
}
