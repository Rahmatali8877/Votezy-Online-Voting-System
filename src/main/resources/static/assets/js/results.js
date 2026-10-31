document.getElementById("searchResultForm")?.addEventListener("submit", async (event) => {
	event.preventDefault();
	await userReady;

	const electionName = document.getElementById("electionName").value.trim();
	try {
		let data;
		if (currentUser.role === "ADMIN") {
			// Admin declares (or re-reads) the result
			const r = await api("/api/election-result/declare", {
				method: "POST",
				body: JSON.stringify({ electionName }),
			});
			data = { name: r.electionNameString, total: r.totalVotes, winner: r.winnerId, votes: r.winnerVotes };
		} else {
			// Voters can only view results that the admin already declared
			const all = await api("/api/election-result");
			const r = all.find((x) => x.electionName.toLowerCase() === electionName.toLowerCase());
			if (!r) throw new Error("Result for this election has not been declared yet.");
			data = { name: r.electionName, total: r.totalVotes, winner: r.winnerid, votes: null };
		}

		document.getElementById("resultsContainer").innerHTML = `
			<div class="card text-center shadow-lg p-4" style="width: 400px;">
				<h4 class="text-danger">${escapeHtml(data.name)}</h4>
				<p><strong>Total Votes:</strong> ${data.total}</p>
				<p><strong>Winner ID:</strong> ${data.winner}</p>
				${data.votes !== null ? `<p><strong>Votes Obtained:</strong> ${data.votes}</p>` : ""}
			</div>`;
	} catch (error) {
		showAlert("Error", error.message || "Election result not found.", "error");
	}
});
