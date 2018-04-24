-- Create table for HANDEDITS
CREATE
	TABLE IF NOT EXISTS handedits
	(
		creation INTEGER PRIMARY KEY,
		last_modification INTEGER,
		zan_number INTEGER,
		author TEXT,
		json_path TEXT,
		cover_path TEXT,
		title TEXT,
		content TEXT,
		archived INTEGER,
		trashed INTEGER,
		category_id INTEGER DEFAULT null
	);
	
--DROP table handedits;

