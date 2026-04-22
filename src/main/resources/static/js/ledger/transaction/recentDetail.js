import { categoryMetadata } from '../LedgerCategoryWithAccounts.js';

console.log(categoryMetadata)

/*
 * =========================================
 * 상태(State)
 * =========================================
 *
 * 기존 코드는 ledger / startDate / endDate / searchOption / sortedOption / nextURL 등을
 * DOM(dataset, textContent 등)에서 계속 다시 읽고 있었다.
 *
 * 이 버전에서는 "화면 상태"를 JS 객체 하나에 모은다.
 * DOM은 보여주는 역할, state는 실제 값 보관 역할을 맡는다.
 */
const state = {
  ledgerId: null,
  ledgerType: null,
  ledgerName: null,

  startDate: null,
  endDate: null,
  startViewMonth: null,

  searchOption: 'ALL',
  sortedOption: 'LATEST',
	keyword: '',

  offset: 0,
  nextURL: null,
  hasNext: false,
  loading: false
};

/*
 * =========================================
 * DOM 캐시
 * =========================================
 *
 * querySelector를 곳곳에서 반복 호출하면
 * 1. 가독성이 떨어지고
 * 2. 실수로 selector가 달라질 수 있고
 * 3. 나중에 바꾸기 귀찮아진다
 *
 * 그래서 자주 쓰는 요소는 한 번 캐싱해둔다.
 */
const elements = {
  dropdownContainer: document.querySelector('.dropdown-trigger .dropdown'),
  dropdownText: document.querySelector('.dropdown-wrapper[data-drop="ledgerSelect"] .dropdown-text'),
  journalContainer: document.querySelector('[data-block="journalTransaction"]'),
  dateFilterBlock: document.querySelector('[data-block="date-filter"]'),
  loadMoreBlock: document.querySelector('[data-block="load-more"]'),
  ledgerBadge: document.getElementById('ledgerTypeBadge'),

  startDateRoot: document.querySelector('#startDate'),
  endDateRoot: document.querySelector('#endDate'),

  startDateOpenButton: document.querySelector('[data-event="startDatePickerOpen"]'),
  endDateOpenButton: document.querySelector('[data-event="endDatePickerOpen"]'),

  searchOptionTrigger: document.querySelector('#searchOption'),
  sortedOptionTrigger: document.querySelector('#sortedOption'),
	recentDetailSearch: document.querySelector('#recentDetailSearch'),
};

/*
 * =========================================
 * 초기화
 * =========================================
 *
 * 페이지 진입 시 해야 하는 일:
 * 1. ledger dropdown 초기화
 * 2. 초기 날짜 세팅
 * 3. state 초기화
 * 4. 최초 거래내역 조회
 * 5. 이벤트 바인딩
 */
await initPage();

async function initPage() {
  initLedgerDropdown();
  initDateRange();
  bindEvents();

  if (!state.ledgerId || !state.startDate || !state.endDate) {
    return;
  }

  await searchJournalList();
}

/*
 
 */

/*
 * =========================================
 * Ledger 초기화
 * =========================================
 *
 * 첫 ledger를 기본 선택값으로 잡고,
 * dropdown 목록도 렌더링한다.
 */
function initLedgerDropdown() {
  const firstLedger = categoryMetadata.accounts?.[0];
  if (!firstLedger) {
    return;
  }

  state.ledgerId = String(firstLedger.ledgerId);
  state.ledgerType = firstLedger.type;
  state.ledgerName = firstLedger.ledgerName;

  elements.dropdownText.dataset.selected = state.ledgerId;
  elements.dropdownText.textContent = state.ledgerName;

  updateLedgerBadge();

  const ledgerOptionsHtml = categoryMetadata.accounts.map(ledger => `
    <span
      class="dropdown-item"
      data-value="${ledger.ledgerId}"
      data-type="${ledger.type}"
    >
      ${ledger.ledgerName}
    </span>
  `).join('');

  elements.dropdownContainer.insertAdjacentHTML('beforeend', ledgerOptionsHtml);
}

/*
 * =========================================
 * 날짜 초기화
 * =========================================
 *
 * 외부 함수 getDateFormat('datepickerRange')가
 * startDate / endDate / startViewMonth를 반환한다고 가정한다.
 */
function initDateRange() {
  const initDates = getDateFormat('datepickerRange');
  if (!initDates) {
    return;
  }

  state.startDate = initDates.startDate;
  state.endDate = initDates.endDate;
  state.startViewMonth = initDates.startViewMonth;

  elements.startDateRoot.dataset.selectedDate = state.startDate;
  elements.startDateRoot.dataset.viewMonth = state.startViewMonth;
  elements.endDateRoot.dataset.selectedDate = state.endDate;

  elements.startDateOpenButton.textContent = state.startDate;
  elements.endDateOpenButton.textContent = state.endDate;
}

/*
 * =========================================
 * 이벤트 바인딩
 * =========================================
 *
 * 기존처럼 이벤트 위임을 유지한다.
 * 다만 document click 하나에 모든 걸 우겨넣는 대신
 * 역할별 함수로 분기한다.
 */
