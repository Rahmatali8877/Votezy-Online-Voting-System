package in.scalive.votezy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.scalive.votezy.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long>{

}
