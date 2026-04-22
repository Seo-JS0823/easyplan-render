function nicknameUpdateModal() {
	const html = `
	<input type="hidden" id="event" value="nickname">
	<div class="modal-container-l modal-shadow">
		<div class="modal-header between p-l fc-text">
			<span>닉네임 변경</span>
		</div>
		<div class="line"></div>
		<div class="modal-body">
			<div class="input-group">
				<span>닉네임</span>
				<input type="text" placeholder="변경할 닉네임을 입력하세요." id="newNickname" data-focus="focus">
			</div>
			<span class="input-description fc-caption">• 닉네임은 2~10 자로 설정해주세요.</span><br>
			<span class="input-description fc-caption">• 닉네임은 30일에 한 번 변경할 수 있습니다.</span>
		</div>
		<div class="modal-action">
			<button class="btn btn-success" data-event="updateNickname">닉네임 변경</button>
			<button class="btn btn-neutral center" data-event="modal-close">닫기</button>
		</div>
	</div>
	<div class="modal-toast"></div>
	`;
	
	return html;
}

function passwordUpdateStep1Modal() {
	const html = `
	<input type="hidden" id="event" value="passwordStep1">
	<div class="modal-container-l modal-shadow">
		<div class="modal-header between p-l fc-text">
			<span>비밀번호 변경</span>
		</div>
		<div class="line"></div>
		<div class="modal-body">
			<div class="input-group">
				<span>현재 비밀번호</span>
				<input type="password" placeholder="현재 비밀번호를 입력하세요." id="currentPassword" data-focus="focus">
			</div>
			<span class="input-description fc-caption">• 비밀번호는 암호화되어 저장됩니다.</span>
		</div>
		<div class="modal-action">
			<button class="btn btn-success" data-event="passwordStep1">확인</button>
			<button class="btn btn-neutral center" data-event="modal-close">닫기</button>
		</div>
	</div>
	<div class="modal-toast"></div>
	`;
	
	return html;
}

function passwordUpdateStep2Modal(currentPassword) {
	const html = `
	<input type="hidden" id="event" value="passwordStep2">
	<input type="hidden" id="currentPassword" value="${currentPassword}">
	<div class="modal-container-l modal-shadow">
		<div class="modal-header between p-l fc-text">
			<span>비밀번호 변경</span>
		</div>
		<div class="line"></div>
		<div class="modal-body">
			<div class="input-group">
				<span>새 비밀번호</span>
				<input type="password" placeholder="영문 소문자, 숫자, 특수문자를 포함한 9자 이상" id="newPassword" data-focus="focus">
			</div>
			<div class="input-group">
				<span>새 비밀번호 확인</span>
				<input type="password" placeholder="영문 소문자, 숫자, 특수문자를 포함한 9자 이상" id="newPasswordCheck">
			</div>
			<span class="input-description fc-caption">• 비밀번호는 암호화되어 저장됩니다.</span>
		</div>
		<div class="modal-action">
			<button class="btn btn-success" data-event="passwordUpdate">확인</button>
			<button class="btn btn-neutral center" data-event="modal-close">닫기</button>
		</div>
	</div>
	<div class="modal-toast"></div>
	`;
	
	return html;
}

function profileUpdateModal() {
	const html = `
	<input type="hidden" id="event" value="updateProfile">
		<div class="modal-container-l modal-shadow">
			<div class="modal-header between p-l fc-text">
				<span>프로필 설정 변경</span>
			</div>
			<div class="line"></div>
			<div class="modal-body">
				<span>프로필 설정을 변경하시겠습니까?</span>
			</div>
			<div class="modal-action">
				<button class="btn btn-primary" id="userProfileUpdate" data-event="updateProfile">변경</button>
				<button class="btn btn-primary-reverse" data-event="modal-close">취소</button>
			</div>
		</div>
		<div class="modal-toast"></div>
	`;
	
	return html;
}

function settingUpdateModal() {
	const html = `
	<input type="hidden" id="event" value="updateSetting">
		<div class="modal-container-l modal-shadow">
			<div class="modal-header between p-l fc-text">
				<span>알림 설정 변경</span>
			</div>
			<div class="line"></div>
			<div class="modal-body">
				<span>변경된 알림 설정을 적용하시겠습니까?</span>
			</div>
			<div class="modal-action">
				<button class="btn btn-primary" id="userSettingUpdate" data-event="updateSetting">변경</button>
				<button class="btn btn-primary-reverse" data-event="modal-close">취소</button>
			</div>
		</div>
		<div class="modal-toast"></div>
	`;
	
	return html;
}