function bindEvents() {
  elements.dateFilterBlock?.addEventListener('click', handleDateFilterClick);
  document.addEventListener('click', handleDocumentClick);
	document.addEventListener('input', handleDocumentInput);
}

/*
 * =========================================
 * 날짜 필터 이벤트
 * =========================================
 *
 * datepicker 관련 이벤트만 따로 분리한다.
 */
function handleDateFilterClick(e) {
  e.stopPropagation();

  const eventEl = e.target.closest('[data-event]');
  if (!eventEl) {
    return;
  }

  const action = eventEl.dataset.event;

  switch (action) {
    case 'endDatePickerOpen':
      calendarOpen(e.target.closest('#endDate'));
      break;
    case 'endDate-today':
      datepickerMonthBtn(e.target.closest('#endDate'), 'today');
      syncDateStateFromDom();
      break;
    case 'endDate-currentMonth':
      datepickerMonthBtn(e.target.closest('#endDate'), 'currentMonth');
      syncDateStateFromDom();
      break;
    case 'endDate-prevMonthBtn':
      datepickerMonthBtn(e.target.closest('#endDate'), 'prev');
      syncDateStateFromDom();
      break;
    case 'endDate-nextMonthBtn':
      datepickerMonthBtn(e.target.closest('#endDate'), 'next');
      syncDateStateFromDom();
      break;
    case 'endDate-datepickerClose':
    case 'endDate-close':
      calendarClose(e.target.closest('#endDate'));
      syncDateStateFromDom();
      break;

    case 'startDatePickerOpen':
      calendarOpen(e.target.closest('#startDate'));
      break;
    case 'startDate-today':
      datepickerMonthBtn(e.target.closest('#startDate'), 'today');
      syncDateStateFromDom();
      break;
    case 'startDate-currentMonth':
      datepickerMonthBtn(e.target.closest('#startDate'), 'currentMonth');
      syncDateStateFromDom();
      break;
    case 'startDate-prevMonthBtn':
      datepickerMonthBtn(e.target.closest('#startDate'), 'prev');
      syncDateStateFromDom();
      break;
    case 'startDate-nextMonthBtn':
      datepickerMonthBtn(e.target.closest('#startDate'), 'next');
      syncDateStateFromDom();
      break;
    case 'startDate-datepickerClose':
    case 'startDate-close':
      calendarClose(e.target.closest('#startDate'));
      syncDateStateFromDom();
      break;
			
		case 'journalUpdateDatepicker' :
			calendarOpen(e.target.closest('#journalUpdateDatepicker'));
			break;
  }
}

const recentDetailSearchDebounce = debounce( async (keyword) => {
	state.keyword = keyword;
	await searchJournalList();
}, 500);

/*
 * 전역 키보드 입력 이벤트 - 'input'
 */
function handleDocumentInput(e) {
	const searchInput = e.target.closest('#recentDetailSearch');
	if(searchInput) {
		recentDetailSearchDebounce(searchInput.value);
	}
}

/*
 * =========================================
 * 전역 클릭 이벤트
 * =========================================
 *
 * 기존 코드는 여기서 dropdown, load more, search dropdown, sort dropdown,
 * searchRun까지 전부 처리했다.
 *
 * 지금도 이벤트 위임은 유지하지만,
 * 실제 처리는 하위 함수로 넘긴다.
 */
async function handleDocumentClick(e) {
  const ledgerWrapper = e.target.closest('.dropdown-wrapper[data-drop="ledgerSelect"]');
  if (ledgerWrapper) {
    await handleLedgerDropdownClick(e, ledgerWrapper);
    return;
  }

  const loadMoreButton = e.target.closest('[data-event="load-more"]');
  if (loadMoreButton) {
    await handleLoadMore();
    return;
  }

  const searchFilterDropdown = e.target.closest('[data-drop="searchFilterDropdown"]');
  if (searchFilterDropdown) {
    handleOptionDropdown(e, 'searchOption');
    return;
  }

  const sortedFilterDropdown = e.target.closest('[data-drop="searchSortedDropdown"]');
  if (sortedFilterDropdown) {
    handleOptionDropdown(e, 'sortedOption');
    return;
  }

  const searchRunButton = e.target.closest('[data-event="searchRun"]');
  if (searchRunButton) {
    await handleSearchRun();
  }
	
	const journalUpdate = e.target.closest('[data-event=journalUpdate]');
	if (journalUpdate) {
		const entryEl = e.target.closest('.entry-row')
		const journalBeginData = await journalUpdateDataRequest(entryEl);
		journalUpdateModalOpen(journalBeginData);
		return;
	}
}

async function journalUpdateDataRequest(entryElement) {
	const journalKey = entryElement.dataset.lineKey;
	
	const journalBeginData = await fetch(`/api/journal/update?journal=${journalKey}`, {
		headers: {
			'X-XSRF-TOKEN': csrfToken()
		}
	});
	
	if(!journalBeginData.ok) {
		return;
	}
	
	const result = await journalBeginData.json();
	
	return result;
}

