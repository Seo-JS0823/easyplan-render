const modalOverlay = document.querySelector('.modal-overlay');
const modal = document.querySelector('.modal');

if(modalOverlay) {
	modalOverlay.addEventListener('keydown', (e) => {
		if(e.key === 'Escape') {
			modalClose();
			return;
		}
	});
}

if(modal) {
	modal.addEventListener('keydown', (e) => {
		if(e.key !== 'Enter' || e.isComposing) return;
		
		const tagIgnore = ['BUTTON', 'A', 'SELECT'];
		if(tagIgnore.includes(e.target.tagName)) return;
		
		const event = modal.querySelector('#event').value;
		
		console.log(event);
		
		if(event) {
			switch (event) {
				case 'login' : loginRequest(); break;
				
				case 'join' : joinRequest(); break;
				
				case 'nickname' : updateNicknameRequest(); break;
				
				case 'passwordStep1' : currentPasswordMatch(); break;
				
				case 'passwordStep2' : updatePasswordRequest(); break;
				
				case 'updateProfile' : updateProfileRequest(); break;
				
				case 'updateSetting' : updateSettingRequest(); break;
			}			
		}
	});
	
  modal.addEventListener('click', (e) => {
    e.stopPropagation();

    const el = e.target.closest('[data-event]');
    
    if(!el) return;
    
    const action = el.dataset.event;
    
    switch (action) {
      case 'modal-close' : modalClose(); break;

      case 'login' : loginRequest(); break;

      case 'gender-pick' : genderPicker(el, e.target); break;
			
			case 'join' : joinRequest(); break;
			
			case 'updateProfile' : updateProfileRequest(); break;
			
			case 'updateSetting' : updateSettingRequest(); break;
			
			case 'passwordStep1' : currentPasswordMatch(); break;
    }
  });
};

function modalOpen(content = undefined, classes = []) {
  if(content) {
    modal.innerHTML = content;
    if(classes.length > 0) {
      for(let i = 0; i < classes.length; i++) {
        modal.classList.add(classes[i]);
      }
    }
    modalOverlay.classList.add('open');
		const focus = modalOverlay.querySelector('[data-focus=focus]');
		if(focus) {
			focus.focus();
		} else {
			modal.focus();
		}
  }
}

function modalClose() {
  modal.className = 'modal';
  modal.innerHTML = '';
  modalOverlay.classList.remove('open');
}

function genderPicker(el, target) {
  const btn = target.closest('.radio');
  if (!btn) return;

  const genderBtns = el.querySelectorAll('.radio');
  genderBtns.forEach(b => b.classList.remove('active'));

  btn.classList.add('active');
}

async function joinRequest() {
	const user = {
		email: modal.querySelector('#joinEmail').value,
		password: modal.querySelector('#joinPassword').value,
		nickname: modal.querySelector('#joinNickname').value,
		notification: modal.querySelector('#notification').checked,
		emailNotification: modal.querySelector('#emailNotification').checked
	}
	
	if(!user.email) {
		toastModal('사용하실 아이디를 입력해주세요.');
		return;
	} else if(!user.password) {
		toastModal('사용하실 비밀번호를 입력해주세요.');
		return;
	} else if(!user.nickname) {
		toastModal('사용하실 닉네임을 입력해주세요.');
		return;
	}
	
	const response = await fetch('/api/user/join', {
		method: 'post',
		headers: {
			'Content-Type':'application/json',
			'X-XSRF-TOKEN': csrfToken()
		},
		body: JSON.stringify(user)
	})
	
	if(!response.ok) {
		const error = await response.json();
		toastModal(error);
		return;
	}
	
	const result = await response.json();
	
	toastModal(result);
}

async function loginRequest() {
	const user = {
		email: modal.querySelector('#loginEmail').value,
		password: modal.querySelector('#loginPassword').value
	}
	
	if(!user.email) {
		toastModal('아이디를 입력해주세요.');
		return;
	} else if(!user.password) {
		toastModal('비밀번호를 입력해주세요.');
		return;
	}
	
	const response = await fetch('/api/user/login', {
		method: 'post',
		headers: {
			'Content-Type':'application/json',
			'X-XSRF-TOKEN': csrfToken(),
			'Time-Zone': timeZone(),
		},
		body: JSON.stringify(user)
	});
	
	if(!response.ok) {
		const error = await response.json();
		toastModal(error);
		return;
	}
	
	window.location.href = baseUrl + 'ledger/dashboard';
}

async function updateNicknameRequest() {
	const user = {
		update: 'NICKNAME',
		newNickname: modal.querySelector('#newNickname').value
	}
	
	const response = await fetch('/api/user/update', {
		method: 'PATCH',
		headers: {
			'Content-Type':'application/json',
			'X-XSRF-TOKEN': csrfToken()
		},
		body: JSON.stringify(user)
	})
	
	if(!response.ok) {
		const error = await response.json();
		toastModal(error);
		return;
	}
	
	const result = await response.json();
	
	toastModal(result);
}

async function currentPasswordMatch() {
	const user = {
		currentPassword: modal.querySelector('#currentPassword').value
	}
	
	if(!user.currentPassword) {
		toastModal('현재 사용하고계신 비밀번호를 입력해주세요.');
		return;
	}
	
	const response = await fetch('/api/user/match', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'X-XSRF-TOKEN': csrfToken(),
		},
		body: JSON.stringify(user)
	})
	
	if(!response.ok) {
		const error = await response.json();
		toastModal(error);
		return;
	}
	
	const result = await response.json();
	
	if(result.success) {
		modalOpen(passwordUpdateStep2Modal(user.currentPassword));
	}
}

async function updatePasswordRequest() {
	const user = {
		update: 'PASSWORD_HASH',
		currentPassword: modal.querySelector('#currentPassword').value,
		newPassword: modal.querySelector('#newPassword').value
	}
	
	const newPasswordCheck = modal.querySelector('#newPasswordCheck').value;
	
	if(user.newPassword !== newPasswordCheck) {
		toastModal('비밀번호가 일치하지 않습니다.');
		return;
	}
	
	const response = await fetch('/api/user/update', {
		method: 'PATCH',
		headers: {
			'Content-Type':'application/json',
			'X-XSRF-TOKEN': csrfToken(),
		},
		body: JSON.stringify(user)
	});
	
	if(!response.ok) {
		const error = await response.json();
		toastModal(error);
		return;
	}
	
	const result = await response.json();
	
	toastModal(result);
}

function updateProfileRequest() {
	const user = {
		update: 'USER_PROFILE',
		profilePublic: document.querySelector('#profile-public').checked,
		friendRequest: document.querySelector('#friend-request').checked,
		searchAllowed: document.querySelector('#search-user').checked
	}
	
	console.log(user);
	
	fetch('/api/user/update', {
		method: 'PATCH',
		headers: {
			'Content-Type': 'application/json',
			'X-XSRF-TOKEN': csrfToken(),
		},
		body: JSON.stringify(user)
	})
	.then(res => res.json())
	.then(data => {
		console.log(data);
	});
}

function updateSettingRequest() {
	const user = {
		update: 'USER_SETTING',
		notification: document.querySelector('#notification').checked,
		emailNotification: document.querySelector('#email-notification').checked
	}
	
	console.log(user);
	
	fetch('/api/user/update', {
		method: 'PATCH',
		headers: {
			'Content-Type': 'application/json',
			'X-XSRF-TOKEN': csrfToken(),
		},
		body: JSON.stringify(user)
	})
	.then(res => res.json())
	.then(data => {
		console.log(data);
	});
}