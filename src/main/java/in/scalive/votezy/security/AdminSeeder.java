package in.scalive.votezy.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.repository.VoterRepository;

/** Creates the first ADMIN account at startup (only if it does not exist yet). */
@Component
public class AdminSeeder implements CommandLineRunner {

	private final VoterRepository voterRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${votezy.admin.name}")
	private String adminName;
	@Value("${votezy.admin.email}")
	private String adminEmail;
	@Value("${votezy.admin.password}")
	private String adminPassword;

	public AdminSeeder(VoterRepository voterRepository, PasswordEncoder passwordEncoder) {
		this.voterRepository = voterRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		if (voterRepository.existsByEmail(adminEmail)) {
			return;
		}
		Voter admin = new Voter();
		admin.setName(adminName);
		admin.setEmail(adminEmail);
		admin.setPassword(passwordEncoder.encode(adminPassword));
		admin.setRole("ADMIN");
		voterRepository.save(admin);
		System.out.println("Admin account created: " + adminEmail);
	}
}
