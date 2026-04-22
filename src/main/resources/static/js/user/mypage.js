const mypageEl = document.querySelector('.mypage');

if(mypageEl) {
	// click 
	mypageEl.addEventListener('click', (e) => {
		if(e.target.tagName === 'INPUT') return;
		
		const el = e.target.closest('[data-event]');
		
		if(!el) return;
		
		const action = el.dataset.event;
		
		switch (action) {
			case 'nicknameUpdate' : nicknameUpdate(el); break;
			
			case 'passwordUpdate' : passwordUpdate(el); break;
		}
	});
	
	// change
	mypageEl.addEventListener('change', (e) => {
		if(e.target.id === 'profile-upload') {
			console.log(e.target)
			profileUpload(e.target);
		}
	});
}

function profileUpload(el) {
	const file = el.files[0];
	if(!file) return;
	
	const reader = new FileReader();
	reader.onload = (e) => {
		const imgEl = document.querySelector('.profile-image img');
		if(imgEl) imgEl.src = e.target.result;
	};
	
	reader.readAsDataURL(file);
	
	console.log('서버로 보낼 File', file.name);
}

function nicknameUpdate(el) {
	modalOpen(nicknameUpdateModal());
}

function passwordUpdate(el) {
	modalOpen(passwordUpdateStep1Modal());
}

const updateProfileBtn = document.querySelector('#updateProfile');
if(updateProfileBtn) {
	updateProfileBtn.addEventListener('click', () => {
		modalOpen(profileUpdateModal());
	});
}

const updateSettingBtn = document.querySelector('#updateSetting');
if(updateSettingBtn) {
	updateSettingBtn.addEventListener('click', () => {
		modalOpen(settingUpdateModal());
	});
}

const mypageCards = document.querySelectorAll('.mypage-card-header');

mypageCards.forEach(card => {
	card.addEventListener('click', () => {
		card.classList.toggle('active');
	});
})