package org.serratec.viroumemeapi.config;

import java.util.Arrays;

import org.serratec.viroumemeapi.security.AuthService;
import org.serratec.viroumemeapi.security.JWTAutheticationFilter;
import org.serratec.viroumemeapi.security.JWTAuthorizationFilter;
import org.serratec.viroumemeapi.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	AuthService service;

	@Autowired
	JWTUtil jwtUtil;

	private static final String[] AUTH_WHITLIST = { "/swagger-ui/**", "/create", "/categoria/**", "/produto/**",
			"/v3/api-docs/**"};

	private static final String[] AUTH_WHITLIST2 = { "/pedido/**" };

	private static final String[] AUTH_WHITLIST3 = { "/endereco/**" };

	// private static final String[] PUBLIC_MATCHERS = { "/" };

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable();
		http.authorizeRequests().antMatchers(AUTH_WHITLIST).permitAll().antMatchers(HttpMethod.GET, AUTH_WHITLIST2)
				.permitAll().antMatchers(HttpMethod.DELETE, AUTH_WHITLIST2).permitAll()
				.antMatchers(HttpMethod.GET, AUTH_WHITLIST3).permitAll().antMatchers(HttpMethod.DELETE, AUTH_WHITLIST3)
				.permitAll().antMatchers(HttpMethod.POST, AUTH_WHITLIST3).permitAll().anyRequest().authenticated();
		http.addFilterBefore(new JWTAutheticationFilter(authenticationManager(), jwtUtil),
				UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
		configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(service).passwordEncoder(bCryptPasswordEncoder());
	}


	// @Bean
	// public CorsConfigurationSource corsConfigurationSource() {
	// CorsConfiguration configuration = new CorsConfiguration();
	// configuration.setAllowedOrigins(Arrays.asList("*"));
	// configuration.setAllowedMethods(Arrays.asList("GET","POST", "OPTIONS"));
	// UrlBasedCorsConfigurationSource source = new
	// UrlBasedCorsConfigurationSource();
	// source.registerCorsConfiguration("/**", configuration);
	// return source;
	// }

	/*
	 * 
	 * @Configuration
	 * 
	 * @EnableWebSecurity public class SecurityConfig extends
	 * WebSecurityConfigurerAdapter {
	 * 
	 * @Autowired private Environment env;
	 * 
	 * private static final String[] PUBLIC_MATCHERS = { "/" };
	 * 
	 * @Override protected void configure(HttpSecurity http) throws Exception {
	 * 
	 * // H2 if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
	 * http.headers().frameOptions().disable(); }
	 * 
	 * http.cors().and().csrf().disable();
	 * http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.
	 * STATELESS);
	 * http.authorizeRequests().antMatchers(PUBLIC_MATCHERS).permitAll(); }
	 * 
	 * @Bean CorsConfigurationSource corsConfigurationSource() { CorsConfiguration
	 * configuration = new CorsConfiguration().applyPermitDefaultValues();
	 * configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE",
	 * "OPTIONS")); final UrlBasedCorsConfigurationSource source = new
	 * UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/",
	 * configuration); return source; } }
	 */
}