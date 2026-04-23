const baseUrl = 'https://easyplan.onrender.com/';

function csrfToken() {
	if(document.cookie) {
		const value = `; ${document.cookie}`;
		const parts = value.split(`; XSRF-TOKEN=`);
		
		if(parts.length === 2) {
			return parts.pop().split(';').shift();
		}
	}
	return '';
}

function debounce(func, timeout = 500) {
	let timer;
	return (...args) => {
		clearTimeout(timer);
		timer = setTimeout(() => { func.apply(this, args); }, timeout);
	}
}

function timeZone() {
	const userTimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
	return userTimeZone;
}

function toastModal(response) {
	let message;
	if(typeof response === 'string') {
		message = response;
	} else if(typeof response === 'object') {
		message = response.message;		
	}
	
	if(message) {
		toastModalOpen(message);		
	}
}

function toastModalOpen(message) {
	const html = `
	<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
		<path d="M12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12C22 17.5228 17.5228 22 12 22ZM11 15V17H13V15H11ZM11 7V13H13V7H11Z"></path>
	</svg>
	<span>${message}</span>
	`;
	
	const toast = document.querySelector('.toast');
	
	if(toast) {
		toast.innerHTML = html;
	}
	
	toast.classList.add('open');
	
	setTimeout(() => {
		toast.classList.remove('open');
	}, 3000)
}

function dateFormat(date) {
	const yyyy = date.getFullYear();
	const mm = String(date.getMonth() + 1).padStart(2, '0');
	const dd = String(date.getDate()).padStart(2, '0');
	
	return `${yyyy}-${mm}-${dd}`;
}

function getDateFormat(option = '') {
	const date = new Date();
	const yyyy = date.getFullYear();
	const mm = String(date.getMonth() + 1).padStart(2, '0');
	const dd = String(date.getDate()).padStart(2, '0');

	let formatted;
	if(option === 'yyyy-mm') {
		formatted = `${yyyy}-${mm}`;
	} else if(option === 'yyyy') {
		formatted = `${yyyy}`;
	} else if(option === 'mm월') {
		formatted = `${mm} 월`;
	} else if(option === 'datepickerRange') {
    const endDate = `${yyyy}-${mm}-${dd}`;

    const prevMonthDate = new Date();
    prevMonthDate.setMonth(date.getMonth() - 1);

    if (date.getDate() !== prevMonthDate.getDate()) {
      prevMonthDate.setDate(0);
    }

    const prevY = prevMonthDate.getFullYear();
    const prevM = String(prevMonthDate.getMonth() + 1).padStart(2, '0');
    const prevD = String(prevMonthDate.getDate()).padStart(2, '0');
    const startDate = `${prevY}-${prevM}-${prevD}`;
		const startViewMonth = prevMonthDate.getMonth();
		
    return { startDate, endDate, startViewMonth };
	} else {
		formatted = `${yyyy}-${mm}-${dd}`
	}
	return formatted;
}

function getMonthRange(month) {
	const now = new Date();
  const year = now.getFullYear();
  
  const startDate = new Date(year, month - 1, 1);
  
  const endDate = new Date(year, month, 0);

  const formatDate = (date) => {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  };

  return {
    startDate: formatDate(startDate),
    endDate: formatDate(endDate)
  };
}

function moneyFormat(money) {
	if(!money) {
		return '0';
	}
	
	return money.toLocaleString();
}

function calendarOpen(wrapper) {
	renderCalendar(wrapper);
	
	if(wrapper.classList.contains('active')) {
		wrapper.classList.remove('active');
		return;
	}
	
	wrapper.classList.add('active');
}

function calendarClose(wrapper) {
	const trigger = wrapper.querySelector('.datepicker-trigger');
	trigger.innerHTML = wrapper.dataset.selectedDate || '날짜 선택';
	wrapper.classList.remove('active');
}

