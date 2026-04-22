import { categoryMetadata } from './LedgerCategoryWithAccounts.js';

console.log(categoryMetadata);

const dropdownContainer = document.querySelector('.dropdown-trigger .dropdown');
const dropdownText = document.querySelector('.dropdown-wrapper[data-drop=ledgerSelect] .dropdown-text');
const firstLedger = categoryMetadata.accounts[0];

if(firstLedger) {
	dropdownText.setAttribute('data-selected', firstLedger.ledgerId);
	dropdownText.textContent = firstLedger.ledgerName;
	
	dropdownContainer.innerHTML = categoryMetadata.accounts.map(ledger => `
	    <span class="dropdown-item" data-value="${ledger.ledgerId}" data-type="${ledger.type}">${ledger.ledgerName}</span>
	`).join('');
	
	categoryBlockRendering(firstLedger.categories)
}

document.addEventListener('click', (e) => {
	const categoryCreateBtn = e.target.closest('[data-event=categoryCreateBtn]');
	if(categoryCreateBtn) {
		let key = e.target.parentElement.parentElement.querySelector('.card-body').dataset.block.replace('category-', '').toUpperCase();
		
		if(key === 'EQUITY') {
			key = 'ALL';
		}
		
		const icon = e.target.parentElement.querySelector('.card-icon')
		
		const findCategoryObject = (category, key) => {
			return category.filter(op => op.accountType === key);
		}
		
		const selectedCategory = findCategoryObject(categoryMetadata.options, key);
		
		const categoryCreateModalData = {
			title: e.target.textContent,
			categoryId: e.target.parentElement.parentElement.querySelector('.card-body').dataset.categoryKey,
			icon: icon.outerHTML,
			selected: selectedCategory,
		}
		
		categoryCreateModalOpen(categoryCreateModalData);
	}
	
	const wrapper = e.target.closest('.dropdown-wrapper[data-drop="ledgerSelect"]');
	if(wrapper) {
		const trigger = wrapper.querySelector('.dropdown-trigger');
			
		const item = e.target.closest('.dropdown-item');
		
		const badge = document.getElementById('ledgerTypeBadge');
		
		trigger.classList.toggle('dropdown-active');
		
		if(item) {
			const text = trigger.querySelector('.dropdown-text');
			text.setAttribute('data-selected', item.dataset.value);
			text.textContent = item.textContent;
			badge.setAttribute('data-type', item.dataset.type);
			badge.textContent = item.dataset.type === 'PERSONAL' ? '개인 가계부' : '공유 가계부';
			
			categoryMetadata.accounts.forEach(cat => {
				if(cat.ledgerName === item.textContent) {
					categoryBlockRendering(cat.categories);
					console.log(cat.categories)
				}
			})
		}
	}
	
	const categoryUpdate = e.target.closest('[data-event=categoryUpdate]');
	if(categoryUpdate) {
		if(e.target.closest('.tooltip')) {
			console.log('리턴');
			return;
		}
		
		const key = e.target.getAttribute('data-key');
		
		const findAccountObject = (metadata, id) => {
			for(const ledger of metadata) {
				if(String(ledger.ledgerId) === dropdownText.getAttribute('data-selected')) {
					for(const category of ledger.categories) {
						for(const account of category.account) {
							if(String(account.accountId) === key) {
								return account;
							}
						}
					}
				}
			}
		}
		
		const account = findAccountObject(categoryMetadata.accounts, key);
		
		categoryUpdateModalOpen(account)
		
		return;
	}
});

