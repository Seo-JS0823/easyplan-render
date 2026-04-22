async function ledgerMetadata() {
	const currentMonth = new Date().getMonth() + 1;
	
	const month = getMonthRange(currentMonth);
	
	const ledgerMetaRequest = await fetch(`/api/ledger?startDate=${month.startDate}&endDate=${month.endDate}`);
	
	const ledgerMetaResponse = await ledgerMetaRequest.json();
	
	return ledgerMetaResponse;
}

export const ledgerData = await ledgerMetadata();