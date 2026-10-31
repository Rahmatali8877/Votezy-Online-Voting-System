// Login and registration forms
document.getElementById("loginForm")?.addEventListener("submit", async (event) => {
	event.preventDefault();
	const body = new URLSearchParams({
		email: document.getElementById("email").value,
		password: document.getElementById("password").value,
	});
	try {
		const res = await fetch("/api/auth/login", {
			method: "POST",
			headers: { "Content-Type": "application/x-www-form-urlencoded" },
			body,
		});
		if (!res.ok) throw new Error("Invalid email or password");
		const me = await (await fetch("/api/auth/me")).json();
		window.location.href = me.role === "ADMIN" ? "candidates.html" : "voting.html";
	} catch (error) {
		showAlert("Login failed", error.message, "error");
	}
});

document.getElementById("registerForm")?.addEventListener("submit", async (event) => {
	event.preventDefault();
	const voterData = {
		name: document.getElementById("name").value,
		email: document.getElementById("email").value,
		password: document.getElementById("password").value,
	};
	try {
		await api("/api/voters/register", { method: "POST", body: JSON.stringify(voterData) });
		await Swal.fire({
			title: "Registered!",
			text: "Your account is ready. Please log in.",
			icon: "success",
			confirmButtonColor: "#d9534f",
		});
		window.location.href = "login.html";
	} catch (error) {
		showAlert("Registration failed", error.message, "error");
	}
});