modal.addEventListener('click', (e) => {
	const categoryOptionItem = e.target.closest('.dropdown-item');
	if(categoryOptionItem) {
		const wrapper = categoryOptionItem.parentElement.parentElement.parentElement;
	
    const textElement = wrapper.querySelector('.dropdown-text');
    const trigger = wrapper.querySelector('.dropdown-trigger');
		
		const selectedValue = categoryOptionItem.getAttribute('data-value');
    const selectedName = categoryOptionItem.textContent;
		
		textElement.setAttribute('data-key', selectedValue);
    textElement.textContent = selectedName;
		
		trigger.classList.remove('active');
		return;
	}
	
	const categoryCreateDropdown = e.target.closest('[data-block=categoryCreateDropdown]');
	if(categoryCreateDropdown) {
		e.target.classList.toggle('active');
		return;
	}
	
	const categoryOptionDropdown = e.target.closest('[data-block=categoryOptionDropdown]');
	if(categoryOptionDropdown) {
		if(e.target.closest('.dropdown-item')) {
			return;
		}
		
		e.target.classList.toggle('active');
		return;
	}
	
	const categoryCreateBtn = e.target.closest('[data-event=categoryCreateRequest]');
	if(categoryCreateBtn) {
		const categoryCreateBody = {
			ledgerId: document.querySelector('[data-drop=ledgerSelect] .dropdown-text').dataset.selected,
			accountName: modal.querySelector('#categoryName').value,
			optionId: modal.querySelector('[data-block=categoryCreateDropdown] .dropdown-text').dataset.key,
			categoryId: modal.querySelector('.modal-container-l').dataset.categoryKey,
			memo: modal.querySelector('#accountMemo').value
		}
		
		fetch('/api/ledger/category', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'X-XSRF-TOKEN': csrfToken()
			},
			body: JSON.stringify(categoryCreateBody)
		})
		.then(res => res.json())
		.then(data => console.log(data))
		
	}
	
	const categoryUpdateBtn = e.target.closest('[data-event=categoryUpdateRequest]');
	if(categoryUpdateBtn) {
		const categoryUpdateBody = {
			ledgerId: document.querySelector('[data-drop=ledgerSelect] .dropdown-text').dataset.selected,
			accountId: modal.querySelector('#categoryName').dataset.key,
			optionId: modal.querySelector('[data-block=categoryOptionDropdown] .dropdown-text').dataset.key,
		}
		
		fetch('/api/ledger/category', {
			method: 'PATCH',
			headers: {
				'Content-Type': 'application/json',
				'X-XSRF-TOKEN': csrfToken()
			},
			body: JSON.stringify(categoryUpdateBody)
		})
		.then(res => res.json())
		.then(data => console.log(data));
	}
})

function categoryCreateModalOpen(category) {
	const categories = category.selected;
	
	const options = categories.map(op => `
		<span class="dropdown-item" data-value="${op.id}"}>${op.optionName}</span>
	`).join('');
	
	const html = `
	<div class="modal-container-l modal-shadow" data-category-key="${category.categoryId}">
		<div class="modal-header gap-m p-l fc-text">
			${category.icon}
			<span id="categoryGroup">${category.title}</span>
		</div>
		<div class="line"></div>
		<div class="modal-body">
			<div class="input-group">
				<span>항목명</span>
				<input type="text" id="categoryName" placeholder="등록할 항목명을 입력하세요."/>
			</div>
			<div class="input-group">
				<span>항목 옵션</span>
				<div class="dropdown-wrapper round-m" data-block="categoryCreateDropdown">
					<div class="dropdown-trigger">
						<span class="dropdown-text">옵션을 선택하세요.</span>
						<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
			        <path d="M11.9999 10.8284L7.0502 15.7782L5.63599 14.364L11.9999 8L18.3639 14.364L16.9497 15.7782L11.9999 10.8284Z"></path>
			      </svg>
			      <div class="dropdown round-m">${options}</div>
					</div>
				</div>
			</div>
			<div class="input-group">
				<span>메모</span>
				<textarea id="accountMemo"></textarea>
			</div>
		</div>
		<div class="modal-action">
			<button class="btn btn-primary" data-event="categoryCreateRequest">항목 등록하기</button>
		</div>
	</div>
	`
	
	modalOpen(html);
}