/*
 * 거래 수정 모달 오픈
 */
function journalUpdateModalOpen(data) {
	console.log(data)
	const dateObj = new Date(data.transactionDate);
	
	const containerButtonActive = (type) => {
		return data.transactionType === type ? 'active' : '';
	}
	
	const state = {
		journalId: data.id,
		ledgerId: data.ledgerId,
		dateStr: dateFormat(dateObj),
		dateViewMonth: dateObj.getMonth(),
		dateViewYear: dateObj.getFullYear(),
		debitMessage: data.transactionType === 'INCOME' ? '돈이 어디로 이동했나요?' : '돈을 어디에 쓰셨나요?',
		creditMessage: data.transactionType === 'INCOME' ? '돈이 들어온 수입처를 선택하세요.' : '지출하신 항목을 선택하세요.',
	}
	
	const entry = data.entryLines.reduce((acc, curr) => {
		const key = curr.type.toLowerCase();
		acc[key] = curr;
		return acc;
	}, {});
	
	const entryObject = (accountId) => {
		return categoryMetadata.accounts[0].categories.find(cat => {
			const account = cat.account.find(acc => {
				if(acc.accountId === accountId) {
					return acc;
				}
			});
			return account;
		})
	};
	
	const debitObject = entryObject(entry.debit.accountId);
	const creditObject = entryObject(entry.credit.accountId);
	
	const selectedEntryName = (obj, accountId) => {
		return obj.account.reduce((prev, curr) => {
			if(curr.accountId === accountId) {
				return `(${obj.categoryName}) - ${curr.accountName}`;
			}
			return prev;
		}, "")
	}
	
	const selectedDebitName = selectedEntryName(debitObject, entry.debit.accountId);
	const selectedCreditName = selectedEntryName(creditObject, entry.credit.accountId);
	
	const getOptions = (types) => {
		const typeList = Array.isArray(types) ? types : [types];
		const filterCategories = categoryMetadata.accounts[0].categories.filter(cat => typeList.includes(cat.categoryName));
		
		return filterCategories.map(cat => {
			return cat.account
				.filter(acc => acc.payment === true)
				.map(acc => `
					<span class="dropdown-item" data-value="${acc.accountId}" data-event="dropdownSelect">(${cat.categoryName}) - ${acc.accountName}</span>
				`).join('');
		}).join('') || '<span class="dropdown-item">항목 없음</span>';
	};
	
	const debitOptions = getOptions(['자산']);
	const creditOptions = getOptions(['수입']);
	
	const html = `
	<input type="hidden" value="journalUpdate" id="modal-event">
	<input type="hidden" value="${state.journalId}" id="journalKey">
	<div class="modal-container-l modal-shadow">
		<div class="modal-header p-l fc-text">
			<div class="container-header-icon">
				<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
					<path d="M12.8995 6.85453L17.1421 11.0972L7.24264 20.9967H3V16.754L12.8995 6.85453ZM14.3137 5.44032L16.435 3.319C16.8256 2.92848 17.4587 2.92848 17.8492 3.319L20.6777 6.14743C21.0682 6.53795 21.0682 7.17112 20.6777 7.56164L18.5563 9.68296L14.3137 5.44032Z"></path>
				</svg>
			</div>
			<span>거래 수정</span>
		</div>
		<div class="line"></div>
		<div class="modal-body">
			<div class="input-group">
				<span id="debitMessage">${state.debitMessage}</span>
				<div class="dropdown-wrapper" data-event="journalUpdateDropdown">
					<div class="dropdown-trigger">
						<span class="dropdown-text" data-selected="${entry.debit.accountId}" id="debit">${selectedDebitName}</span>
						<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
			        <path d="M11.9999 10.8284L7.0502 15.7782L5.63599 14.364L11.9999 8L18.3639 14.364L16.9497 15.7782L11.9999 10.8284Z"></path>
			      </svg>
						<div class="dropdown">
							${debitOptions}
						</div>
					</div>
				</div>
			</div>
			
			<div class="input-group">
				<span id="creditMessage">${state.creditMessage}</span>
				<div class="dropdown-wrapper" data-event="journalUpdateDropdown">
					<div class="dropdown-trigger">
						<span class="dropdown-text" data-selected="${entry.credit.accountId}" id="credit">${selectedCreditName}</span>
						<svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
			        <path d="M11.9999 10.8284L7.0502 15.7782L5.63599 14.364L11.9999 8L18.3639 14.364L16.9497 15.7782L11.9999 10.8284Z"></path>
			      </svg>
						<div class="dropdown">
							${creditOptions}
						</div>
					</div>
				</div>
			</div>
			
			<div class="input-group">
				<span>거래 금액</span>
				<input type="text" value="${data.totalAmount}" id="amount">
			</div>
			
			<div class="input-group">
				<span>거래 날짜</span>
				<div class="datepicker-wrapper"
				     data-selected-date="${state.dateStr}"
						 data-view-month="${state.dateViewMonth}"
						 data-view-year="${state.dateViewYear}"
						 id="journalUpdateDatepicker">
					<button class="datepicker-trigger" data-event="journalUpdateDatepicker">
						<span class="datepicker-text">${state.dateStr}</span>
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
			
			<div class="input-group">
				<span>거래 메모</span>
				<textarea id="memo">${data.memo}</textarea>
			</div>
		</div>
		<div class="modal-action">
			<button class="btn btn-primary" data-event="journalUpdateRequest">거래 수정</button>
			<button class="btn" data-event="modal-close">취소</button>
		</div>
	</div>
	`
	
	modalOpen(html)
}

