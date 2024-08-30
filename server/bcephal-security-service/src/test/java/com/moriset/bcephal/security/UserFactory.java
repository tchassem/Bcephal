package com.moriset.bcephal.security;

import java.util.ArrayList;
import java.util.List;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.security.domain.ProfileType;
import com.moriset.bcephal.security.domain.User;

public class UserFactory {

	@SuppressWarnings("unused")
	public static List<User> BuildUsers() {
		List<User> users = new ArrayList<>();
		User user = new User();
		user.setClient(null);
		user.setCreationDate(null);
		user.setDefaultLanguage(null);
		user.setEmail("georged1swakeu21@gmail.com");
		user.setEmailVerified(true);
		user.setEnabled(true);
		user.setName("Georged12");
		user.setFirstName("Georged12");
		user.setUsername("georged12");
		user.setUserId("Georged12");
		user.setType(ProfileType.ADMINISTRATOR);
		user.setPassword("12345678");
		user.setProfileListChangeHandler(BuildUserProfiles(user));
		users.add(user);
		
		User user1 = new User();
		user1.setClient(null);
		user1.setCreationDate(null);
		user1.setDefaultLanguage(null);
		user1.setEmailVerified(true);
		user1.setEnabled(true);
		user1.setName("joel1");
		user1.setFirstName("joel1");
		user1.setUsername("joel1");
		user1.setUserId("joel1");
		user1.setType(ProfileType.ADMINISTRATOR);
		user1.setPassword("12345678");
		user1.setProfileListChangeHandler(BuildUserProfiles(user1));
		users.add(user1);
		
		
		User user2 = new User();
		user2.setClient(null);
		user2.setCreationDate(null);
		user2.setDefaultLanguage(null);
		user2.setEmail("fdiondou@moriset.com");
		user2.setEmailVerified(true);
		user2.setEnabled(true);
		user2.setName("Fabiola1");
		user2.setFirstName("Fabiola1");
		user2.setUsername("fabiola1");
		user2.setUserId("Fabiola1");
		user2.setType(ProfileType.ADMINISTRATOR);
		user2.setPassword("12345678");
		user2.setProfileListChangeHandler(BuildUserProfiles(user2));
		users.add(user2);
		
		User user3 = new User();
		user3.setClient(null);
		user3.setCreationDate(null);
		user3.setDefaultLanguage(null);
		user3.setEmail("Emmanuel87@moriset.com");
		user3.setEmailVerified(true);
		user3.setEnabled(true);
		user3.setName("Emmanuel87175");
		user3.setFirstName("Emmanuel87175");
		user3.setUsername("emmanuel87175");
		user3.setUserId("Emmanuel87175");
		user3.setType(ProfileType.ADMINISTRATOR);
		user3.setPassword("12345678");
		user3.setProfileListChangeHandler(BuildUserProfiles(user3));
		users.add(user3);
		
		User user4 = new User();
		user4.setClient(null);
		user4.setCreationDate(null);
		user4.setDefaultLanguage("Fr");
		user4.setEmail("ludovictchassem@gmail.com");
		user4.setEmailVerified(true);
		user4.setEnabled(true);
		user4.setName("Ludovic1");
		user4.setFirstName("Tchassem1");
		user4.setLastName("Ludovic1");
		user4.setUsername("ludovic1");
		user4.setUserId("Ludovic1");
		user4.setType(ProfileType.USER);
		user4.setPassword("12345678");
		user4.setProfileListChangeHandler(BuildUserProfiles(user4));
		users.add(user4);
		
		User user5 = new User();
		user5.setClient(null);
		user5.setCreationDate(null);
		user5.setDefaultLanguage("Fr");
		user5.setEmail("landryndumate@gmail.com");
		user5.setEmailVerified(true);
		user5.setEnabled(true);
		user5.setName("Landry1");
		user5.setFirstName("Landry1");
		user5.setLastName("Ndumate1");
		user5.setUsername("landry1");
		user5.setUserId("Landry1");
		user5.setType(ProfileType.ADMINISTRATOR);
		user5.setPassword("12345678");
		user5.setProfileListChangeHandler(BuildUserProfiles(user5));
		users.add(user5);
		
		User user6 = new User();
		user6.setClient(null);
		user6.setCreationDate(null);
		user6.setDefaultLanguage("Fr");
		user6.setEmail("charles@moriset.com");
		user6.setEmailVerified(true);
		user6.setEnabled(true);
		user6.setName("charles1");
		user6.setFirstName("charles1");
		user6.setLastName("Moi1");
		user6.setUsername("charles1");
		user6.setUserId("charles");
		user6.setType(ProfileType.SUPERUSER);
		user6.setPassword("12345678");
		user6.setProfileListChangeHandler(BuildUserProfiles(user6));
		users.add(user6);
		
		User user7 = new User();
		user7.setClient(null);
		user7.setCreationDate(null);
		user7.setDefaultLanguage("Fr");
		user7.setEmail("fabiola.diondou@gmail.com");
		user7.setEmailVerified(true);
		user7.setEnabled(true);
		user7.setName("super1");
		user7.setFirstName("super1");
		user7.setLastName("super1");
		user7.setUsername("super1");
		user7.setUserId("super1");
		user7.setType(ProfileType.SUPERUSER);
		user7.setPassword("12345678");
		user7.setProfileListChangeHandler(BuildUserProfiles(user7));
		users.add(user7);
		
		User user8 = new User();
		user8.setClient(null);
		user8.setCreationDate(null);
		user8.setEmail("eemmeni@moriset.com");
		user8.setEmailVerified(true);
		user8.setEnabled(true);
		user8.setName("kompressor1");
		user8.setFirstName("kompressor1");
		user8.setLastName("emmaus1");
		user8.setUsername("emmaus1");
		user8.setUserId("kompressor1");
		user8.setType(ProfileType.USER);
		user8.setPassword("12345678");
		user8.setProfileListChangeHandler(BuildUserProfiles(user8));
		users.add(user8);
		
		User user9 = new User();
		user9.setClient(null);
		user9.setCreationDate(null);
		user9.setDefaultLanguage("Fr");
		user9.setEmail("user70@gmail.com");
		user9.setEmailVerified(true);
		user9.setEnabled(true);
		user9.setName("user701");
		user9.setFirstName("user701");
		user9.setLastName("user1");
		user9.setUsername("user1");
		user9.setUserId("user701");
		user9.setType(ProfileType.USER);
		user9.setPassword("12345678");
		user9.setProfileListChangeHandler(BuildUserProfiles(user9));
		users.add(user9);
		
		User user10 = new User();
		user10.setClient(null);
		user10.setCreationDate(null);
		user10.setDefaultLanguage("");
		user10.setEmail(null);
		user10.setEmailVerified(true);
		user10.setEnabled(true);
		user10.setName("w1");
		user10.setFirstName("w1");
		user10.setLastName("");
		user10.setUsername("w1");
		user10.setUserId("w1");
		user10.setType(ProfileType.USER);
		user10.setPassword("12345678");
		user10.setProfileListChangeHandler(BuildUserProfiles(user10));
		users.add(user10);
		
		
		return users;
	}
	
	public static ListChangeHandler<Nameable> BuildUserProfiles(User user) {
		
		 ListChangeHandler<Nameable> listchangehandler = new ListChangeHandler<Nameable>();
		 
		 for(int i = 1 ; i< 5; i++) {
			 
			 Nameable name = new Nameable(null, user.getName().concat("" +i));
			 name.setCode(user.getName().concat("" +i));
			 listchangehandler.addNew(name);
		 }
		
		 return listchangehandler;
	}
		
		
	
}
