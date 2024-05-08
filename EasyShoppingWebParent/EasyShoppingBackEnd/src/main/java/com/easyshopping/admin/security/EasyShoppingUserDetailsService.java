package com.easyshopping.admin.security;

import com.easyshopping.admin.user.repository.UserRepository;
import com.easyshopping.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class EasyShoppingUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.getUserByEmail(email);
		if (user != null) {
			return new EasyShoppingUserDetails(user);
		}
		throw new UsernameNotFoundException("Could not find user with email: " + email);
	}
}
