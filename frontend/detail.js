const API_URL = "http://localhost:8080/api/exchange-rates?usedb=true";

const params = new URLSearchParams(window.location.search);
const code = params.get("code");

async function loadDetail() {
    const container = document.getElementById("detail");

    try {
        const response = await fetch(API_URL);
        const rates = await response.json();
        const rate = rates.find(r => r.shortName === code);

        if (!rate) {
            container.textContent = "Rate not found: " + code;
            return;
        }

        container.innerHTML = `
            <h1>${rate.shortName} — ${rate.name}</h1>
            <p><strong>Country:</strong> ${rate.country}</p>
            <p><strong>Amount:</strong> ${rate.amount}</p>
            <p><strong>Buy:</strong> ${rate.valBuy}</p>
            <p><strong>Sell:</strong> ${rate.valSell}</p>
            <p><strong>Mid:</strong> ${rate.valMid}</p>
            <p><strong>Valid from:</strong> ${rate.validFrom}</p>
        `;
    } catch (error) {
        container.textContent = "Could not reach the backend. Is it running on port 8080?";
    }
}

loadDetail();
