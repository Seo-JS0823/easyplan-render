document.querySelector('#modal-login').addEventListener('click', (e) => {
  modalOpen(loginModal(), ['modal__login']);
});

document.querySelector('#modal-join').addEventListener('click', (e) => {
  modalOpen(joinModal(), ['modal__login']);
});

document.querySelector('#topbar-menu').addEventListener('click', (e) => {
	e.target.classList.toggle('dropdown-active');
});