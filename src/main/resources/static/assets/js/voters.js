// Admin page: list and delete voters
document.addEventListener("DOMContentLoaded", async () => {
	await userReady;
	if (currentUser && currentUser.role === "ADMIN") loadVoters();
});

function loadVoters() {
	api("/api/voters")
		.then((data) => {
			const votersContainer = document.getElementById("votersContainer");
			votersContainer.innerHTML = "";
			data.forEach((voter) => {
				const isAdmin = voter.role === "ADMIN";
				votersContainer.innerHTML += `
					<tr>
						<td>${voter.id}</td>
						<td>${escapeHtml(voter.name)}</td>
						<td>${escapeHtml(voter.email)}</td>
						<td>${voter.hasVoted ? "Yes" : "No"}</td>
						<td><button class="btn btn-danger btn-sm" onclick="deleteVoter(${voter.id})">Delete</button></td>
					</tr>`;
			});
		})
		.catch((error) => showAlert("Error", error.message || "Failed to load voters.", "error"));
}

function deleteVoter(id) {
	Swal.fire({
		title: "Are you sure?",
		text: "This voter will be permanently deleted.",
		icon: "warning",
		showCancelButton: true,
		confirmButtonColor: "#d9534f",
		cancelButtonColor: "#6c757d",
		confirmButtonText: "Yes, delete it!",
	}).then((result) => {
		if (result.isConfirmed) {
			api(`/api/voters/delete/${id}`, { method: "DELETE" })
				.then(() => {
					showAlert("Deleted!", "Voter has been removed.", "success");
					loadVoters();
				})
				.catch((error) => showAlert("Error", error.message, "error"));
		}
	});
}
