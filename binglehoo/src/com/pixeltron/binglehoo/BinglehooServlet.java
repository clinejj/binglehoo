package com.pixeltron.binglehoo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@SuppressWarnings("serial")
public class BinglehooServlet extends HttpServlet {
	public static final String G_URL = "https://www.google.com/uds/GwebSearch?callback=google.search.WebSearch.RawCompletion&rsz=small&hl=en&source=gsc&gss=.com&sig=dafe20cc2afc0dcfa10b802f251c72d0&&q=&gl=www.google.com&oq=bing&gs_l=partner.12...0.0.2.14400.0.0.0.0.0.0.0.0..0.0.gsnos%2Cn%3D13..0.0.0..1ac.&qid=13dfa707f45f9643&context=1&key=notsupplied&v=1.0&nocache=1365706268260";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");

		String qry = "cupcakes";
		String gqry = G_URL;
		gqry = gqry.replace("&q=", "&q=" + URLEncoder.encode(qry, "UTF-8"));
		String gresp = executePost(gqry,"");
    	if (gresp != null) {
    		String ans = gresp.substring(gresp.indexOf("{"), gresp.lastIndexOf("}"));
    		
    		resp.getWriter().println(ans);
    	}

    	/*
    	Document doc = Jsoup.parse(src);
    	Elements newsHeadlines = doc.select("#rso");
    	resp.getWriter().println(newsHeadlines.toString());
    	
    	*/
	}
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
    	String qry = req.getParameter(Query.QUERY_NAME);
    	String gqry = G_URL;
		gqry = gqry.replace("&q=", "&q=" + URLEncoder.encode(qry, "UTF-8"));
		String gresp = executePost(gqry,"");
    	if (gresp != null) {
    		gresp = gresp.substring(gresp.indexOf("{"), gresp.lastIndexOf("}"));
    		resp.getWriter().println(gresp);
    	}
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
