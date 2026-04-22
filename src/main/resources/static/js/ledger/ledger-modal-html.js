function categoryChoiceModal() {
	const html = `
	<input type="hidden" id="event" value="categoryChoice">
		<div class="modal-container-l modal-shadow">
			<div class="modal-header p-l fc-text">
				<div class="container-header-icon">
					<svg viewBox="0 0 24 24" width="24" height="24" fill="currentColor">
						<path d="M18 15L17.999 18H21V20H17.999L18 23H16L15.999 20H13V18H15.999L16 15H18ZM11 18V20H3V18H11ZM21 11V13H3V11H21ZM21 4V6H3V4H21Z"></path>
					</svg>
				</div>
				<span>기본 카테고리 설정</span>
			</div>
			<div class="line"></div>
			<div class="flex-col gap-m p-s">
				<div class="flex-col gap-s p-m">
					<div class="flex items-center gap-m fc-text">
						<div class="ball asset"></div>
						자산 항목
					</div>
					<div class="grid-cols-auto-100 gap-m">
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="ass_01">
							<span class="checkmark"></span>
							<span class="checkbox-description">현금</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="ass_02">
							<span class="checkmark"></span>
							<span class="checkbox-description">체크카드</span>
						</label>
					</div>
				</div>
				<div class="line"></div>
				<div class="flex-col gap-s p-m">
					<div class="flex items-center gap-m fc-text">
						<div class="ball liabilities"></div>
						부채 항목
					</div>
					<div class="grid-cols-auto-100 gap-m">
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="lia_01">
							<span class="checkmark"></span>
							<span class="checkbox-description">신용카드</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="lia_02">
							<span class="checkmark"></span>
							<span class="checkbox-description">대출</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="lia_03">
							<span class="checkmark"></span>
							<span class="checkbox-description">갚을 돈</span>
						</label>
					</div>
				</div>
				<div class="line"></div>
				<div class="flex-col gap-s p-m">
					<div class="flex items-center gap-m fc-text">
						<div class="ball income"></div>
						수입 항목
					</div>
					<div class="grid-cols-auto-100 gap-m">
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="inc_01">
							<span class="checkmark"></span>
							<span class="checkbox-description">월급</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="inc_02">
							<span class="checkmark"></span>
							<span class="checkbox-description">상여금</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="inc_03">
							<span class="checkmark"></span>
							<span class="checkbox-description">사업 소득</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="inc_04">
							<span class="checkmark"></span>
							<span class="checkbox-description">판매 수익</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="inc_05">
							<span class="checkmark"></span>
							<span class="checkbox-description">기타 수익</span>
						</label>
					</div>
				</div>
				<div class="line"></div>
				<div class="flex-col gap-s p-m">
					<div class="flex items-center gap-m fc-text">
						<div class="ball expense"></div>
						지출 항목
					</div>
					<div class="grid-cols-auto-100 gap-m">
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="exp_01">
							<span class="checkmark"></span>
							<span class="checkbox-description">식비</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="exp_02">
							<span class="checkmark"></span>
							<span class="checkbox-description">교통비</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="exp_03">
							<span class="checkmark"></span>
							<span class="checkbox-description">월세</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="exp_04">
							<span class="checkmark"></span>
							<span class="checkbox-description">생활용품</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="exp_05">
							<span class="checkmark"></span>
							<span class="checkbox-description">취미</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="exp_06">
							<span class="checkmark"></span>
							<span class="checkbox-description">학업</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="exp_07">
							<span class="checkmark"></span>
							<span class="checkbox-description">의류 및 미용</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="exp_08">
							<span class="checkmark"></span>
							<span class="checkbox-description">의료 및 건강</span>
						</label>
						<label class="checkbox gap-m">
							<input type="checkbox" data-value="exp_09">
							<span class="checkmark"></span>
							<span class="checkbox-description">이자</span>
						</label>
					</div>
				</div>
				<div class="line"></div>
				<div class="modal-action">
					<button class="btn btn-info" data-event="categoryChoice">설정하기</button>
					<button class="btn" data-event="modal-close">닫기</button>
				</div>
			</div>
		</div>
	`;
	
	return html;
}