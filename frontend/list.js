const API_URL = "http://localhost:8080/api/exchange-rates?usedb=false";

async function loadRates() {
    const status = document.getElementById("status");
    const table = document.getElementById("rates");
    const tbody = document.getElementById("rates-body");

    try {
        const response = await fetch(API_URL);
        if (!response.ok) {
            status.textContent = "Failed to load rates (HTTP " + response.status + ")";
            return;
        }

        const rates = await response.json();

        for (const rate of rates) {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td><a href="detail.html?code=${rate.shortName}">${rate.shortName}</a></td>
                <td>${rate.name}</td>
                <td>${rate.country}</td>
                <td>${rate.valBuy}</td>
                <td>${rate.valSell}</td>
                <td>${rate.valMid}</td>
            `;
            tbody.appendChild(row);
        }

        status.hidden = true;
        table.hidden = false;
    } catch (error) {
        status.textContent = "Could not reach the backend";
    }
}

loadRates();
