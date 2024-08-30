package com.moriset.bcephal.security;

import java.util.Locale;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.security.domain.Address;
import com.moriset.bcephal.security.domain.Client;
import com.moriset.bcephal.security.domain.ClientNature;
import com.moriset.bcephal.security.domain.ClientStatus;
import com.moriset.bcephal.security.domain.ClientType;
import com.moriset.bcephal.security.domain.Profile;
import com.moriset.bcephal.security.domain.ProfileDashboard;
import com.moriset.bcephal.security.domain.ProfileProject;
import com.moriset.bcephal.security.domain.ProfileType;
import com.moriset.bcephal.security.domain.ProfileUser;
import com.moriset.bcephal.security.domain.Right;
import com.moriset.bcephal.security.domain.RightLevel;
import com.moriset.bcephal.security.domain.User;
import com.moriset.bcephal.security.domain.UserInfo;
import com.moriset.bcephal.security.domain.UserSessionLog;



public class SecurityFactory {

    private String clientId = "45643107";
    private String clientId2 = "4333107";
    private String clientId3 = "1111";
    private String clientName = "Bcephal=56767458";
    private String projectCode = "project123";
	
	public Client buildClient() {

		Client client = Client.builder().build();
		client.setName(clientName);
		client.setClientId("Bcephal=56767458");
	 	client.setCode("Bcephal=56767458");
		client.setDescription("default client");
		client.setAddress(builAddress());
		client.setNature(ClientNature.COMPANY);
		client.setStatus(ClientStatus.ACTIVE);
		client.setType(ClientType.PRIVILEGE);
		client.setSecret("##123456");
		client.setDefaultClient(true);
		client.setDefaultLanguage(Locale.FRENCH.toString());
		client.setMaxUser(5);
		return client;
	}

	public Address builAddress() {

		Address address = new Address();
		address.setEmail("abc@gmail.com");
		address.setCity("y");
		address.setCountry("cam");
		return address;
	}

	public Right buildRight() {

		Right right = new Right();
		right.setFunctionality("project");
		right.setLevel(RightLevel.ACTION);
		right.setProjectCode(projectCode);
		return right;
	}

	public User buildAdminUser() {
		User user = new User();
		user.setName("TableauCraieProfesseur");
		user.setUserId(String.valueOf(1L));
		user.setUsername("Professeur");
		user.setEnabled(true);
		user.setEmailVerified(true);
		user.setFirstName("Tableau");
		user.setLastName("Craie");
		user.setEmail("prof@ecole.com");
		user.setDefaultLanguage("fr");
		user.setType(ProfileType.ADMINISTRATOR);
		Long clientId_ = Long.parseLong(clientId3);
		user.setClientId(clientId_);
		user.setProfileListChangeHandler(buildProfiles());
		return user;
	}
	
	public User buildUser() {
		User user = new User();
		user.setName("zra");
		user.setUserId(String.valueOf(2L));
		user.setUsername("elphenomeno");
		user.setEnabled(true);
		user.setEmailVerified(true);
		user.setFirstName("####");
		user.setLastName("maten");
		user.setEmail("matdu@moriset.com");
		user.setDefaultLanguage("fr");
		user.setType(ProfileType.USER);
		Long clientId_ = Long.parseLong(clientId);
		user.setClientId(clientId_);
		user.setPassword("#Testament123456=?");
		return user;
	}
	
	public User buildUser2() {
		User user = new User();
		user.setName("ABC");
		user.setUserId(String.valueOf(3L));
		user.setUsername("user3");
		user.setEnabled(true);
		user.setEmailVerified(true);
		user.setFirstName("####");
		user.setLastName(" camer");
		user.setEmail("abc@moriset.com");
		user.setDefaultLanguage("fr");
		user.setType(ProfileType.GUEST);
		Long clientId_ = Long.parseLong(clientId2);
		user.setClientId(clientId_);
		user.setProfileListChangeHandler(buildProfiles());
		return user;
	}
	
