package in.scalive.votezy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class Voter {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message="Name is required")
	private String name;
	
	@NotBlank(message="Email is required")
	@Email(message="Invalid mail format")
	private String email;
	
	// Stored as a BCrypt hash. Accepted from JSON on registration, never sent back in a response.
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(message="Password is required")
	@Size(min = 6, message="Password must be at least 6 characters")
	@ToString.Exclude
	private String password;
	
	// "VOTER" or "ADMIN". Ignored in JSON so nobody can make themselves admin through the API.
	@JsonIgnore
	private String role = "VOTER";
	
	private boolean hasVoted=false;
	@OneToOne(mappedBy="voter" ,cascade = CascadeType.ALL)
	@JsonIgnore
	@ToString.Exclude
	private Vote vote;

}
