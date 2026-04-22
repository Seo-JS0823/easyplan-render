import { ledgerData } from './ledger-metadata.js';

console.log(ledgerData)

if(ledgerData.message !== '204') {
	const firshLedger = ledgerData.data.ledgerMeta[0];
	const netWorth = ledgerData.data.netWorth;
	const monthRank = ledgerData.data.monthRank;
	const recentExpense = ledgerData.data.recentExpense;
	const recentIncome = ledgerData.data.recentIncome;
	
	ledgerDropdownRendering(firshLedger);
	
	const liaRate = (netWorth.totalLiabilities / netWorth.totalAssets) * 100;
			
	const netWorthRate = (netWorth.netWorth / netWorth.totalAssets) * 100;
	
	netWorth.liaRate = Number(liaRate.toFixed(1));
	netWorth.netWorthRate = Number(netWorthRate.toFixed(1));
	
	netWorthRendering(netWorth);
	monthRankRendering(monthRank);
	recentExpenseRendering(recentExpense);
	recentIncomeRendering(recentIncome);
} else {
	const dropdownText = document.querySelector('.dropdown-wrapper[data-drop=ledgerSelect] .dropdown-text');
	
	dropdownText.textContent = '가계부를 생성해주세요.';
	
	netWorthRendering({});
	monthRankRendering({});
	recentExpenseRendering({});
	recentIncomeRendering({});
}

function ledgerDropdownRendering(mainLedger) {
	const dropdownContainer = document.querySelector('.dropdown-trigger .dropdown');
	const dropdownText = document.querySelector('.dropdown-wrapper[data-drop=ledgerSelect] .dropdown-text');
	
	dropdownText.setAttribute('data-selected', mainLedger.ledgerId);
	dropdownText.textContent = mainLedger.ledgerName;
	
	dropdownContainer.innerHTML = ledgerData.data.ledgerMeta.map(ledger => `
	    <span class="dropdown-item" data-value="${ledger.ledgerId}" data-type="${ledger.type}">${ledger.ledgerName}</span>
	`).join('');
}

document.getElementById('currentMonthSummary').appendChild(monthSummaryText())

function monthSummaryText() {
	const tag = document.createElement('span');
	tag.textContent = ' [' + getDateFormat('mm월') + ']';
	tag.className = 'fc-text fs-caption';
	
	return tag;
}

document.addEventListener('click', (e) => {
	const wrapper = e.target.closest('.dropdown-wrapper[data-drop="ledgerSelect"]');
	if(!wrapper) return;
	
	const trigger = wrapper.querySelector('.dropdown-trigger');
	
	const item = e.target.closest('.dropdown-item');
	
	const badge = document.getElementById('ledgerTypeBadge');
	
	const dropdownContainer = document.querySelector('.dropdown-trigger .dropdown');
	
	if(!dropdownContainer.innerHTML) {
		toastModal('가계부를 생성해주세요')
		return;
	}
	
	if(wrapper) {
		trigger.classList.toggle('dropdown-active');
		
		if(item) {
			const text = trigger.querySelector('.dropdown-text');
			text.setAttribute('data-selected', item.dataset.value);
			text.textContent = item.textContent;
			badge.setAttribute('data-type', item.dataset.type);
			badge.textContent = item.dataset.type === 'PERSONAL' ? '개인 가계부' : '공유 가계부';
			
			dashboardNetWorthRequest();
		}
	}
});

async function dashboardNetWorthRequest() {
	const ledgerId = document.querySelector('.app-container').querySelector('.dropdown-text').dataset.selected;
	
	const currentMonth = new Date().getMonth() + 1;
	
	const month = getMonthRange(currentMonth);
	
	const metadata = await fetch(`/api/ledger/meta/${ledgerId}?startDate=${month.startDate}&endDate=${month.endDate}`);
	
	if(!metadata.ok) {
		const error = await metadata.json();
		console.error(error);
	}
	
	const result = await metadata.json();
	
	if(result) {	
		const netWorth = result.netWorth;
		const monthRank = result.monthRank;
		const recentExpense = result.recentExpense;
		const recentIncome = result.recentIncome;
		
		const liaRate = (netWorth.totalLiabilities / netWorth.totalAssets) * 100;
		
		const netWorthRate = (netWorth.netWorth / netWorth.totalAssets) * 100;
		
		netWorth.liaRate = Number(liaRate.toFixed(1));
		netWorth.netWorthRate = Number(netWorthRate.toFixed(1));
		
		netWorthRendering(netWorth);
		monthRankRendering(monthRank);
		recentExpenseRendering(recentExpense);
		recentIncomeRendering(recentIncome);
	}
}

