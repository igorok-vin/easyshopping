package com.easyshopping.admin.user.controller;

import com.easyshopping.admin.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class UserRestController {
	private UserService userService;

	@Autowired
	public UserRestController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/users/check_email")
	public String checkDuplicateEmail(@RequestParam(name = "email") String email, @RequestParam(name = "id", required = false) Integer id){
		return userService.isEmailUnique(id,email)? "OK": "Duplicated";
	}

	@GetMapping("/users/enabled")
	public String updateUserEnabledStat(@RequestParam(name="email") String email,
									  @RequestParam(name="status") String enabled, RedirectAttributes redirectAttributes) {

		userService.updateUserEnabledStatus(email, false);
		String status ="disabled";
		String message = "The user ID " + email + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return status;
	}
}