	public Profile buildProfile() {

		Profile profile = new Profile();
		profile.setName("MyProfile14");
		profile.setType(ProfileType.ADMINISTRATOR);
		profile.setClientId(1L);
		profile.setClient(clientName);
		profile.setDescription("It is admin");
		profile.setCode("MyProfile14");
		return profile;
	}
	
	public ProfileDashboard buildProfileDashboard() {

		ProfileDashboard profileDashboard = new ProfileDashboard();
		profileDashboard.setName("ProfileDashboard15");
		profileDashboard.setDefaultDashboard(true);
		profileDashboard.setPosition(2);
		return profileDashboard;
	}

	public ProfileProject buildprofileProject() {
		ProfileProject profileProject = new ProfileProject();
		profileProject.setProjectCode(projectCode);
		profileProject.setPosition(6);
		profileProject.setName("profileProject");
		return profileProject;
	}
	
	public UserInfo buildUserInfo() {
		UserInfo userInfo = new UserInfo();
		Long clientId_ = Long.parseLong(clientId);
		userInfo.setClientId(clientId_);
		userInfo.setDefaultLanguage(Locale.FRENCH.toString());
		userInfo.setFirstName("Bcephal");
		userInfo.setLastName("Default");
		userInfo.setName(clientName);
		userInfo.setLastName(clientName);
		userInfo.setType(ProfileType.ADMINISTRATOR);
		userInfo.setUsername("Bcephal");
		return userInfo;
	}
	
	public ProfileUser buildProfileUser() {
		ProfileUser profileUser = new ProfileUser();
		Long clientId_ = Long.parseLong(clientId);
		profileUser.setClientId(clientId_);
		profileUser.setProfileId(1L);
		profileUser.setUserId(2L);
		return profileUser;
	}
	
	public ListChangeHandler<Nameable> buildProfiles() {
		ListChangeHandler<Nameable> profiles = new ListChangeHandler<Nameable>();
		Nameable profile1 = new Nameable(null, "profile1");
		Nameable profile2 = new Nameable(null, "profile2");
		Nameable profile3 = new Nameable(null, "profile3");
		profiles.addNew(profile1);
		profiles.addNew(profile2);
		profiles.addNew(profile3);
		profiles.addDeleted(profile1);
		profiles.addDeleted(profile2);
		profiles.addDeleted(profile3);
		return profiles;
	}
	
	public ListChangeHandler<Profile> buildProfiles_() {
		ListChangeHandler<Profile> profiles = new ListChangeHandler<Profile>();
		profiles.addNew(buildProfile1());
		profiles.addNew(buildProfile2());
		profiles.addNew(buildProfile3());
		profiles.addDeleted(buildProfile1());
		profiles.addDeleted(buildProfile2());
		profiles.addDeleted(buildProfile3());
		return profiles;
	}
	
	public Profile buildProfile1() {

		Profile profile = new Profile();
		profile.setName("Profile1");
		profile.setType(ProfileType.ADMINISTRATOR);
		long Id = Long.parseLong(clientId);
		profile.setClientId(Id);
		profile.setClient(clientName);
		profile.setCode("Profile1");
		return profile;
	}
	public Profile buildProfile2() {

		Profile profile = new Profile();
		profile.setName("Profile2");
		profile.setType(ProfileType.USER);
		long Id = Long.parseLong(clientId);
		profile.setClientId(Id);
		profile.setClient(clientName);
		profile.setCode("Profile2");
		return profile;
	}
	public Profile buildProfile3() {

		Profile profile = new Profile();
		profile.setName("Profile3");
		profile.setType(ProfileType.SUPERUSER);
		long Id = Long.parseLong(clientId);
		profile.setClientId(Id);
		profile.setClient(clientName);
		profile.setCode("Profile3");
		return profile;
	}
	
	public String getProjectCode() {
		
		return projectCode;
	}

	public String getClientName() {
		
		return clientName;
	}
	
	public UserSessionLog buildUserSessionLog() {
		UserSessionLog userSessionLog = new UserSessionLog();
		userSessionLog.setClientId(1L);
		userSessionLog.setClientName(clientName);
		userSessionLog.setName(clientName);
		userSessionLog.setUsername("Landry");
		
		
		return userSessionLog;
	}
}
