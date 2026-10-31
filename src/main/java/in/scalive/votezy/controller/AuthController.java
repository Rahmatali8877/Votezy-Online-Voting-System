package in.scalive.votezy.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.repository.VoterRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final VoterRepository voterRepository;

	public AuthController(VoterRepository voterRepository) {
		this.voterRepository = voterRepository;
	}

	// Tells the frontend who is logged in (name, email, role, hasVoted)
	@GetMapping("/me")
	public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
		Voter voter = voterRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return ResponseEntity.ok(Map.of(
				"id", voter.getId(),
				"name", voter.getName(),
				"email", voter.getEmail(),
				"role", voter.getRole(),
				"hasVoted", voter.isHasVoted()));
	}
}
