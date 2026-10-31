package in.scalive.votezy.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.repository.VoterRepository;

/** Loads a voter (or admin) from MySQL so Spring Security can check the login. */
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final VoterRepository voterRepository;

	public CustomUserDetailsService(VoterRepository voterRepository) {
		this.voterRepository = voterRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Voter voter = voterRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
		System.out.println("Loaded user: " + voter.getEmail());

		return User.withUsername(voter.getEmail())
				.password(voter.getPassword())   // BCrypt hash
				.roles(voter.getRole())          // "VOTER" or "ADMIN" (ROLE_ prefix is added automatically)
				.build();
	}
}
