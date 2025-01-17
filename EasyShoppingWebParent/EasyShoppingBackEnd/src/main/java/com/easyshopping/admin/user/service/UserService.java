package com.easyshopping.admin.user.service;

import com.easyshopping.admin.user.repository.RoleRepository;
import com.easyshopping.admin.user.exception.UserNotFoundException;
import com.easyshopping.admin.user.repository.UserRepository;
import com.easyshopping.common.entity.Role;
import com.easyshopping.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
	public static final int USERS_PER_PAGE = 4;

	private UserRepository userRepository;
	private RoleRepository roleRepository;

	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public User getUserByEmail(String email) {
		return userRepository.getUserByEmail(email);
	}
	
	public List<User> listAll() {
		return (List<User>) userRepository.findAll();
	}

	public Page<User> listByPage(Integer pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
		if (keyword != null) {
			return userRepository.findAll(keyword, pageable);
		}
		return userRepository.findAll(pageable);
	}
	
	public List<Role> listRoles() {
		return (List<Role>) roleRepository.findAll();
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);
		if (isUpdatingUser) {
			User existingUser = userRepository.findById(user.getId()).get();
			if (user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			} else {
				encodePassword(user);
			}
		} else {		
			encodePassword(user);
		}
		return userRepository.save(user);
	}
	
	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepository.getUserByEmail(email);
		if (userByEmail == null) return true;
		if (userByEmail.getId() == id) return true;
		System.out.println("User service: " +id+"  "+ email);
		return false;
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
		userRepository.deleteById(id);
	}
	
	public void updateUserEnabledStatus(String email, boolean enabled) {
		userRepository.updateEnabledStatus(email, enabled);
	}

	public void updateUserEnabled(Integer id, boolean enabled) {
		userRepository.updateEnabled(id, enabled);
	}

	public User updateUserAccount(User userInForm) {
		User userInDatabase =  userRepository.findById(userInForm.getId()).get();
		if(!userInForm.getPassword().isEmpty()){
			userInDatabase.setPassword(userInForm.getPassword());
			encodePassword(userInDatabase);
		}
		if (userInForm.getPhoto() !=null) {
			userInDatabase.setPhoto(userInForm.getPhoto());
		}
		userInDatabase.setFirstName(userInForm.getFirstName());
		userInDatabase.setLastName(userInForm.getLastName());
		return userRepository.save(userInDatabase);
	}
}
