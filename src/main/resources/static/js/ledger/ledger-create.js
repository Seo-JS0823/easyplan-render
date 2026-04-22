const ledgerTypeBtns = document.querySelectorAll('.container-btn');
const appContainer = document.querySelector('.app-container');

ledgerTypeBtns.forEach(typeBtn => {
	typeBtn.addEventListener('click', (e) => {
		
		ledgerTypeBtns.forEach(btn => {
			btn.classList.remove('active');
		})
		
		e.target.classList.add('active');
	})
})

const categoryChoice = document.querySelector('#categoryChoice');

categoryChoice.addEventListener('click', () => {
	modalOpen(categoryChoiceModal());
	const categoryBlock = document.querySelector('[data-block=category]');
	
	if(categoryBlock) {
		const values = categoryBlock.querySelectorAll('.badge');
		
		const checks = modalOverlay.querySelectorAll('input[type=checkbox]');
		values.forEach(val => {
			const value = val.dataset.value;
			checks.forEach(chk => {
				if(chk.dataset.value === value) {
					chk.checked = true;
				}
			})
		});
	}
});

if(modal) {
	modal.addEventListener('keydown', (e) => {
		if(e.key !== 'Enter' || e.isComposing) return;
		
		const tagIgnore = ['BUTTON', 'A', 'SELECT'];
		if(tagIgnore.includes(e.target.tagName)) return;
		
		const event = modal.querySelector('#event').value;
		
		if(event) {
			switch (event) {
				case 'categoryChoice' : categoryChoiceAction(modal); break;
			}
		}
	});
	
	modal.addEventListener('click', (e) => {
		e.stopPropagation();
		
		const el = e.target.closest('[data-event]');
		
		if(!el) return;
		
		const action = el.dataset.event;
		
		if(action) {
			switch (action) {
				case 'categoryChoice' : categoryChoiceAction(modal); break;
			}
		}
	});
}

function categoryChoiceAction(modal) {
	const choiceCategories = modal.querySelectorAll('input[type=checkbox]:checked');
	const categoryContainer = document.querySelector('[data-block=category]');
	categoryContainer.innerHTML = '';

	let fragment = document.createDocumentFragment();

	choiceCategories.forEach(checkbox => {
	  const value = checkbox.dataset.value;
	  const name = checkbox.parentElement.querySelector('.checkbox-description').textContent;
	  
	  const itemTag = document.createElement('div');
	  
	  let typeClass = '';
	  
	  if (value.includes('inc')) {
			typeClass = 'income';
	  } else if (value.includes('ass')) {
			typeClass = 'asset';
	  } else if (value.includes('lia')) {
			typeClass = 'liabilities';
	  } else if (value.includes('exp')) {
			typeClass = 'expense';
	  }
	
	  itemTag.className = `badge ${typeClass}`;
	  itemTag.textContent = name;
		itemTag.setAttribute('data-value', value);
	  
	  fragment.appendChild(itemTag);
	});

	categoryContainer.appendChild(fragment);
	
	modalClose();
}

const ledgerCreateBtn = document.querySelector('#ledgerCreate');

if(ledgerCreateBtn) {
	ledgerCreateBtn.addEventListener('click', async (e) => {
		const ledgerName = appContainer.querySelector('#ledgerName').value;
		const ledgerDescription = appContainer.querySelector('#ledgerDescription').value;
		const ledgerType = appContainer.querySelector('.container-btn.active').dataset.value;
		const ledgerCategories = appContainer.querySelectorAll('.badge');
		const categories = new Set();
		
		if(ledgerCategories.length < 1) {
			toastModalOpen('최소 1개의 기본 카테고리를 선택해야 합니다.');
			return;
		}
		
		ledgerCategories.forEach(item => {
			categories.add(item.dataset.value);
		});
		
		const ledgerSetting = {
			ledgerName: ledgerName,
			ledgerDescription: ledgerDescription,
			ledgerType: ledgerType,
			categories: [...categories, 'equ_01']
		}
		
		const response = await fetch('/api/ledger/create', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'X-XSRF-TOKEN': csrfToken()
			},
			body: JSON.stringify(ledgerSetting)
		});
		
		if(!response.ok) {
			alert('에러');
			return;
		}
		
		const result = await response.json();
		
		console.log(JSON.stringify(result));
	});
}