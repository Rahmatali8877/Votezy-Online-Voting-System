package in.scalive.votezy.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Session-based authentication with role-based access control (VOTER, ADMIN).
 * Login/logout are plain JSON endpoints so the HTML/JS frontend can call them with fetch().
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
			// Session cookie is SameSite=Lax (see application.properties), which blocks
			// cross-site POST requests. For a production app, enable CSRF tokens as well.
			.csrf(csrf -> csrf.disable())

			.authorizeHttpRequests(auth -> auth
				// Static pages and assets (the data itself is protected by the API rules below)
				.requestMatchers("/", "/*.html", "/assets/**", "/favicon.ico", "/error").permitAll()

				// Public API: registration only (login is handled by formLogin below)
				.requestMatchers(HttpMethod.POST, "/api/voters/register").permitAll()

				// Voters: admin only (list, view, update, delete)
				.requestMatchers("/api/voters/**").hasRole("ADMIN")

				// Candidates: any logged-in user can view, only ADMIN can change
				.requestMatchers(HttpMethod.GET, "/api/candidate", "/api/candidate/**").authenticated()
				.requestMatchers("/api/candidate/**").hasRole("ADMIN")

				// Voting: only VOTER can cast; only ADMIN can see the list of votes
				.requestMatchers(HttpMethod.POST, "/api/votes/cast").hasRole("VOTER")
				.requestMatchers("/api/votes/**", "/api/votes").hasRole("ADMIN")

				// Results: any logged-in user can view, only ADMIN can declare
				.requestMatchers(HttpMethod.GET, "/api/election-result").authenticated()
				.requestMatchers("/api/election-result/**").hasRole("ADMIN")

				// Everything else (including /api/auth/me) needs login
				.anyRequest().authenticated()
			)

			.formLogin(form -> form
				.loginProcessingUrl("/api/auth/login")
				.usernameParameter("email")
				.passwordParameter("password")
				.successHandler((request, response, authentication) -> {
					response.setStatus(HttpStatus.OK.value());
					response.setContentType("application/json");
					response.getWriter().write("{\"message\":\"Login successful\"}");
				})
				.failureHandler((request, response, exception) -> {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setContentType("application/json");
					response.getWriter().write("{\"message\":\"Invalid email or password\"}");
				})
				.permitAll()
			)

			.logout(logout -> logout
				.logoutUrl("/api/auth/logout")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.logoutSuccessHandler((request, response, authentication) -> {
					response.setStatus(HttpStatus.OK.value());
					response.setContentType("application/json");
					response.getWriter().write("{\"message\":\"Logged out\"}");
				})
				.permitAll()
			)

			.exceptionHandling(ex -> ex
				// Not logged in -> 401 (the frontend redirects to login.html)
				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
				// Logged in but wrong role -> 403
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					response.setStatus(HttpStatus.FORBIDDEN.value());
					response.setContentType("application/json");
					response.getWriter().write("{\"message\":\"You do not have permission to do this\"}");
				})
			);

		return http.build();
	}

	// Passwords are stored as BCrypt hashes, never as plain text
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
