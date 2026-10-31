package in.scalive.votezy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.Vote;
import in.scalive.votezy.entity.Voter;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.repository.CandidateRepository;

@Service
public class CandidateService {
	private CandidateRepository candidateRepository;
	@Autowired
	public CandidateService(CandidateRepository candidateRepository) {
		this.candidateRepository = candidateRepository;
	}
	public Candidate addCandidate(Candidate candidate) {
		return candidateRepository.save(candidate);
	}
	public List<Candidate> getAllCandidates(){
		return candidateRepository.findAll();
	}
	public Candidate getCandidateById(Long id) {
		Candidate candidate = candidateRepository.findById(id).orElse(null);
		if(candidate == null) {
			throw new ResourceNotFoundException("Candidate with id: "+id+" not found");
		}
		return candidate;
	}
	public Candidate updatetCandidate(Long id,Candidate updatedCandidate) {
		Candidate candidate= getCandidateById(id);
		if(updatedCandidate.getName() != null) {
			candidate.setName(updatedCandidate.getName());
		}
		if(updatedCandidate.getParty() != null) {
			candidate.setParty(updatedCandidate.getParty());
		}
		return candidateRepository.save(candidate);		
	}
	public void deleteCandidate(Long id) {
		Candidate candidate= getCandidateById(id);
		List<Vote> votes=candidate.getVote();
		for(Vote v:votes) {
			v.setCandidate(null);
		}
		candidate.getVote().clear();
		candidateRepository.delete(candidate);
	}
	
	

}