/*
 * Modal 이벤트 위임
 */
const recentDetailModalContainer = document.querySelector('.modal');
if(recentDetailModalContainer) {
	recentDetailModalContainer.addEventListener('click', (e) => {
		const event = e.target.closest('[data-event]');
		
		if(event) {
			const eventKey = event.dataset.event;
			
			switch (eventKey) {
				case 'journalUpdateDatepicker' :
					handleDateFilterClick(e);
					break;
				case 'datepickerClose' :
					calendarClose(e.target.closest('#journalUpdateDatepicker'));
					break;
					
				case 'journalUpdateDropdown' :
					journalDropdownClick(e.target.closest('.dropdown-wrapper'))
					break;
				
				case 'dropdownSelect' :
					journalDropdownClick(e.target.closest('.dropdown-wrapper'), true, e)
					break;
					
				case 'journalUpdateRequest' :
					journalUpdateRequest();
					break;
			}
		}
	});
}

function journalUpdateRequest() {
	const request = {
		transactionType: modal.querySelector('.container-btn.active').dataset.type,
		debitAccountId: modal.querySelector('#debit').dataset.selected,
		creditAccountId: modal.querySelector('#credit').dataset.selected,
		memo: modal.querySelector('#memo').value,
		transactionDate: modal.querySelector('#journalUpdateDatepicker').dataset.selectedDate,
		journal: modal.querySelector('#journalKey').value,
		totalAmount: modal.querySelector('#amount').value
	}
	
	fetch('/api/journal/update', {
		method: 'PATCH',
		headers: {
			'Content-Type': 'application/json',
			'X-XSRF-TOKEN': csrfToken()
		},
		body: JSON.stringify(request)
	})
	.then(res => res.json())
	.then(data => console.log(data));
}

function journalDropdownClick(wrapper, isItem = false, e = null) {
	const trigger = wrapper.querySelector('.dropdown-trigger');
	
	trigger.classList.toggle('dropdown-active');
	
	if(isItem) {
		const item = e.target.closest('.dropdown-item');
		const dropStorage = trigger.querySelector('.dropdown-text');
		dropStorage.setAttribute('data-selected', item.dataset.value);
		dropStorage.textContent = item.textContent;
		// TODO: 2026-04-21 : 거래입력한 내역 수정하는 API 만들고 소비, 수입을 선택하는 건 삭제해야 할 듯.
		// 소비, 수입 삭제에 따른 journalUpdateModalOpen 함수 내부 getOptions 파라미터 전달 방식 수정
		
		// 2026-04-22에 자고 일어나서 할 것
		// Update API는 완성했고 테스트도 끝남 근데 해당 페이지 UI 개선사항을 제미나이가 말해줬는데
		
	}
}

/*
 * =========================================
 * Ledger dropdown 처리
 * =========================================
 */
async function handleLedgerDropdownClick(e, wrapper) {
  const trigger = wrapper.querySelector('.dropdown-trigger');
  const item = e.target.closest('.dropdown-item');

  trigger.classList.toggle('dropdown-active');

  if (!item) {
    return;
  }

  state.ledgerId = item.dataset.value;
  state.ledgerType = item.dataset.type;
  state.ledgerName = item.textContent.trim();

  elements.dropdownText.dataset.selected = state.ledgerId;
  elements.dropdownText.textContent = state.ledgerName;

  updateLedgerBadge();

  await searchJournalList();
}

/*
 * =========================================
 * 옵션 dropdown 처리
 * =========================================
 *
 * 기존 코드의 optionDropdown을 유지하되,
 * 어떤 state key를 바꿀지 명시적으로 받도록 변경했다.
 */
function handleOptionDropdown(e, stateKey) {
  const trigger = e.target.closest('.dropdown-wrapper .dropdown-trigger');
  if (!trigger) {
    return;
  }

  trigger.classList.toggle('active');

  const item = e.target.closest('.dropdown-item');
  if (!item) {
    return;
  }

  trigger.classList.remove('active');
  trigger.dataset.selected = item.dataset.value;
  trigger.querySelector('.dropdown-text').textContent = item.textContent;

  state[stateKey] = item.dataset.value;
	
	if (stateKey === 'sortedOption') {
	    state.offset = 0; 
	    searchJournalList();
	  }
}

/*
 * =========================================
 * 검색 실행
 * =========================================
 *
 * 검색 버튼을 누르면 항상 처음부터 다시 조회한다.
 */