function categoryUpdateModalOpen(account) {
	const currentOption = account.options.find(op => op.id === account.optionId);
	
	const selectedOptionName = currentOption ? currentOption.optionName : '옵션 선택';
	
	const optionsHTML = account.options.map(op => `
		<span class="dropdown-item" data-value="${op.id}"}>${op.optionName}</span>
	`).join('');
	
	const html = `
	<div class="modal-container-l modal-shadow">
		<div class="modal-header p-l fc-text">
			<div class="container-header-icon">
				<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
					<path d="M7.24264 17.9967H3V13.754L14.435 2.319C14.8256 1.92848 15.4587 1.92848 15.8492 2.319L18.6777 5.14743C19.0682 5.53795 19.0682 6.17112 18.6777 6.56164L7.24264 17.9967ZM3 19.9967H21V21.9967H3V19.9967Z"></path>
				</svg>
			</div>
			<span>카테고리 관리</span>
		</div>
		<div class="line"></div>
		<div class="flex-col gap-m p-m">
			<div class="input-group">
				<span>항목 이름</span>
				<input type="text" id="categoryName" value="${account.accountName}" data-key="${account.accountId}">
			</div>
		</div>
		<div class="flex-col gap-m p-m">
			<div class="input-group">
				<span>옵션</span>
				<div class="dropdown-wrapper round-m" data-block="categoryOptionDropdown">
					<div class="dropdown-trigger">
						<span class="dropdown-text" data-key="${account.optionId}">${selectedOptionName}</span>
						<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
			        <path d="M11.9999 10.8284L7.0502 15.7782L5.63599 14.364L11.9999 8L18.3639 14.364L16.9497 15.7782L11.9999 10.8284Z"></path>
			      </svg>
						<div class="dropdown round-m">
							${optionsHTML}
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="flex-col gap-m p-m">
			<div class="input-group">
				<span>계정 메모</span>
				<textarea></textarea>
			</div>
		</div>
		<div class="modal-action">
			<button class="btn btn-primary" data-event="categoryUpdateRequest">수정</button>
			<button class="btn btn-primary-reverse" data-event="modal-close">닫기</button>
		</div>
	</div>
	`;
	
	modalOpen(html);
}

function categoryBlockRendering(categories) {
	const containerMapper = {
		'자산': 'category-asset',
		'부채': 'category-liabilities',
		'기초 자산': 'category-equity',
		'수입': 'category-income',
		'지출': 'category-expense'
	}
	
	categories.forEach(cat => {
		const blockKey = containerMapper[cat.categoryName];
		const categoryId = cat.categoryId;
		const blockElement = document.querySelector(`[data-block=${blockKey}]`);
		
		if(blockElement) {
			const html = cat.account.map(acc => `
			<div class="flex between main-border items-center p-m round-m fc-text fs-caption" data-event="categorySetting" data-card="hoverItem">
				<span class="font-component fw-bold">${acc.accountName}</span>
				<div class="flex gap-m items-center">
					<div class="flex items-center justify-center p-s relative tooltip-box" data-event="categoryUpdate" data-key="${acc.accountId}">
						<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M7.24264 17.9967H3V13.754L14.435 2.319C14.8256 1.92848 15.4587 1.92848 15.8492 2.319L18.6777 5.14743C19.0682 5.53795 19.0682 6.17112 18.6777 6.56164L7.24264 17.9967ZM3 19.9967H21V21.9967H3V19.9967Z"></path></svg>
						<div class="tooltip bottom">
							<span>수정하기</span>
						</div>
					</div>
					<div class="flex items-center justify-center p-s relative tooltip-box" data-event="categoryDelete">
						<svg viewBox="0 0 24 24" width="18" height="18" fill="var(--color-danger)"><path d="M6.53451 3H20.9993C21.5516 3 21.9993 3.44772 21.9993 4V20C21.9993 20.5523 21.5516 21 20.9993 21H6.53451C6.20015 21 5.88792 20.8329 5.70246 20.5547L0.369122 12.5547C0.145189 12.2188 0.145189 11.7812 0.369122 11.4453L5.70246 3.4453C5.88792 3.1671 6.20015 3 6.53451 3ZM12.9993 10.5858L10.1709 7.75736L8.75668 9.17157L11.5851 12L8.75668 14.8284L10.1709 16.2426L12.9993 13.4142L15.8277 16.2426L17.242 14.8284L14.4135 12L17.242 9.17157L15.8277 7.75736L12.9993 10.5858Z"></path></svg>
						<div class="tooltip bottom">
							<span>삭제하기</span>
						</div>
					</div>
				</div>
			</div>
			`).join('');
			
			blockElement.innerHTML = html;
			blockElement.setAttribute('data-category-key', categoryId);
		}
	});
}