function recentExpenseRendering(recent) {
	const headHTML = `
    <div class="table-row-4 head">
      <div class="table-header">카테고리</div>
      <div class="table-header">금액</div>
      <div class="table-header">비용자산</div>
      <div class="table-header">지출날짜</div>
    </div>
  `;
	
	if(!Array.isArray(recent)) {
		document.querySelector('[data-card=recentExpense] .card-body .table').innerHTML = headHTML;
		return;
	}
	
  const recentHTML = recent.map(item => `
    <div class="table-row-4 content">
      <span class="bold">${item.accountName}</span>
      <div class="bold">${moneyFormat(item.totalAmount)}</div>
      <div class="flex gap-m">
        <div class="ball ${item.useType === 'ASSET' ? 'asset' : 'liabilities'}"></div>
        <span class="bold">${item.useAssetName}</span>
      </div>
      <div>${item.transactionDate}</div>
    </div>
  `).join('');

  document.querySelector('[data-card=recentExpense] .card-body .table').innerHTML = headHTML + recentHTML;
}

function recentIncomeRendering(recent) {
	console.log(recent)
	
	const headHTML = `
    <div class="table-row-4 head">
      <div class="table-header">카테고리</div>
      <div class="table-header">금액</div>
      <div class="table-header">수익자산</div>
      <div class="table-header">수입날짜</div>
    </div>
  `;
	
	if(!Array.isArray(recent)) {
		document.querySelector('[data-card=recentIncome] .card-body .table').innerHTML = headHTML;
		return;
	}
	
  const recentHTML = recent.map(item => `
    <div class="table-row-4 content">
      <span class="bold">${item.accountName}</span>
      <div class="bold">${moneyFormat(item.totalAmount)}</div>
      <div class="flex gap-m">
        <div class="ball income"></div>
        <span class="bold">${item.useAssetName}</span>
      </div>
      <div>${item.transactionDate}</div>
    </div>
  `).join('');

  document.querySelector('[data-card=recentIncome] .card-body .table').innerHTML = headHTML + recentHTML;
}

function netWorthRendering(data) {
	if(!data.totalAssets || data.totalAssets === 0) {
		data.liaRate = 0;
		data.netWorthRate = 0;
	}
	
	const parent = document.querySelector('[data-card=netWorth]');
	const totalAssetText = parent.querySelector('.summary-total-amount');
	const assetParent = parent.querySelector('.card-body .chart.asset');
		const assetChip = assetParent.querySelector('.percent-chip');
		const assetText = assetParent.querySelector('.summary-state-amount');
		const assetProgress = assetParent.querySelector('.progress-fill');
	const liabilitiesParent = parent.querySelector('.card-body .chart.liabilities');
		const liabilitiesChip = liabilitiesParent.querySelector('.percent-chip');
		const liabilitiesText = liabilitiesParent.querySelector('.summary-state-amount');
		const liabilitiesProgress = liabilitiesParent.querySelector('.progress-fill');
	
	totalAssetText.textContent = moneyFormat(data.netWorth);
		
	assetChip.textContent = data.netWorthRate;
	assetText.textContent = moneyFormat(data.totalAssets);
	assetProgress.style.width = data.netWorthRate + '%';
	
	liabilitiesChip.textContent = data.liaRate;
	liabilitiesText.textContent = moneyFormat(data.totalLiabilities);
	liabilitiesProgress.style.width = data.liaRate + '%';
}

