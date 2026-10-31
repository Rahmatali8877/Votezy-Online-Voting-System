package in.scalive.votezy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.scalive.votezy.entity.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
	List<Candidate> findAllByOrderByVoteCountDesc();

}