async function handleSearchRun() {
  syncDateStateFromDom();
  await searchJournalList();
}

/*
 * =========================================
 * 더보기 처리
 * =========================================
 *
 * 기존 코드는 버튼 dataset에서 nextUrl을 직접 읽었는데,
 * 이제는 state.nextURL을 기준으로 요청한다.
 */
async function handleLoadMore() {
  if (state.loading || !state.hasNext || !state.nextURL) {
    return;
  }

  state.loading = true;

  try {
    const journalResponse = await fetchNextJournalList(state.nextURL);
    if (!journalResponse) {
      return;
    }

    appendJournalList(journalResponse);
    updatePaginationState(journalResponse);
  } finally {
    state.loading = false;
  }
}

/*
 * =========================================
 * 기본 검색 요청
 * =========================================
 *
 * 목록을 처음부터 다시 가져오는 공통 함수
 */
async function searchJournalList() {
  if (state.loading) {
    return;
  }

  state.loading = true;

  try {
    syncDateStateFromDom();

		const request = buildJournalRequest();
		
    const journalResponse = await fetchJournalList(request);
    if (!journalResponse) {
      return;
    }

    replaceJournalList(journalResponse);
    updatePaginationState(journalResponse);
  } finally {
    state.loading = false;
  }
}

/*
 * =========================================
 * Request 생성
 * =========================================
 */
function buildJournalRequest() {
  return {
    ledgerId: state.ledgerId,
    startDate: state.startDate,
    endDate: state.endDate,
    offset: 0,
    searchOption: state.searchOption,
    sortedOption: state.sortedOption,
		keyword: state.keyword
  };
}

/*
 * =========================================
 * URL 생성
 * =========================================
 *
 * 나중에 커서 기반으로 바꾸더라도 이 함수만 바꾸면 된다.
 */
function buildJournalUrl(request) {
  const params = new URLSearchParams({
    ledger: request.ledgerId,
    startDate: request.startDate,
    endDate: request.endDate,
    offset: String(request.offset),
    'search-option': request.searchOption,
    'sorted-option': request.sortedOption,
		keyword: request.keyword,
  });

  return `/api/journal/search?${params.toString()}`;
}

/*
 * =========================================
 * API 요청
 * =========================================
 */
async function fetchJournalList(request) {
  const url = buildJournalUrl(request);

  const response = await fetch(url, {
    headers: {
      'X-XSRF-TOKEN': csrfToken()
    }
  });

  if (!response.ok) {
    return null;
  }

  return await response.json();
}

async function fetchNextJournalList(url) {
  const response = await fetch(url, {
    headers: {
      'X-XSRF-TOKEN': csrfToken()
    }
  });

  if (!response.ok) {
    return null;
  }

  return await response.json();
}

/*
 * =========================================
 * DOM -> state 동기화
 * =========================================
 *
 * datepicker는 외부 함수가 DOM 속성을 바꾸는 구조라서
 * 지금은 그 결과를 state에 반영하는 방식으로 간다.
 */
function syncDateStateFromDom() {
  state.startDate = elements.startDateRoot?.dataset.selectedDate ?? state.startDate;
  state.endDate = elements.endDateRoot?.dataset.selectedDate ?? state.endDate;
  state.startViewMonth = elements.startDateRoot?.dataset.viewMonth ?? state.startViewMonth;
}

/*
 * =========================================
 * Ledger 뱃지 업데이트
 * =========================================
 */
function updateLedgerBadge() {
  if (!elements.ledgerBadge) {
    return;
  }

  elements.ledgerBadge.dataset.type = state.ledgerType;
  elements.ledgerBadge.textContent =
    state.ledgerType === 'PERSONAL' ? '개인 가계부' : '공유 가계부';
}

/*
 * =========================================
 * 목록 전체 교체 렌더링
 * =========================================
 *
 * 최초 검색, 필터 변경, ledger 변경 시 사용
 */
function replaceJournalList(journalResponse) {
  elements.journalContainer.innerHTML = renderJournalItems(journalResponse);
  renderLoadMoreButton(journalResponse.next, journalResponse.nextURL);
}

/*
 * =========================================
 * 목록 추가 렌더링
 * =========================================
 *
 * 더보기 시 사용
 */
function appendJournalList(journalResponse) {
  const html = renderJournalItems(journalResponse);

  /*
   * 빈 문자열이나 "거래 없음" 메시지를 더보기로 append하는 건 이상하므로
   * 실제 journal 데이터가 있을 때만 append 한다.
   */
  if (journalResponse?.journal?.length > 0) {
    elements.journalContainer.insertAdjacentHTML('beforeend', html);
  }

  renderLoadMoreButton(journalResponse.next, journalResponse.nextURL);
}

/*
 * =========================================
 * 페이지네이션 상태 갱신
 * =========================================
 */
