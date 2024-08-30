package com.moriset.bcephal.security;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.Right;
import com.moriset.bcephal.security.domain.RightLevel;

public class ProfileFactory {

	public static List<Profile> BuildProfiles() {

		List<Profile> profiles = new ArrayList<Profile>();

		for (int i = 200; i < 220; i++) {
			Profile profile = new Profile();
			profile.setName("Profile".concat("" + i));
			profile.setCode("Profile".concat("" + i));
			profile.setUserListChangeHandler(BuildUserListChangeHandler(profile));
			profile.setRightListChangeHandler(BuildRightListChangeHandler(profile));
			profiles.add(profile);
		}

		return profiles;
	}

	public static ListChangeHandler<Nameable> BuildUserListChangeHandler(Profile profile) {
		ListChangeHandler<Nameable> userListChangeHandler = new ListChangeHandler<>();
		for (int i = 300; i < 320; i++) {
			Nameable nameable = new Nameable();
			nameable.setName("User".concat("" + i));
			nameable.setCode("User".concat("" + i));
			userListChangeHandler.addNew(nameable);
		}
		return userListChangeHandler;
	}

	public static ListChangeHandler<Right> BuildRightListChangeHandler(Profile profile) {
		ListChangeHandler<Right> rightListChangeHandler = new ListChangeHandler<Right>();

		Right right = new Right();
		right.setFunctionality("project");
		right.setLevel(RightLevel.CREATE);
		rightListChangeHandler.addNew(right);

		Right right2 = new Right();
		right.setFunctionality("sourcing.input.grid");
		right.setLevel(RightLevel.CREATE);
		rightListChangeHandler.addNew(right2);

		Right right3 = new Right();
		right.setFunctionality("sourcing.materialized.grid");
		right.setLevel(RightLevel.CREATE);
		rightListChangeHandler.addNew(right3);

		Right right4 = new Right();
		right.setFunctionality("sourcing.file.loader");
		right.setLevel(RightLevel.CREATE);
		rightListChangeHandler.addNew(right4);
		return rightListChangeHandler;
	}

}
