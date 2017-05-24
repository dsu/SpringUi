package pl.springui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		disableAll(http);
		disableForConsole(http);

		// http.authorizeRequests().antMatchers("/",
		// "/home").permitAll().antMatchers("/admin/**")
		// .access("hasRole('ADMIN')").antMatchers("/db/**").access("hasRole('ADMIN')
		// and hasRole('DBA')").and()
		// .formLogin().and().exceptionHandling().accessDeniedPage("/Access_Denied");

	}



	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
		// auth.inMemoryAuthentication().withUser("user").password("user").roles("USER");
		// auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
		// auth.inMemoryAuthentication().withUser("dba").password("dba").roles("ADMIN",
		// "DBA");
	}



	private void disableAll(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/*").permitAll();
	}



	private void disableForConsole(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/").permitAll().and().authorizeRequests()
		.antMatchers("/console/**").permitAll();

		http.csrf().disable();
		http.headers().frameOptions().disable();
	}
}