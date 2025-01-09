function escapeHash(h) {
	if (h != null)
		return encodeURI(h);
	return null;
}

function sanitizeHtml(s) {
	if (s != null)
		return s.replace(/&/g,'&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot').replace(/'/g, '&#39;')
		.replace(/\//g,'&#47;').replace('/\r/g', '').replace(/\n/g, '');
	return null;
}