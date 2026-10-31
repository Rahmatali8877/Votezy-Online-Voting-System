package in.scalive.votezy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import in.scalive.votezy.dto.ElectionResultRequestDTO;
import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.ElectionResult;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.repository.CandidateRepository;
import in.scalive.votezy.repository.ElectionResultRepository;
import in.scalive.votezy.repository.VoteRepository;
import jakarta.validation.Valid;

@Service
public class ElectionResultService {
	private CandidateRepository candidateRepository;
	private ElectionResultRepository electionResultRepository;
	private VoteRepository voteRepository;
	
	public ElectionResultService(CandidateRepository candidateRepository,
			ElectionResultRepository electionResultRepository, VoteRepository voteRepository) {
		this.candidateRepository = candidateRepository;
		this.electionResultRepository = electionResultRepository;
		this.voteRepository = voteRepository;
	}
	public ElectionResult declareResult(String electionName) {
		Optional<ElectionResult> existingResult = electionResultRepository.findByElectionName(electionName);
		if(existingResult.isPresent()) {
			return existingResult.get();
		}
		if(voteRepository.count()==0) {
			throw new IllegalStateException("Cannot declare Result as no vote have been casted");
		}
		List<Candidate> allCandidates = candidateRepository.findAllByOrderByVoteCountDesc();
		if(allCandidates.isEmpty()) {
			throw new ResourceNotFoundException("No Candidates available");
		}
		Candidate winner = allCandidates.get(0);
		int totalVotes = 0;
		for(Candidate candidate : allCandidates) {
			totalVotes +=candidate.getVoteCount();
		}
		ElectionResult electionResult = new ElectionResult();
		electionResult.setElectionName(electionName);
		electionResult.setTotalVotes(totalVotes);
		electionResult.setWinner(winner);
		return electionResultRepository.save(electionResult);
	}
	
	public List<ElectionResult> getAllResults(){
		return electionResultRepository.findAll();
	}
	
	
	

}