function updatePaginationState(journalResponse) {
  state.hasNext = Boolean(journalResponse?.next);
  state.nextURL = journalResponse?.nextURL ?? null;

  /*
   * offset 기반일 때 서버가 nextURL을 만들어 내려주는 구조를 그대로 유지한다.
   * 나중에 nextOffset으로 바꾸면 여기만 바꾸면 된다.
   */
}

/*
 * =========================================
 * 거래 목록 HTML 생성
 * =========================================
 *
 * 이 함수는 "문자열 생성"에 집중한다.
 * 버튼 상태 변경 같은 DOM side effect는 여기서 하지 않는다.
 */
function renderJournalItems(journalResponse) {
  if (!journalResponse?.journal || journalResponse.journal.length < 1) {
    return `
      <div class="flex items-center main-border bg-main fc-text fs-body jus-center p-l">
        <span>❌ 거래 내역이 존재하지 않습니다.</span>
      </div>
    `;
  }

  const weekDays = ['일', '월', '화', '수', '목', '금', '토'];

  return journalResponse.journal.map(item => {
    const dateObj = new Date(item.transactionDate);
    const dayName = weekDays[dateObj.getDay()];

		console.log(dayName)
		
    return `
      <div class="entry-row" data-line-key="${item.journalId}">
        <div class="entry-row_date">
          <span id="transactionDate">${item.transactionDate}</span>
          <span class="entry-row_day">(${dayName})</span>
        </div>

        <div class="entry-row_info">
          <span class="entry-row_info_memo">${item.memo || '메모 X'}</span>
          <span class="entry-row_info_money">${moneyFormat(item.totalAmount)}</span>
        </div>

        <div class="entry-row_account">
          <div class="entry-row_account_entryline">
            <div class="ball ${item.debit.type.toLowerCase()}"></div>
            <span>${item.debit.name}</span>
          </div>
          <div class="entry-row_account_entryline">
            <div class="ball ${item.credit.type.toLowerCase()}"></div>
            <span>${item.credit.name}</span>
          </div>
        </div>

        <div class="entry-row_update">
          <div class="flex items-center justify-center p-s relative tooltip-box" data-event="journalUpdate">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M7.24264 17.9967H3V13.754L14.435 2.319C14.8256 1.92848 15.4587 1.92848 15.8492 2.319L18.6777 5.14743C19.0682 5.53795 19.0682 6.17112 18.6777 6.56164L7.24264 17.9967ZM3 19.9967H21V21.9967H3V19.9967Z"></path>
            </svg>
            <div class="tooltip bottom">
              <span>수정하기</span>
            </div>
          </div>

          <div class="flex items-center justify-center p-s relative tooltip-box" data-event="journalDelete">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="var(--color-danger)">
              <path d="M6.53451 3H20.9993C21.5516 3 21.9993 3.44772 21.9993 4V20C21.9993 20.5523 21.5516 21 20.9993 21H6.53451C6.20015 21 5.88792 20.8329 5.70246 20.5547L0.369122 12.5547C0.145189 12.2188 0.145189 11.7812 0.369122 11.4453L5.70246 3.4453C5.88792 3.1671 6.20015 3 6.53451 3ZM12.9993 10.5858L10.1709 7.75736L8.75668 9.17157L11.5851 12L8.75668 14.8284L10.1709 16.2426L12.9993 13.4142L15.8277 16.2426L17.242 14.8284L14.4135 12L17.242 9.17157L15.8277 7.75736L12.9993 10.5858Z"></path>
            </svg>
            <div class="tooltip bottom">
              <span>삭제</span>
            </div>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

/*
 * =========================================
 * 더보기 버튼 렌더링
 * =========================================
 *
 * 기존 코드에서는 버튼의 data-next-url을 직접 갱신했다.
 * 지금은 버튼은 단순한 트리거로만 두고,
 * 실제 nextURL은 state에 보관한다.
 */
function renderLoadMoreButton(hasNext) {
  const currentButton = elements.loadMoreBlock.querySelector('[data-event="load-more"]');

  if (!hasNext) {
    if (currentButton) {
      currentButton.remove();
    }
    return;
  }

  if (currentButton) {
    return;
  }

  const buttonHtml = `
    <button class="btn btn-info center w-full" data-event="load-more">
      더보기
    </button>
  `;

  elements.loadMoreBlock.insertAdjacentHTML('beforeend', buttonHtml);
}

/*
import { categoryMetadata } from '../LedgerCategoryWithAccounts.js';

console.log(categoryMetadata);

const dropdownContainer = document.querySelector('.dropdown-trigger .dropdown');
const dropdownText = document.querySelector('.dropdown-wrapper[data-drop=ledgerSelect] .dropdown-text');
const firstLedger = categoryMetadata.accounts[0];
const journalContainer = document.querySelector('[data-block=journalTransaction]');

if(firstLedger) {
	dropdownText.setAttribute('data-selected', firstLedger.ledgerId);
	dropdownText.textContent = firstLedger.ledgerName;
	
	const categoryLedgerBlock = categoryMetadata.accounts.map(ledger => `
	    <span class="dropdown-item" data-value="${ledger.ledgerId}" data-type="${ledger.type}">${ledger.ledgerName}</span>
	`).join('');
	
	dropdownContainer.insertAdjacentHTML('beforeend', categoryLedgerBlock);
}

const initDates = getDateFormat('datepickerRange');

if(initDates) {
	document.querySelector('#endDate').setAttribute('data-selected-date', initDates.endDate);
	document.querySelector('[data-event=startDatePickerOpen]').textContent = initDates.startDate;
	document.querySelector('#startDate').setAttribute('data-selected-date', initDates.startDate);
	document.querySelector('#startDate').setAttribute('data-view-month', initDates.startViewMonth);
	document.querySelector('[data-event=endDatePickerOpen]').textContent = initDates.endDate;
	
	const journalRequest = {
		startDate: document.querySelector('#startDate').getAttribute('data-selected-date'),
		endDate: document.querySelector('#endDate').getAttribute('data-selected-date'),
		offset: 0,
		searchOption: 'ALL',
		sortedOption: 'LATEST'
	}
	
	const journalTransaction = await journalTransactionRequest(journalRequest);
	
	journalContainer.insertAdjacentHTML('beforeend', journalTransactionRender(journalTransaction));
}

document.querySelector('[data-block="date-filter"]').addEventListener('click', (e) => {
	e.stopPropagation();
	
	const event = e.target.closest('[data-event]');
	
	if(!event) return;
				
	const action = event.dataset.event;
	
	switch (action) {
		case 'endDatePickerOpen' : calendarOpen(e.target.closest('#endDate')); break;
		case 'endDate-today' : datepickerMonthBtn(e.target.closest('#endDate'), 'today'); break;
		case 'endDate-currentMonth' : datepickerMonthBtn(e.target.closest('#endDate'), 'currentMonth'); break;
		case 'endDate-prevMonthBtn' : datepickerMonthBtn(e.target.closest('#endDate'), 'prev'); break;
		case 'endDate-nextMonthBtn' : datepickerMonthBtn(e.target.closest('#endDate'), 'next'); break;
		case 'endDate-datepickerClose' : calendarClose(e.target.closest('#endDate')); break;
		case 'endDate-close' : calendarClose(e.target.closest('#endDate')); break;
		case 'startDatePickerOpen' : calendarOpen(e.target.closest('#startDate')); break;
		case 'startDate-today' : datepickerMonthBtn(e.target.closest('#startDate'), 'today'); break;
		case 'startDate-currentMonth' : datepickerMonthBtn(e.target.closest('#startDate'), 'currentMonth'); break;
		case 'startDate-prevMonthBtn' : datepickerMonthBtn(e.target.closest('#startDate'), 'prev'); break;
		case 'startDate-nextMonthBtn' : datepickerMonthBtn(e.target.closest('#startDate'), 'next'); break;
		case 'startDate-datepickerClose' : calendarClose(e.target.closest('#startDate')); break;
		case 'startDate-close' : calendarClose(e.target.closest('#startDate')); break;
	}
});

document.addEventListener('click', async (e) => {
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
			
			const journalRequest = {
				startDate: document.querySelector('#startDate').getAttribute('data-selected-date'),
				endDate: document.querySelector('#endDate').getAttribute('data-selected-date'),
				offset: 0
			}
			
			const journalResponse = await journalTransactionRequest(journalRequest);
			
			journalContainer.innerHTML = journalTransactionRender(journalResponse);
		}
		return;
	}
	
	const loadMoreEvent = e.target.closest('[data-event=load-more]');
	if(loadMoreEvent) {
		const nextURL = e.target.dataset.nextUrl;
		const journalResponse = await nextJournalTransactionRequest(nextURL);
		journalContainer.insertAdjacentHTML('beforeend', journalTransactionRender(journalResponse));
		return;
	}
	
	const searchOptionDropdown = e.target.closest('[data-drop=searchFilterDropdown]');
	if(searchOptionDropdown) {
		optionDropdown(e);
		return;
	}
	
	const sortedOptionDropdown = e.target.closest('[data-drop=searchSortedDropdown]');
	if(sortedOptionDropdown) {
		optionDropdown(e);
		return;
	}
	
	const searchRun = e.target.closest('[data-event=searchRun]');
	if(searchRun) {
		const journalRequest = {
			startDate: document.querySelector('#startDate').getAttribute('data-selected-date'),
			endDate: document.querySelector('#endDate').getAttribute('data-selected-date'),
			offset: 0
		}
		
		const journalResponse = await journalTransactionRequest(journalRequest);
					
		journalContainer.innerHTML = journalTransactionRender(journalResponse);
		return;
	}
});

function optionDropdown(e) {
	const parent = e.target.closest('.dropdown-wrapper .dropdown-trigger');
	
	if(parent.classList.contains('active')) {
		parent.classList.remove('active');
	} else {
		parent.classList.add('active');			
	}
	
	if(e.target.closest('.dropdown-item')) {
		const target = e.target.closest('.dropdown-item');
		
		parent.classList.remove('active');
		parent.setAttribute('data-selected', target.dataset.value);
		parent.querySelector('.dropdown-text').textContent = target.textContent;
		return;
	}
}

async function nextJournalTransactionRequest(url) {
	const journal = await fetch(url, {
		headers: {
			'X-XSRF-TOKEN': csrfToken()
		}
	})
	
	if(!journal.ok) {
		return;
	}
	
	return await journal.json();
}

async function journalTransactionRequest(journalRequest) {
	// TODO Default Request
	const ledger = document.querySelector('[data-drop=ledgerSelect] .dropdown-text').dataset.selected;
	
	const searchOption = document.querySelector('#searchOption').dataset.selected;
	const sortedOption = document.querySelector('#sortedOption').dataset.selected;
	
	const url = `
		/api/journal/search?
		ledger=${ledger}
		&startDate=${journalRequest.startDate}
		&endDate=${journalRequest.endDate}
		&offset=${journalRequest.offset}
		&search-option=${searchOption}
		&sorted-option=${sortedOption}
	`.replace(/\s/g, '');
	
	const journal = await fetch(url, {
		headers: {
			'X-XSRF-TOKEN': csrfToken()
		}
	})
	
	if(!journal.ok) {
		return;
	}
	
	return await journal.json();
}

function journalTransactionRender(journalResponse) {
	console.log(journalResponse)

	if(journalResponse.journal.length < 1) {
		return `
		<div class="flex items-center main-border bg-main fc-text fs-body jus-center p-l">
			<span>❌ 거래 내역이 존재하지 않습니다.</span>
		</div>
		`;
	}
		
	const weekDays = ['일', '월', '화', '수', '목', '금', '토'];
	
	let html = journalResponse.journal.map(item => {
		
		const dateObj = new Date(item.transactionDate);
		
		const dayName = weekDays[dateObj.getDay()];
		
		return `
		<div class="entry-row">
			<div class="entry-row_date">
				<span>${item.transactionDate}</span>
				<span class="entry-row_day">(${dayName})</span>
			</div>
			<div class="entry-row_info">
				<span class="entry-row_info_memo">${item.memo || '메모 X'}</span>
				<span class="entry-row_info_money">${moneyFormat(item.totalAmount)}</span>
			</div>
			<div class="entry-row_account">
				<div class="entry-row_account_entryline">
					<div class="ball ${item.debit.type.toLowerCase()}"></div>
					<span>${item.debit.name}</span>
				</div>
				<div class="entry-row_account_entryline">
					<div class="ball ${item.credit.type.toLowerCase()}"></div>
					<span>${item.credit.name}</span>
				</div>
			</div>
			
			<div class="entry-row_update">
				<div class="flex items-center justify-center p-s relative tooltip-box">
					<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M7.24264 17.9967H3V13.754L14.435 2.319C14.8256 1.92848 15.4587 1.92848 15.8492 2.319L18.6777 5.14743C19.0682 5.53795 19.0682 6.17112 18.6777 6.56164L7.24264 17.9967ZM3 19.9967H21V21.9967H3V19.9967Z"></path></svg>
					<div class="tooltip bottom">
						<span>수정하기</span>
					</div>									
				</div>
				<div class="flex items-center justify-center p-s relative tooltip-box">
					<svg viewBox="0 0 24 24" width="18" height="18" fill="var(--color-danger)"><path d="M6.53451 3H20.9993C21.5516 3 21.9993 3.44772 21.9993 4V20C21.9993 20.5523 21.5516 21 20.9993 21H6.53451C6.20015 21 5.88792 20.8329 5.70246 20.5547L0.369122 12.5547C0.145189 12.2188 0.145189 11.7812 0.369122 11.4453L5.70246 3.4453C5.88792 3.1671 6.20015 3 6.53451 3ZM12.9993 10.5858L10.1709 7.75736L8.75668 9.17157L11.5851 12L8.75668 14.8284L10.1709 16.2426L12.9993 13.4142L15.8277 16.2426L17.242 14.8284L14.4135 12L17.242 9.17157L15.8277 7.75736L12.9993 10.5858Z"></path></svg>
					<div class="tooltip bottom">
						<span>삭제</span>
					</div>
				</div>
			</div>
		</div>
		`
	}).join('');
	
	loadMoreButtonRender(journalResponse.next, journalResponse.nextURL)
	
	return html;
}

function loadMoreButtonRender(bool, nextURL) {
	const loadMoreBlock = document.querySelector('[data-block=load-more]');
	const currentLoadBtn = loadMoreBlock.querySelector('[data-event=load-more]');
	if(currentLoadBtn) {
		if(!bool) {
			loadMoreBlock.removeChild(currentLoadBtn);
			return;
		}
		
		currentLoadBtn.setAttribute('data-next-url', nextURL);
		return;
	}
	
	if(bool) {
		const btn = `<button class="btn btn-info center w-full" data-event="load-more" data-next-url="${nextURL}">더보기</button>`;
		loadMoreBlock.insertAdjacentHTML('beforeend', btn);
	} else {
		loadMoreBlock.innerHTML = '';
	}
}
*/