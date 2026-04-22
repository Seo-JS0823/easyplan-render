const currentPath = window.location.pathname;

const sidebarClose = document.querySelector('#sidebarClose');
const sidebarOpen = document.querySelector('#sidebarOpen');
const sidebar = document.querySelector('.app-left');
const topbar = document.querySelector('.app-topbar');

if(sidebarClose) {
  sidebarClose.addEventListener('click', (e) => {
    sidebar.classList.add('close');
    topbar.classList.add('full');
    sidebarClose.classList.toggle('disabled');
    sidebarOpen.classList.toggle('disabled');
  });
}

if(sidebarOpen) {
  sidebarOpen.addEventListener('click', (e) => {
    sidebar.classList.remove('close');
    topbar.classList.remove('full');
    sidebarOpen.classList.toggle('disabled');
    sidebarClose.classList.toggle('disabled');
  });
}

const sidebarParent = document.querySelector('.sidebar-items');
const sidebarItems = document.querySelectorAll('.sidebar-item');
const sidebarAccordions = document.querySelectorAll('.sidebar-item-accordion');

if(sidebarItems) {
	sidebarItems.forEach(item => {
		const key = item.dataset.sidebar;
		const accordions = sidebarParent.querySelector(`.sidebar-item-accordion[data-sidebar-family=${key}]`);
		
		if(accordions) {
			const accordionItem = accordions.querySelectorAll('.sidebar-accordion-item');
			
			accordionItem.forEach(acc => {
				const aTag = acc.querySelector('a');
				if(aTag) {
					if(aTag.getAttribute('href') === currentPath) {
						acc.classList.add('active');
						item.classList.add('active');
						accordions.classList.add('active');
					}
				}
			}) 
		}
		
		item.addEventListener('click', () => {
			sidebarItems.forEach(si => si.classList.remove('active'));
			
			sidebarAccordions.forEach(ac => ac.classList.remove('active'));
			
			item.classList.add('active');
			
			if(accordions) {
				accordions.classList.add('active');
			}
		});
	})
}

/*
const dropdowns = document.querySelectorAll('.dropdown-trigger');

if(dropdowns.length > 0) {
  dropdowns.forEach(drop => {
		console.log(drop)
    const dropdown = drop.querySelector('.dropdown');
    const dropdownText = drop.querySelector('.dropdown-text');
    
    if(dropdown) {
      const items = dropdown.querySelectorAll('span');
      items.forEach(item => {
        item.addEventListener('click', (e) => {
          e.stopPropagation();

          const isSelectedValue = e.target.getAttribute('data-value');
          drop.setAttribute('data-selected', isSelectedValue);
          dropdownText.textContent = e.target.textContent;
          drop.classList.remove('active');
        });
      });
    };

    drop.addEventListener('click', (e) => {
      e.target.classList.toggle('active');
    });
  });
}
*/
const menuDropdown = document.querySelector('[data-icon=menu]');

if(menuDropdown) {
	menuDropdown.addEventListener('click', (e) => {
		e.target.classList.toggle('dropdown-active');
	})
}