function monthRankRendering(monthRank) {
    const parent = document.querySelector('[data-card=monthRank]');
    
    parent.querySelector('.income .summary-total-amount').textContent = moneyFormat(monthRank.totalIncome);
    parent.querySelector('.expense .summary-total-amount').textContent = moneyFormat(monthRank.totalExpense);

    for (let i = 0; i < 3; i++) {
			let category;
			
			if(Array.isArray(monthRank.topCategories)) {
	      category = monthRank.topCategories[i];				
			} else {
				
			}
      const row = parent.querySelector(`.chart.rank-${i + 1}`);
      
      if (!row) continue;

      const name = category ? category.categoryName : '지출 없음';
      const percent = category ? `${category.percentage}` : '0';
      const amount = category ? moneyFormat(category.amount) : '0';
      const width = category ? `${category.percentage}%` : '0%';

      // 돔 업데이트 (준비된 값 꽂기)
      row.querySelector('.categoryName').textContent = name;
      row.querySelector('.percent-chip').textContent = percent;
      row.querySelector('.summary-state-amount').textContent = amount;
      row.querySelector('.progress-fill').style.width = width;
    }
}

if(modal) {
	modal.addEventListener('click', (e) => {
		e.stopPropagation();
		
		const eventTag = modal.querySelector('#event');
		if(!eventTag) return;
		
		const eventKey = eventTag.value;
		
		if(eventKey === 'journalCreate') {
			const event = e.target.closest('[data-event]');
			
			if(!event) return;
			
			const action = event.dataset.event;
			
			switch (action) {
				case 'expenseType': expenseTypeBtn(modal, e.target); break;
				
				case 'transactionCreate': transactionCreateDropdown(e.target); break;
				
				case 'dropdownSelect': transactionDropdownSelected(e.target); break;
				
				case 'journalCreateRequest': journalCreateRequest(modal); break;
				
				case 'datepicker': calendarOpen(e.target.closest('.datepicker-wrapper')); break;
				
				case 'prevMonthBtn': datepickerMonthBtn(e.target.closest('.datepicker-wrapper'), 'prev'); break;
				
				case 'nextMonthBtn': datepickerMonthBtn(e.target.closest('.datepicker-wrapper'), 'next'); break;
				
				case 'today': datepickerMonthBtn(e.target.closest('.datepicker-wrapper'), 'today'); break;
				
				case 'currentMonth': datepickerMonthBtn(e.target.closest('.datepicker-wrapper'), 'currentMonth'); break;
				
				case 'datepickerClose': calendarClose(e.target.closest('.datepicker-wrapper')); break;
			}
		}
	});
}

const journalCreateBtn = document.querySelector('#journalCreateModal');

journalCreateBtn.addEventListener('click', (e) => {
	const ledgerId = document.querySelector('[data-drop=ledgerSelect] .dropdown-text').getAttribute('data-selected');
	
	if(ledgerId === null) {
		toastModal('가계부를 먼저 생성해주세요');
		return;
	}
	
	expenseDropdown(
		'expense',
		ledgerData.data,
		'Step2. 돈을 어디에 쓰셨나요?',
		'Step3. 지출하신 항목을 선택해주세요.',
		'Step4. 지출하신 금액을 입력해주세요.',
		['지출'],
		['자산', '부채']);
});

async function journalCreateRequest(parent) {
	const debitAccountId = parent.querySelector('#debitTransaction').dataset.selected;
	const creditAccountId = parent.querySelector('#creditTransaction').dataset.selected;
	const amount = parent.querySelector('#transactionAmount').value;
	const ledgerId = parent.querySelector('#ledgerId').value;
	const memo = parent.querySelector('#transactionMemo').value;
	const transactionType = parent.querySelector('.container-btn.active').dataset.type.toUpperCase();
	
	const cons = {
		ledgerId: ledgerId,
		debitAccountId: debitAccountId,
		creditAccountId: creditAccountId,
		amount: amount,
		memo: memo,
		date: parent.querySelector('#transactionDate').dataset.selectedDate,
		transactionType: transactionType
	}
	
	console.log(cons);
	
	const response = await fetch('/api/journal/create', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'X-XSRF-TOKEN': csrfToken()
		},
		body: JSON.stringify(cons)
	});
	
	if(!response.ok) {
		const errorResponse = await response.json();
		toastModal(errorResponse);
		return;
	}
	
	const result = await response.json();
	
	console.log(result);
}

function transactionCreateDropdown(tag) {
	tag.classList.add('dropdown-active');
}

