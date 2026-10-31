package in.scalive.votezy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequestDTO {
	// The voter is taken from the logged-in session, not from the request.
	@NotNull(message = "Candidate Id is required")
	private Long candidateId;
}
