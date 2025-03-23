async function loadReport() {
    const start = document.getElementById('startDate').value;
    const end = document.getElementById('endDate').value;

    const url = new URL('http://localhost:8090/api/v1/cards/relatorio/concluido');
    url.searchParams.append('inicio', start);
    url.searchParams.append('fim', end);

    try {
        const response = await fetch(url);
        const cards = await response.json();
        renderReport(cards);
    } catch (error) {
        console.error('Erro ao carregar relatório:', error);
    }
}

function renderReport(cards) {
    const container = document.getElementById('reportContent');
    container.innerHTML = `
        <h3>Relatório: ${cards.length} cards encontrados</h3>
        ${cards.map(card => `
            <div class="report-card">
                <h4>${card.titulo}</h4>
                <p>Concluído em: ${new Date(card.dataConclusao).toLocaleString()}</p>
            </div>
        `).join('')}
    `;
}

function exportReport() {
    const content = document.getElementById('reportContent').textContent;
    const blob = new Blob([content], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `relatorio-${new Date().toISOString()}.txt`;
    a.click();
} 