function transactionDropdownSelected(tag) {
	const dropdown = tag.parentElement.parentElement;
	const dropdownText = dropdown.querySelector('.dropdown-text');
	dropdownText.setAttribute('data-selected', tag.dataset.value);
	dropdownText.textContent = tag.textContent;
	dropdown.classList.remove('dropdown-active');
}

function expenseTypeBtn(modal, target) {
	const expenseBtns = modal.querySelectorAll('.container-btn');
	const expenseType = target.dataset.type;
	
	expenseBtns.forEach(btn => {
		btn.classList.remove('active');
	})
	
	target.classList.add('active');
	if(expenseType === 'expense') {
		expenseDropdown(
			'expense',
			ledgerData.data,
			'Step2. 돈을 어디에 쓰셨나요??',
			'Step3. 지출하신 항목을 선택해주세요.',
			'Step4. 지출하신 금액을 입력해주세요.',
			['지출'],
			['자산', '부채']);
	}
	
	if(expenseType === 'income') {
		expenseDropdown(
			'income',
			ledgerData.data,
			'Step2. 돈이 저장된 자산항목을 선택해주세요.',
			'Step3. 어디서 돈이 들어왔나요?',
			'Step4. 벌으신 금액을 입력해주세요.',
			['자산'],
			['수입']);
	}
}

