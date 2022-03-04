package stocktales.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Profile("prod")
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	// add a reference to our security data source

	@Autowired
	@Qualifier("appDataSource")
	private DataSource securityDataSource;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception
	{

		// use jdbc authentication ... oh yeah!!!
		auth.jdbcAuthentication().dataSource(securityDataSource);

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{

		http.authorizeRequests().antMatchers("/ScSpFPools/*").hasRole("ADMIN").antMatchers("/fpconfig/*")
				.hasRole("ADMIN").antMatchers("/siteconfig/*").hasRole("ADMIN").antMatchers("/databook/scrip*")
				.hasRole("ADMIN").antMatchers("/scJournalM/*").hasRole("ADMIN").antMatchers("/")
				.hasAnyRole("ADMIN", "GUEST").antMatchers("/scrips*").hasAnyRole("GUEST", "ADMIN")
				.antMatchers("/admin/*").hasRole("ADMIN").antMatchers("/register/**").permitAll()
				.antMatchers("/resources/**").permitAll().and().formLogin().loginPage("/showMyLoginPage")
				.loginProcessingUrl("/authenticateTheUser").permitAll().and().logout().permitAll().and()
				.exceptionHandling().accessDeniedPage("/access-denied");

	}
}
