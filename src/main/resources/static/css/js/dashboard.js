/* ==========================================================================
   CHRONOS COMMAND DECK | DASHBOARD CONTROLLER
   ========================================================================== */

const API_BASE = "http://localhost:8081/api";
const userEmail = localStorage.getItem("userEmail");

// --- INITIALIZATION ---
document.addEventListener("DOMContentLoaded", () => {
    // 1. Security Check
    if (!userEmail) {
        window.location.href = "login.html";
        return;
    }

    // 2. Initialize UI Elements
    typeWriterEffect("userEmailDisplay", userEmail); // Cool typing effect
    startCountdown(); // Start the timer
    loadVaultFiles(); // Fetch data

    // 3. Bind Event Listeners
    document.getElementById("btnCheckIn").addEventListener("click", handleCheckIn);
    document.getElementById("fileInput").addEventListener("change", handleUpload);
});

/* =========================================
   1. VISUAL EFFECTS (Typing & Timer)
   ========================================= */

// Simulates a retro terminal typing out the username
function typeWriterEffect(elementId, text) {
    const el = document.getElementById(elementId);
    el.innerText = "";
    let i = 0;
    const speed = 50; // ms per character

    function type() {
        if (i < text.length) {
            el.innerHTML += text.charAt(i);
            i++;
            setTimeout(type, speed);
        } else {
            el.innerHTML += "<span class='blink'>_</span>"; // Blinking cursor
        }
    }
    type();
}

// Simulates the Doomsday Clock
// Note: In a real app, you'd fetch the specific 'releaseDate' from the backend.
// Here, we simulate a 30-day countdown that resets visually.
let timeRemaining = 30 * 24 * 60 * 60 * 1000; // 30 Days in MS

function startCountdown() {
    const display = document.getElementById("countdown");

    setInterval(() => {
        timeRemaining -= 1000;

        // Math to calculate D:H:M:S
        const days = Math.floor(timeRemaining / (1000 * 60 * 60 * 24));
        const hours = Math.floor((timeRemaining % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((timeRemaining % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((timeRemaining % (1000 * 60)) / 1000);

        // Formatting (01d 05h 30m 10s)
        display.innerText =
            `${pad(days)}d ${pad(hours)}h ${pad(minutes)}m ${pad(seconds)}s`;
    }, 1000);
}

function pad(num) {
    return num.toString().padStart(2, '0');
}

/* =========================================
   2. VAULT LOGIC (Load & Upload)
   ========================================= */

async function loadVaultFiles() {
    const listContainer = document.getElementById("fileListContainer");
    listContainer.innerHTML = "<div class='loading-text'>ACCESSING ENCRYPTED SHARDS...</div>";

    try {
        // Artificial delay for "Hacking" effect
        await delay(800);

        const response = await fetch(`${API_BASE}/vault/list?email=${userEmail}`);
        const files = await response.json();

        listContainer.innerHTML = ""; // Clear loader

        if (files.length === 0) {
            listContainer.innerHTML = "<div class='empty-text'>// VAULT EMPTY //</div>";
            return;
        }

        // Staggered Animation: Files appear one by one
        files.forEach((file, index) => {
            setTimeout(() => {
                const item = document.createElement("div");
                item.className = "file-item fade-in-up"; // Uses CSS animation
                item.innerHTML = `
                    <div class="file-info">
                        <span class="icon">🔒</span>
                        <span class="name">${file.fileName}</span>
                    </div>
                    <button class="btn-download" onclick="downloadFile(${file.fileId})">
                        DECRYPT ⬇
                    </button>
                `;
                listContainer.appendChild(item);
            }, index * 150); // 150ms delay between each item
        });

    } catch (e) {
        listContainer.innerHTML = "<div class='error-text'>CONNECTION SEVERED</div>";
    }
}

async function handleUpload() {
    const fileInput = document.getElementById("fileInput");
    const uploadZone = document.querySelector(".upload-zone");

    if (fileInput.files.length === 0) return;

    // Visual Feedback
    const originalText = uploadZone.innerHTML;
    uploadZone.innerHTML = "<p class='blink'>ENCRYPTING & UPLOADING...</p>";
    uploadZone.style.borderColor = "var(--primary)";

    const formData = new FormData();
    formData.append("file", fileInput.files[0]);
    formData.append("email", userEmail);

    try {
        const response = await fetch(`${API_BASE}/vault/upload`, {
            method: "POST",
            body: formData
        });

        if (response.ok) {
            // Success
            uploadZone.innerHTML = "<p style='color:var(--primary)'>UPLOAD SECURE.</p>";
            setTimeout(() => {
                uploadZone.innerHTML = originalText;
                uploadZone.style.borderColor = "var(--border)";
                loadVaultFiles(); // Refresh list
            }, 1500);
        } else {
            throw new Error("Upload Failed");
        }
    } catch (e) {
        uploadZone.innerHTML = "<p style='color:var(--secondary)'>UPLOAD FAILED</p>";
        setTimeout(() => {
            uploadZone.innerHTML = originalText;
        }, 2000);
    }
}

// Trigger download in new window
window.downloadFile = function(fileId) {
    window.location.href = `${API_BASE}/vault/download/${fileId}?email=${userEmail}`;
}

/* =========================================
   3. HEARTBEAT LOGIC (The "I Am Alive" Button)
   ========================================= */

async function handleCheckIn() {
    const btn = document.getElementById("btnCheckIn");
    const statusDiv = document.getElementById("checkInStatus");

    // 1. Lock Interface
    btn.disabled = true;
    btn.innerText = "SCANNING...";
    statusDiv.innerHTML = "INITIATING BIOMETRIC HANDSHAKE...";
    statusDiv.style.color = "#fff";

    try {
        // 2. Artificial "Processing" Delay
        await delay(1500);

        // 3. API Call
        const response = await fetch(`${API_BASE}/auth/check-in?email=${userEmail}`, {
            method: "POST"
        });

        if (response.ok) {
            // 4. Success Animation
            statusDiv.innerHTML = "IDENTITY CONFIRMED. TIMER RESET.";
            statusDiv.style.color = "var(--primary)";
            btn.innerText = "ALIVE CONFIRMED";
            btn.style.borderColor = "var(--primary)";
            btn.style.boxShadow = "0 0 20px var(--primary)";

            // Reset countdown visually
            timeRemaining = 30 * 24 * 60 * 60 * 1000;

            // Reset button after 3 seconds
            setTimeout(() => {
                btn.disabled = false;
                btn.innerText = "I AM ALIVE";
                statusDiv.innerHTML = "";
                btn.style.boxShadow = "none";
            }, 3000);
        } else {
            throw new Error("Check-in Failed");
        }
    } catch (e) {
        statusDiv.innerHTML = "SYSTEM ERROR: UNABLE TO VERIFY";
        statusDiv.style.color = "var(--secondary)";
        btn.disabled = false;
        btn.innerText = "RETRY";
    }
}

/* =========================================
   4. UTILS
   ========================================= */

function delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

// Attach logout to window so onclick works in HTML
window.logout = function() {
    localStorage.removeItem("userEmail");
    document.body.style.opacity = "0"; // Fade out effect
    setTimeout(() => {
        window.location.href = "login.html";
    }, 500);
}