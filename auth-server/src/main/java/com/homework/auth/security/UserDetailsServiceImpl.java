package com.homework.auth.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private BCryptPasswordEncoder encoder;
	
	private List<UserData> users;
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		for (UserData user : getUsers()) {
			if (user.userName.equals(userName)) {
				return getUserCredentials(user);
			}
		}
		throw new UsernameNotFoundException("User: " + userName + " not found");
	}
	
	private User getUserCredentials(UserData user) {
		return new User(user.userName, user.password, getUserAuthorities(user));
	}
	
	private List<GrantedAuthority> getUserAuthorities(UserData user) {
		return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + user.role);
	}
	
	private List<UserData> getUsers() {
		if (users == null) {
			users = Arrays.asList(
				new UserData(1, "user", encoder.encode("user"), "USER"),
				new UserData(2, "admin", encoder.encode("admin"), "ADMIN"));
		}
		return users;
	}
}

class UserData {
	public Integer id;
	public String userName;
	public String password;
	public String role;

	public UserData(Integer id, String userName, String password, String role) {
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.role = role;
	}
}
