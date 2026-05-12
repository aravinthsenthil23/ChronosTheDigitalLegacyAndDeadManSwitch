/* ==========================================================================
   CHRONOS SECURITY PROTOCOL | AUTHENTICATION HANDLER
   ========================================================================== */

const API_BASE_URL = "http://localhost:8081/api/auth";

document.addEventListener("DOMContentLoaded", () => {
    console.log("SYSTEM ONLINE: Auth Module Loaded.");

    // Detect which page we are on
    const loginForm = document.getElementById("loginForm");
    const signupForm = document.getElementById("signupForm");

    if (loginForm) initLogin(loginForm);
    if (signupForm) initSignup(signupForm);
});

/* =========================================
   1. LOGIN LOGIC (The Biometric Scan)
   ========================================= */
function initLogin(form) {
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const emailInput = document.getElementById("loginEmail");
        const passInput = document.getElementById("loginPass");
        const btn = document.getElementById("btnSubmit");
        const status = document.getElementById("statusText");
        const card = document.getElementById("loginCard");

        // 1. VISUALS: Start "Scanning"
        const originalBtnText = btn.innerText;
        btn.disabled = true;
        btn.innerHTML = "SCANNING BIOMETRICS...";
        btn.style.opacity = "0.8";
        status.className = "status-msg";
        status.innerText = "ESTABLISHING SECURE HANDSHAKE...";

        // 2. ARTIFICIAL DELAY (For that sci-fi "processing" feel)
        await delay(1200);

        const loginData = {
            email: emailInput.value,
            password: passInput.value
        };

        try {
            // 3. API CALL
            const response = await fetch(`${API_BASE_URL}/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(loginData)
            });

            if (response.ok) {
                // SUCCESS SEQUENCE
                status.innerText = "IDENTITY VERIFIED. ACCESS GRANTED.";
                status.classList.add("success");
                btn.innerHTML = "INITIATING SESSION...";

                // Save Session
                localStorage.setItem("userEmail", loginData.email);

                // Redirect Animation
                setTimeout(() => {
                    window.location.href = "dashboard.html";
                }, 1000);
            } else {
                throw new Error("INVALID CREDENTIALS");
            }

        } catch (error) {
            // FAILURE SEQUENCE
            status.innerText = "ACCESS DENIED: " + error.message;
            status.classList.add("error");

            // Reset Button
            btn.disabled = false;
            btn.innerHTML = "RETRY AUTHENTICATION";
            btn.style.opacity = "1";

            // Trigger Shake Animation
            shakeElement(card);
        }
    });
}

/* =========================================
   2. SIGNUP LOGIC (The Vault Initialization)
   ========================================= */
function initSignup(form) {
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const btn = document.getElementById("btnSubmit");
        const status = document.getElementById("statusText");
        const card = document.getElementById("signupCard");

        // 1. VISUALS: Start "Encryption"
        btn.disabled = true;
        btn.innerHTML = "ENCRYPTING DATA SHARDS...";
        btn.style.opacity = "0.7";
        status.className = "status-msg";
        status.innerText = "ALLOCATING SECURE STORAGE...";

        // 2. GATHER DATA
        const formData = {
            email: document.getElementById("email").value,
            passwordHash: document.getElementById("password").value, // Backend will hash this
            nomineeName: document.getElementById("nomineeName").value,
            nomineeEmail: document.getElementById("nomineeEmail").value,
            checkInThreshold: parseInt(document.getElementById("threshold").value)
        };

        // Artificial Delay
        await delay(1500);

        try {
            // 3. API CALL
            const response = await fetch(`${API_BASE_URL}/signup`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData)
            });

            const message = await response.text();

            if (response.ok) {
                // SUCCESS SEQUENCE
                status.innerText = "VAULT INITIALIZED SUCCESSFULLY.";
                status.classList.add("success");
                btn.innerHTML = "PROTOCOL ENGAGED";
                btn.style.background = "var(--primary)";
                btn.style.color = "#000";

                setTimeout(() => {
                    window.location.href = "login.html";
                }, 2000);
            } else {
                throw new Error(message.includes("Email") ? "EMAIL ALREADY REGISTERED" : "REGISTRATION FAILED");
            }

        } catch (error) {
            // FAILURE SEQUENCE
            status.innerText = "ERROR: " + error.message;
            status.classList.add("error");

            btn.disabled = false;
            btn.innerHTML = "RETRY INITIALIZATION";
            btn.style.opacity = "1";

            // Trigger Shake Animation
            shakeElement(card);
        }
    });
}

/* =========================================
   3. HELPER FUNCTIONS
   ========================================= */

// Simple promise-based delay
function delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

// Adds 'shake' class and removes it after animation plays
function shakeElement(element) {
    element.classList.add("shake");
    setTimeout(() => {
        element.classList.remove("shake");
    }, 500); // Matches CSS animation duration
}