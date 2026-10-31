document.addEventListener("DOMContentLoaded", async () => {
	await userReady;
	if (!currentUser) return;

	const isAdmin = currentUser.role === "ADMIN";
	const voteCard = document.getElementById("voteCard");
	const votesSection = document.getElementById("votesSection");

	if (isAdmin) {
		// Admin cannot vote; admin can see who voted for whom
		voteCard.style.display = "none";
		votesSection.style.display = "block";
		loadVotes();
	} else {
		loadCandidates();
		document.getElementById("voterName").textContent = currentUser.name;
		if (currentUser.hasVoted) {
			markAlreadyVoted();
		}
	}
});

function markAlreadyVoted() {
	document.getElementById("voteForm").style.display = "none";
	document.getElementById("alreadyVoted").style.display = "block";
}

// Load candidates for voting
function loadCandidates() {
	api("/api/candidate")
		.then((data) => {
			const candidateSelect = document.getElementById("candidateSelect");
			candidateSelect.innerHTML = '<option value="">Select a Candidate</option>';
			data.forEach((candidate) => {
				const option = document.createElement("option");
				option.value = candidate.id;
				option.textContent = `${candidate.name} (${candidate.party})`;
				candidateSelect.appendChild(option);
			});
		})
		.catch(() => showAlert("Error", "Failed to load candidates.", "error"));
}

// Load votes (admin only)
function loadVotes() {
	api("/api/votes")
		.then((data) => {
			const votesTable = document.getElementById("votesTable");
			votesTable.innerHTML = "";
			data.forEach((vote) => {
				votesTable.innerHTML += `<tr>
					<td>${vote.voterId}</td>
					<td>${vote.candidateId ?? "-"}</td>
				</tr>`;
			});
		})
		.catch(() => showAlert("Error", "Failed to load votes.", "error"));
}

// Cast vote (the voter is the logged-in user, so no voter id is sent)
document.getElementById("voteForm")?.addEventListener("submit", function (event) {
	event.preventDefault();

	const candidateId = document.getElementById("candidateSelect").value;
	if (!candidateId) {
		showAlert("Warning", "Please select a candidate.", "warning");
		return;
	}

	api("/api/votes/cast", {
		method: "POST",
		body: JSON.stringify({ candidateId: Number(candidateId) }),
	})
		.then((obj) => {
			showAlert("Success", obj.message, "success");
			markAlreadyVoted();
		})
		.catch((error) => showAlert("Error", error.message, "error"));
});