function renderCalendar(wrapper) {
	const dateFormat = (dateObj) => {
		const yyyy = dateObj.getFullYear();
		const mm = String(dateObj.getMonth() + 1).padStart(2, '0');
		const dd = String(dateObj.getDate()).padStart(2, '0');
		
		return `${yyyy}-${mm}-${dd}`;
	}
	
	const datasetYear = (viewYear) => {
		wrapper.dataset.viewYear = viewYear;
	}
	
	const datasetMonth = (viewMonth) => {
		wrapper.dataset.viewMonth = viewMonth;
	}
	
	const datasetSelectedDate = (selectedDate) => {
		wrapper.dataset.selectedDate = selectedDate;
	}
	
	const startOfDay = (date) => {
		return new Date(date.getFullYear(), date.getMonth(), date.getDate());
	}
	
	const isSameDate = (a, b) => {
		if(!a || !b) return false;
		return (
			a.getFullYear() === b.getFullYear() &&
			a.getMonth() === b.getMonth() &&
			a.getDate() === b.getDate()
		);
	}
	
	const dsYear = wrapper.dataset.viewYear;
	const dsMonth = wrapper.dataset.viewMonth;
	
	let viewYear = (dsYear !== undefined) ? parseInt(dsYear) : new Date().getFullYear();
	
	let viewMonth = (dsMonth !== undefined) ? parseInt(dsMonth) : new Date().getMonth();
	datasetYear(viewYear);
	datasetMonth(viewMonth);
	
	const today = startOfDay(new Date());
	
	const title = wrapper.querySelector('.datepicker-title');
	const daysContainer = wrapper.querySelector('.datepicker-days');
	daysContainer.innerHTML = '';
	
	title.textContent = `${viewYear}년 ${viewMonth + 1}월`;
	
	const firstDay = new Date(viewYear, viewMonth, 1);
	const firstWeekday = firstDay.getDay();
	const daysInMonth = new Date(viewYear, viewMonth + 1, 0).getDate();
	const prevMonthDays = new Date(viewYear, viewMonth, 0).getDate();
	
	const cells = [];
	
	for(let i = firstWeekday - 1; i >= 0; i--) {
		const date = new Date(viewYear, viewMonth - 1, prevMonthDays - i);
		cells.push({ date, outside: true });
	}
	
	for(let day = 1; day <= daysInMonth; day++) {
		const date = new Date(viewYear, viewMonth, day);
		cells.push({ date, outside: false });
	}
	
	const remain = 42 - cells.length;
	for(let day = 1; day <= remain; day++) {
		const date = new Date(viewYear, viewMonth + 1, day);
		cells.push({ date, outside: true });
	}
	
	cells.forEach(({ date, outside }) => {
		const btn = document.createElement('button');
    btn.type = 'button';
    btn.className = 'datepicker-day';
    btn.textContent = String(date.getDate());
		
		const currentSelectedDate = wrapper.dataset.selectedDate;
		
		if(currentSelectedDate === dateFormat(date)) {
			btn.classList.add('is-selected');
		}
		
		if(outside) {
			btn.classList.add('is-outside');
		}
		
		if(isSameDate(date, today)) {
			btn.classList.add('is-today');
		}
		
		btn.addEventListener('click', (e) => {
			e.stopPropagation();
			
			datasetSelectedDate(dateFormat(date));
			
			if(outside) {
				datasetYear(date.getFullYear());
				datasetMonth(date.getMonth());
				renderCalendar(wrapper);
			} else {
				renderCalendar(wrapper);
			}
		});
		daysContainer.appendChild(btn);
	});
	wrapper.querySelector('.datepicker-selected-text').textContent = wrapper.dataset.selectedDate || '선택된 날짜 없음';
}

function datepickerMonthBtn(wrapper, option = '') {
	let currentYear = parseInt(wrapper.dataset.viewYear);
	let currentMonth = parseInt(wrapper.dataset.viewMonth);
	
	let newDate;
	
	if(option === 'prev') {
		newDate = new Date(currentYear, currentMonth - 1, 1);
	} else if(option === 'next') {
		newDate = new Date(currentYear, currentMonth + 1, 1);
	} else if(option === 'today') {
		wrapper.dataset.selectedDate = getDateFormat();
		wrapper.dataset.viewYear = new Date().getFullYear();
		wrapper.dataset.viewMonth = new Date().getMonth();
		renderCalendar(wrapper);
		return;
	} else if(option === 'currentMonth') {
		const cmd = new Date();
		wrapper.dataset.selectedDate = dateFormatObj(new Date(cmd.getFullYear(), cmd.getMonth(), 1));
		wrapper.dataset.viewYear = cmd.getFullYear();
		wrapper.dataset.viewMonth = cmd.getMonth();
		renderCalendar(wrapper);
		return;
	}
	
	wrapper.dataset.viewYear = newDate.getFullYear();
	wrapper.dataset.viewMonth = newDate.getMonth();
	renderCalendar(wrapper);
}

function dateFormatObj(date) {
	const yyyy = date.getFullYear();
	const mm = String(date.getMonth() + 1).padStart(2, '0');
	const dd = String(date.getDate()).padStart(2, '0');
	
	return `${yyyy}-${mm}-${dd}`;
}










