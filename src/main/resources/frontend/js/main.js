let draggedCard = null;

function showCardForm() {
    document.getElementById('cardForm').style.display = 'block';
}

function closeCardForm() {
    document.getElementById('cardForm').style.display = 'none';
    // Limpa os campos do formulário
    document.getElementById('cardTitle').value = ''; // Limpa o campo de título
    document.getElementById('cardDescription').innerHTML = ''; // Limpa o campo de descrição
}

async function createCard() {
    const title = document.getElementById('cardTitle').value;
    const description = document.getElementById('cardDescription').innerHTML;

    try {
        const response = await fetch('http://localhost:8090/api/v1/cards', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                titulo: title,
                descricao: description
            })
        });

        if (response.ok) {
            closeCardForm();
            loadCards();
        }
    } catch (error) {
        console.error('Erro ao criar card:', error);
    }
}

// Carrega os card nas respectivas colunas.
async function loadCards() {
    const columns = document.querySelectorAll('.column');
    columns.forEach(async column => {
        const status = column.dataset.status;
        const response = await fetch(`http://localhost:8090/api/v1/cards?status=${status}`);
        const cards = await response.json();
        renderCards(column.querySelector('.cards'), cards);
    });
}

// Função para renderizar os cards nas respectivas colunas
function renderCards(container, cards) {
    container.innerHTML = cards.map(card => `
        <div class="card" draggable="true" data-id="${card.id}" 
                                        ondragstart="dragStart(event)"
                                        ondblclick="showCardDetail(${card.id})">
            <div class="card-header">
                <div class="card-title">
                    <h3>${card.titulo}</h3>
                    <small>${new Date(card.dataCriacao).toLocaleDateString()}</small>
                </div>
                <div class="card-actions">
                    <button class="btn-icon" onclick="toggleEdit(this)">✎</button>
                    <button class="btn-icon expand-btn" onclick="toggleExpand(this)">▼</button>
                </div>
            </div>
            <div class="card-content">
                <div class="description">${card.descricao}</div>
            </div>
        </div>
    `).join('');
}

// Drag and Drop
function dragStart(e) {
    const card = e.target.closest('.card');
    if (card) {
        e.dataTransfer.setData('text/plain', card.dataset.id);
        card.classList.add('dragging');
    }
}

async function allowDrop(e) {
    e.preventDefault();
}

async function drop(e) {
    e.preventDefault();

    const newColumn = e.target.closest('.column');
    if (!newColumn) {
        console.error('Coluna de destino não encontrada');
        return;
    }

    // Obtém o novo status da coluna
    const newStatus = newColumn.dataset.status;
    if (!newStatus) {
        console.error('Status da coluna não encontrado');
        return;
    }
    
   const cardId = e.dataTransfer.getData('text/plain');
    if (!cardId) {
        console.error('ID não encontrado...');
        return;
    }

    // Encontra o card no DOM
    const cardElement = document.querySelector(`.card[data-id="${cardId}"]`);
    if (!cardElement) {
        console.error(`Card com ID ${cardId} não encontrado`);
        return;
    }

    try {
        const response = await fetch(`http://localhost:8090/api/v1/cards/${cardId}/move?newStatus=${newStatus}`, {
            method: 'PUT'
        });

        if (response.ok) {
            //loadCards();
            // Move visualmente o card para a nova coluna
            cardElement.remove();
            newColumn.querySelector('.cards').appendChild(cardElement);

            // Atualiza o status no card
            cardElement.dataset.status = newStatus;

            // Feedback visual
            showNotification('Card movido com sucesso!');
        } else {
            showNotification('Erro ao mover card', true);
        }
    } catch (error) {
        console.error('Erro ao mover card:', error);
        showNotification('Erro de conexão', true);
    } finally {
        // Remove classes de feedback visual
        cardElement.classList.remove('dragging');
        newColumn.classList.remove('over');
    }
}

// Expansão da descrição
function toggleDescription(btn) {
    const card = btn.closest('.card');
    const description = card.querySelector('.description');
    btn.classList.toggle('open');
    description.style.display = description.style.display === 'none' ? 'block' : 'none';
}


// Permitir colar imagens
document.querySelectorAll('.description').forEach(editor => {
    editor.addEventListener('paste', (e) => {
        const items = (e.clipboardData || window.clipboardData).items;
        for (const item of items) {
            if (item.type.indexOf('image') === 0) {
                e.preventDefault();
                const blob = item.getAsFile();
                const reader = new FileReader();
                reader.onload = (event) => {
                    const img = document.createElement('img');
                    img.src = event.target.result;
                    editor.appendChild(img);
                };
                reader.readAsDataURL(blob);
            }
        }
    });
});

// Feedback visual
function showNotification(text, isError = false) {
    const notification = document.createElement('div');
    notification.textContent = text;
    notification.style.position = 'fixed';
    notification.style.bottom = '20px';
    notification.style.right = '20px';
    notification.style.padding = '1rem';
    notification.style.background = isError ? '#ff4444' : '#4CAF50';
    notification.style.color = 'white';
    notification.style.borderRadius = '4px';

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Alternar expansão
function toggleExpand(btn) {
    const card = btn.closest('.card');
    card.classList.toggle('expanded');
    btn.textContent = card.classList.contains('expanded') ? '▲' : '▼';
}

// Função para alternar edição
function toggleEdit(btn) {
    const card = btn.closest('.card');
    const description = card.querySelector('.description');

    if (!description.hasAttribute('contenteditable')) {
        description.setAttribute('contenteditable', 'true');
        description.focus();
        btn.textContent = '💾';

        // Salvar ao pressionar Ctrl+Enter
        description.addEventListener('keydown', (e) => {
            if (e.ctrlKey && e.key === 'Enter') {
                saveDescription(card.dataset.id, description.innerHTML);
                description.removeAttribute('contenteditable');
                btn.textContent = '✎';
            }
        });

        // Salvar ao perder o foco
        description.addEventListener('blur', () => {
            saveDescription(card.dataset.id, description.innerHTML);
            description.removeAttribute('contenteditable');
            btn.textContent = '✎';
        });
    } else {
        description.removeAttribute('contenteditable');
        btn.textContent = '✎';
    }
}

// Função para salvar descrição
async function saveDescription(cardId, newDesc) {
    try {
        const response = await fetch(`http://localhost:8090/api/v1/cards/atualizar/${cardId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                descricao: newDesc
            })
        });

        if (response.ok) {
            showNotification('Alterações salvas com sucesso!');
        } else {
            showNotification('Erro ao salvar alterações', true);
        }
    } catch (error) {
        showNotification('Erro de conexão', true);
        console.error('Erro:', error);
    }
}

function dragEnter(e) {
    e.preventDefault();
    const column = e.target.closest('.column');
    if (column) {
        column.classList.add('over');
    }
}

function dragLeave(e) {
    const column = e.target.closest('.column');
    if (column) {
        column.classList.remove('over');
    }
}

// Inicialização
document.addEventListener('DOMContentLoaded', loadCards);