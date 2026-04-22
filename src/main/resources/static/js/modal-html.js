function loginModal() {
  const html = `
	<input type="hidden" id="event" value="login">
  <div class="modal-header jus-center">
    <img src="/images/sublogo.png" style="width: 10rem;">
  </div>
  <div class="modal-body">
    <div class="modal-input-form">
      <div class="input-floating">
        <div class="input-group">
          <input type="text" id="loginEmail" class="input-field" placeholder="아이디를 입력해주세요." data-focus="focus">
          <p class="input-label">아이디</p>
        </div>
      </div>
      <div class="input-floating">
        <div class="input-group">
          <input type="password" id="loginPassword" class="input-field" placeholder="비밀번호를 입력해주세요.">
          <p class="input-label">비밀번호</p>
        </div>
      </div>
      <div class="modal__login--action">
        <label class="checkbox">
          <input type="checkbox">
          <span class="checkmark"></span>
          <span class="checkbox-description">자동 로그인</span>
        </label>
        <a class="font-link" href="#">아이디 / 비밀번호 찾기</a>
      </div>
      <div class="modal-button-form-2">
        <button class="btn btn-primary center" data-event="login">로그인</button>
        <button class="btn btn-neutral center" data-event="modal-close">닫기</button>
      </div>
    </div>
  </div>
	<div class="modal-toast"></div>
  `;

  return html;
}

function joinModal() {
  const html = `
	<input type="hidden" id="event" value="join">
  <div class="modal-header jus-center">
    <img src="/images/sublogo.png" style="width: 10rem;">
  </div>
  <div class="modal-body">
    <div class="modal-input-form">
      <div class="input-floating">
        <div class="input-group">
          <input type="text" class="input-field" id="joinEmail" placeholder="사용하실 아이디를 입력해주세요." data-focus="focus">
          <p class="input-label">아이디</p>
        </div>
      </div>
      <div class="input-floating">
        <div class="input-group">
          <input type="password" class="input-field" id="joinPassword" placeholder="영문 소문자, 숫자, 특수문자를 포함한 9자 이상이어야 합니다.">
          <p class="input-label">비밀번호</p>
        </div>
      </div>
      <div class="input-floating">
        <div class="input-group">
          <input type="text" class="input-field" id="joinNickname" placeholder="2~10자 사이의 한글, 영문, 숫자여야 합니다.">
          <p class="input-label">닉네임</p>
        </div>
      </div>
			<label class="checkbox">
        <input type="checkbox" id="notification">
        <span class="checkmark"></span>
        <span class="checkbox-description">알림 수신 동의</span>
      </label>
			<label class="checkbox">
        <input type="checkbox" id="emailNotification">
        <span class="checkmark"></span>
        <span class="checkbox-description">이메일 수신 동의</span>
      </label>
      <div class="modal-button-form-2">
        <button class="btn btn-primary center" data-event="join">회원가입</button>
        <button class="btn btn-neutral center" data-event="modal-close">닫기</button>
      </div>
    </div>
  </div>
	<div class="modal-toast"></div>
  `;

  return html;
}