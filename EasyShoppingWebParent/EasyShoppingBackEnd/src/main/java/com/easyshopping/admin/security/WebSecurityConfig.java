package com.easyshopping.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Bean
	public UserDetailsService userDetailsService() {
		return new EasyShoppingUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/users/**").hasAuthority("Admin")/*доступ до /users/ буде тільки в Admin якщо навіть ввести URL забороненої сторінки напряму*/
				.antMatchers("/categories/**").hasAnyAuthority("Admin","Editor")
				.antMatchers("/brands/**").hasAnyAuthority("Admin","Editor")
				.antMatchers("/products/edit/**","/products/","/products/save","/products/check_unique").hasAnyAuthority("Admin","Editor","Salesperson")
				.antMatchers("/products/new","/products/delete/**","/products/page/**").hasAnyAuthority("Admin","Editor")
				.antMatchers("/products","/products/","/products/detail/**","/products/page/**").hasAnyAuthority("Admin","Editor","Salesperson","Shipper")
				.antMatchers("/products/**").hasAnyAuthority("Admin","Editor")
				.antMatchers("/questions/**").hasAnyAuthority("Admin","Assistant")
				.antMatchers("/reviews/**").hasAnyAuthority("Admin","Assistant")
				.antMatchers("/customers/**").hasAnyAuthority("Admin","Salesperson")
				.antMatchers("/shipping/**").hasAnyAuthority("Admin","Salesperson")
				.antMatchers("/orders/**").hasAnyAuthority("Admin","Salesperson","Shipper")
				.antMatchers("/reports/**").hasAnyAuthority("Admin","Salesperson")
				.antMatchers("/articles/**").hasAnyAuthority("Admin","Editor")
				.antMatchers("/menus/**").hasAnyAuthority("Admin","Editor")
				.antMatchers("/settings/**").hasAnyAuthority("Admin")
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/login")
				.usernameParameter("email")
				.permitAll()
				.and()
				.logout()
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/login?logout").permitAll()
				.deleteCookies("JSESSIONID")
				.and()
				.rememberMe().alwaysRemember(true).key("asdfgASDFG_1234567890").tokenValiditySeconds(7*24*60*60);
	}

	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		webSecurity.ignoring().antMatchers("/image/**","/js/**","/icon/**","/fontawesome/**","/webfonts/**","/webjars/**","/style.css");
	}
}
