async function ledgerMetadata() {
	const response = await fetch('/api/ledger/category-list');
	
	if(!response.ok) {
		const errorResponse = await response.json();
		console.error(errorResponse.message);
	}
	
	const result = await response.json();
	
	return result;
}

export const categoryMetadata = await ledgerMetadata();