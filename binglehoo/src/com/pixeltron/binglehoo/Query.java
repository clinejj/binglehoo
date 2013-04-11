package com.pixeltron.binglehoo;

public class Query {
	public static final String QUERY_NAME = "query";
	
	public String url;
	public String visibleUrl;
	public String title;
	public String content;
	
	public String toHTML() {
		String base = "<div class=\"res\"><div><h3><a href=\"LURL\" target=\"_blank\">TITLE</a></h3></div><div class=\"abstr\">CONTENT</div><span>VISURL</span></div>";
		base = base.replace("LURL", url);
		base = base.replace("TITLE", title);
		base = base.replace("CONTENT", content);
		base = base.replace("VISURL", visibleUrl);
		return base;
	}
}
