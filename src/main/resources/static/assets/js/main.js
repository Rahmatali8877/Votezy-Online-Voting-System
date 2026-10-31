// ---------- Session / role handling ----------
const PUBLIC_PAGES = ["", "index.html", "login.html", "register.html"];
const ADMIN_ONLY_LINKS = ["voters.html", "candidates.html"];
const VOTER_ONLY_LINKS = [];
const currentPage = window.location.pathname.split("/").pop();

let currentUser = null;

// Resolves when the user check is finished (pages wait for it before calling the API)
const userReady = (async function () {
	try {
		const res = await fetch("/api/auth/me");
		if (res.ok) {
			currentUser = await res.json();
		}
	} catch (e) {
		console.error("Could not check login status", e);
	}

	if (!currentUser && !PUBLIC_PAGES.includes(currentPage)) {
		window.location.href = "login.html";
		return null;
	}
	// Logged-in users do not need the login / register pages
	if (currentUser && (currentPage === "login.html" || currentPage === "register.html")) {
		window.location.href = currentUser.role === "ADMIN" ? "candidates.html" : "voting.html";
		return currentUser;
	}
	// Voters cannot open admin pages
	if (currentUser && currentUser.role !== "ADMIN" && ADMIN_ONLY_LINKS.includes(currentPage)) {
		window.location.href = "voting.html";
		return currentUser;
	}
	return currentUser;
})();

document.addEventListener("DOMContentLoaded", async () => {
	await userReady;
	setupNavbar();
});

// Highlight active link, hide links the user may not use, add login/logout
function setupNavbar() {
	const navList = document.querySelector(".navbar-nav");
	if (!navList) return;

	navList.querySelectorAll(".nav-link").forEach((link) => {
		const href = link.getAttribute("href");
		const item = link.closest("li");

		if (!currentUser) {
			// Guests only see the public links
			if (ADMIN_ONLY_LINKS.includes(href) || href === "voting.html" || href === "results.html") {
				item.style.display = "none";
			}
		} else if (currentUser.role !== "ADMIN" && ADMIN_ONLY_LINKS.includes(href)) {
			item.style.display = "none";
		}

		link.classList.toggle("active", href === currentPage);
	});

	const extra = document.createElement("li");
	extra.className = "nav-item d-flex align-items-center ms-lg-3";
	if (currentUser) {
		extra.innerHTML = `
			<span class="text-white me-2">${escapeHtml(currentUser.name)} (${currentUser.role})</span>
			<button class="btn btn-sm btn-light" id="logoutBtn">Logout</button>`;
	} else {
		extra.innerHTML = `
			<a class="btn btn-sm btn-light me-2" href="login.html">Login</a>
			<a class="btn btn-sm btn-outline-light" href="register.html">Register</a>`;
	}
	navList.appendChild(extra);

	document.getElementById("logoutBtn")?.addEventListener("click", async () => {
		await fetch("/api/auth/logout", { method: "POST" });
		window.location.href = "login.html";
	});
}

// ---------- Helpers ----------
function escapeHtml(text) {
	const div = document.createElement("div");
	div.textContent = text ?? "";
	return div.innerHTML;
}

// Fetch wrapper: sends the session cookie, sends the user to login if the session expired,
// and turns error responses into Error(message).
async function api(url, options = {}) {
	const res = await fetch(url, {
		...options,
		headers: { "Content-Type": "application/json", ...(options.headers || {}) },
	});
	if (res.status === 401 && !PUBLIC_PAGES.includes(currentPage)) {
		window.location.href = "login.html";
		throw new Error("Please log in again.");
	}
	let body = null;
	const text = await res.text();
	if (text) {
		try {
			body = JSON.parse(text);
		} catch (e) {
			body = text;
		}
	}
	if (!res.ok) {
		let message = "Something went wrong!";
		if (body && typeof body === "object") {
			// error body is either {message} or a map of field -> validation message
			message = body.message || Object.values(body).join(", ") || message;
		} else if (typeof body === "string" && body) {
			message = body;
		}
		throw new Error(message);
	}
	return body;
}

// Function to show a SweetAlert notification
function showAlert(title, message, type = "info") {
	Swal.fire({
		title: title,
		text: message,
		icon: type,
		confirmButtonColor: "#d9534f",
	});
}
