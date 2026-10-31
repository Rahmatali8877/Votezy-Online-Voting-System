package in.scalive.votezy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.scalive.votezy.entity.Voter;

public interface VoterRepository extends JpaRepository<Voter,Long>{
	boolean existsByEmail(String email);
	Optional<Voter> findByEmail(String email);

}