function expenseDropdown(transactionType, ledgerData, step2, step3, step4, debitType, creditType) {
	const ledgerId = document.querySelector('.app-container').querySelector('.dropdown-text').dataset.selected;
	const currentLedger = ledgerData.ledgerMeta.find(l => String(l.ledgerId) === String(ledgerId));
	
	if(!currentLedger) {
		console.error('해당 가계부를 찾을 수 없습니다.');
		return;
	}
	
	const getOptions = (types) => {
		const typeList = 	Array.isArray(types) ? types : [types];
		const filteredCategories = currentLedger.categories.filter(cat => typeList.includes(cat.categoryName));
		
		return filteredCategories.map(cat => {
			return cat.account
				.filter(acc => acc.payment === true)
				.map(acc => `
					<span class="dropdown-item" data-value="${acc.accountId}" data-event="dropdownSelect">(${cat.categoryName}) - ${acc.accountName}</span>
				`).join('');
		}).join('') || '<span class="dropdown-item">항목 없음</span>';
	}
	
	const optionsStep2 = getOptions(debitType);
	const optionsStep4 = getOptions(creditType);
	
	const detailsHTML = `
	<div class="flex items-center gap-m fc-text fs-body">
    <div class="ball liabilities"></div>
    <span class="debit-description">${step2}</span>
  </div>
  <div class="dropdown-wrapper" data-drop="transactionCreate">
    <div class="dropdown-trigger" data-event="transactionCreate">
      <span class="dropdown-text" data-selected="" id="debitTransaction">${debitType} 항목을 선택하세요.</span>
      <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
        <path d="M11.9999 10.8284L7.0502 15.7782L5.63599 14.364L11.9999 8L18.3639 14.364L16.9497 15.7782L11.9999 10.8284Z"></path>
      </svg>
      <div class="dropdown">
        ${optionsStep2}
      </div>
    </div>
  </div>
	<div class="flex-col gap-l mt-1" data-blokc="step3">
		<div class="flex items-center gap-m fc-text fs-body">
			<div class="ball expense"></div>
			<span>${step3}</span>
		</div>
	</div>
	<div class="dropdown-wrapper">
		<div class="dropdown-trigger" data-event="transactionCreate">
			<span class="dropdown-text" data-selected="" id="creditTransaction">항목을 선택하세요.</span>
			<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
				<path d="M11.9999 10.8284L7.0502 15.7782L5.63599 14.364L11.9999 8L18.3639 14.364L16.9497 15.7782L11.9999 10.8284Z"></path>
			</svg>
			<div class="dropdown">
				${optionsStep4}
			</div>
		</div>
	</div>
	<div class="flex-col gap-l mt-1" data-block="step4">
		<div class="flex items-center gap-m fc-text fs-body">
			<div class="ball income"></div>
			<span>${step4}</span>
		</div>
		<input type="text" id="transactionAmount" placeholder="숫자만 입력해주세요.">
	</div>
	<div class="flex-col gap-l mt-1" data-block="step5">
		<div class="flex items-center gap-m fc-text fs-body">
			<div class="ball liabilities"></div>
			<span>Step5. 거래 발생 날짜를 선택하세요.</span>
		</div>
		<div class="datepicker-wrapper" data-event="datepicker" id="transactionDate">
			<button class="datepicker-trigger">
				<svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
	        <path d="M8 2V5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
	        <path d="M16 2V5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
	        <rect x="3" y="4" width="18" height="17" rx="3" stroke="currentColor" stroke-width="1.8"/>
	        <path d="M3 9H21" stroke="currentColor" stroke-width="1.8"/>
	      </svg>
				<span class="datepicker-text" id="dateTriggerText">날짜 선택</span>
			</button>
			
			<div class="datepicker bottom">
				<div class="datepicker-actions">
					<span class="datepicker-chip" data-event="today">오늘</span>
					<span class="datepicker-chip" data-event="currentMonth">이번 달</span>
				</div>
				
				<div class="datepicker-header">
					<button class="datepicker-nav" type="button" data-event="prevMonthBtn" aria-label="이전 달">‹</button>
					<div class="datepicker-title"></div>
					<button class="datepicker-nav" type="button" data-event="nextMonthBtn" arai-label="다음 달">›</button>
				</div>
				
				<div class="datepicker-weekdays">
					<div class="datepicker-weekday">일</div>
					<div class="datepicker-weekday">월</div>
					<div class="datepicker-weekday">화</div>
					<div class="datepicker-weekday">수</div>
					<div class="datepicker-weekday">목</div>
					<div class="datepicker-weekday">금</div>
					<div class="datepicker-weekday">토</div>
				</div>
				
				<div class="datepicker-days" id="datepickerDays"></div>
				
				<div class="datepicker-footer">
					<span class="datepicker-selected-text"></span>
					<button class="btn btn-primary small" data-event="datepickerClose">선택</button>
				</div>
			</div>
		</div>
	</div>
	<div class="flex-col gap-l mt-1" data-block="step6">
		<div class="flex items-center gap-m fc-text fs-body">
			<div class="ball asset"></div>
			<span>Step6. 메모</span>
		</div>
		<textarea id="transactionMemo"></textarea>
	</div>
	`;
	
	const html = `
	<input type="hidden" id="event" value="journalCreate">
	<input type="hidden" id="ledgerId" value="${ledgerId}">
	<div class="modal-container-l modal-shadow">
		<div class="modal-header p-l fc-text">
			<div class="container-header-icon">
				<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
					<path d="M18 15L17.999 18H21V20H17.999L18 23H16L15.999 20H13V18H15.999L16 15H18ZM11 18V20H3V18H11ZM21 11V13H3V11H21ZM21 4V6H3V4H21Z"></path>
				</svg>
			</div>
			<span>거래입력</span>
		</div>
		<div class="line"></div>
		<div class="flex-col gap-l p-l">
			<div class="flex items-center gap-m fc-text fs-body">
				<div class="ball asset"></div>
				<span>Step1. 어떤 유형의 거래인가요?</span>			
			</div>
			<div class="grid-cols-2 gap-l">
				<button class="container-btn" data-event="expenseType" data-type="expense">소비</button>
				<button class="container-btn" data-event="expenseType" data-type="income">수입</button>
			</div>
		</div>
		<div class="flex-col gap-l p-l" data-block="step2">
		  ${detailsHTML}
		</div>
		<div class="line"></div>
		<div class="modal-action" data-event="journalCreateRun">
			<button class="btn btn-primary" data-event="journalCreateRequest">거래입력</button>
			<button class="btn btn-primary-reverse" data-event="modal-close">입력취소</button>
		</div>
	`;
	
	modalOpen(html);
	
	const transactionBtns = modal.querySelectorAll(`.container-btn`);
	
	transactionBtns.forEach(btn => {
		btn.classList.remove('active');
	})
	
	transactionBtns.forEach(btn => {
		if(btn.dataset.type === transactionType) {
			btn.classList.add('active');
		} 
	})
	
	const block = modal.querySelector('[data-block=step2]');
	block.innerHTML = '';
	block.insertAdjacentHTML('afterbegin', detailsHTML);
	
	return html;
}