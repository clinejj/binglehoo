package com.pixeltron.binglehoo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

import javax.servlet.http.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class BinglehooServlet extends HttpServlet {
	private static final String G_URL = "https://www.google.com/uds/GwebSearch?callback=google.search.WebSearch.RawCompletion&rsz=small&hl=en&source=gsc&gss=.com&sig=dafe20cc2afc0dcfa10b802f251c72d0&&q=&gl=www.google.com&oq=bing&gs_l=partner.12...0.0.2.14400.0.0.0.0.0.0.0.0..0.0.gsnos%2Cn%3D13..0.0.0..1ac.&qid=13dfa707f45f9643&context=1&key=notsupplied&v=1.0&nocache=1365706268260";
	private static final String Y_URL = "http://search.yahoo.com/search;_ylt=Ag2rqzK7vv6CsmRCVSecZ16bvZx4?p=&toggle=1&cop=mss&ei=UTF-8&fr=yfp-t-900";
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
    	String qry = URLEncoder.encode(req.getParameter(Query.QUERY_NAME), "UTF-8");
    	String gqry = G_URL;
		gqry = gqry.replace("&q=", "&q=" + qry);
		String yqry = Y_URL;
		yqry = yqry.replace("?p=", "?p=" + qry);
		
		Vector<Query> results = new Vector<Query>();
		Vector<Query> gResults = new Vector<Query>();
		Vector<Query> yResults = new Vector<Query>();
		String strRes = "";
		
		String gresp = executePost(gqry,"");
    	if (gresp != null) {
    		gresp = gresp.substring(gresp.indexOf("{"), gresp.lastIndexOf("}")) + "}";
    		JSONObject gresults;
			try {
				gresults = new JSONObject(gresp);
				JSONArray arr = (JSONArray) gresults.get("results");
				for (int i=0;i<arr.length();i++) {
					JSONObject cur = arr.getJSONObject(i);
					Query newCur = new Query();
					newCur.content = (String) cur.get("content");
					newCur.title = (String) cur.get("title");
					newCur.title = "G - " + newCur.title;
					newCur.visibleUrl = (String) cur.get("visibleUrl");
					newCur.url = (String) cur.get("url");
					gResults.add(newCur);
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		//resp.getWriter().println(strRes);
    	}
    	
    	Document doc = Jsoup.connect(yqry).get();
    	Element content = doc.getElementById("web");
    	Elements links = content.getElementsByTag("li");
    	for (int i=0;i<links.size();i++) {
    		Element cur = links.get(i);
    		Query newCur = new Query();
			newCur.content = cur.getElementsByClass("abstr").toString();
			newCur.title = cur.getElementsByTag("a").get(0).text();
			newCur.title = "Y - " + newCur.title;
			newCur.visibleUrl = cur.getElementsByClass("url").toString();
			newCur.url = cur.getElementsByTag("a").get(0).attr("href");
			yResults.add(newCur);
    	}
    	
    	if (gResults.size() <= yResults.size()) {
    		int count = 0;
    		for (int i=0;i<gResults.size();i++)
    		{
    			results.add(gResults.get(i));
    			results.add(yResults.get(i));
    			count = i;
    		}
    		for (int i=count;i<yResults.size();i++) {
    			results.add(yResults.get(i));
    		}
    	} else {
    		int count = 0;
    		for (int i=0;i<yResults.size();i++)
    		{
    			results.add(gResults.get(i));
    			results.add(yResults.get(i));
    			count = i;
    		}
    		for (int i=count;i<gResults.size();i++) {
    			results.add(gResults.get(i));
    		}
    	}
    	
    	for (int i=0;i<results.size();i++) {
    		strRes = strRes + results.get(i).toHTML();
    	}
    	resp.getWriter().println(strRes);

    }
    
    public static String executePost(String targetURL, String urlParameters)
    {
      URL url;
      HttpURLConnection connection = null;  
      try {
        //Create connection
        url = new URL(targetURL);
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Language", "en-US");  
  			
        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        /*
        //Send request
        DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
        wr.writeBytes (urlParameters);
        wr.flush ();
        wr.close ();
        */

        //Get Response	
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer(); 
        while((line = rd.readLine()) != null) {
          response.append(line);
          response.append('\r');
        }
        rd.close();
        return response.toString();

      } catch (Exception e) {

        e.printStackTrace();
        return null;

      } finally {

        if(connection != null) {
          connection.disconnect(); 
        }
      }
    }
}
