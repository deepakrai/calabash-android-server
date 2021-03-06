package sh.calaba.instrumentationbackend.actions.webview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import sh.calaba.instrumentationbackend.Result;
import sh.calaba.instrumentationbackend.actions.Action;
import sh.calaba.instrumentationbackend.actions.webview.CalabashChromeClient.WebFuture;
import sh.calaba.instrumentationbackend.query.ast.UIQueryUtils;

import android.os.Build;
import android.webkit.WebView;

public class ExecuteJavascript implements Action {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Result execute(String... args) {
		final String scriptCode = args[0]; 
		List<WebFuture> webResults = (List<WebFuture>) UIQueryUtils.evaluateSyncInMainThread(new Callable() {			
			
			public Object call() throws Exception {
				
				List<WebFuture> webResults = new ArrayList();
				List<CalabashChromeClient> list = CalabashChromeClient.findAndPrepareWebViews();
				if (list.isEmpty()) {
					return webResults;
				}
				
				CalabashChromeClient ccc = list.get(0);
				WebView webView = ccc.getWebView();
				final String script = "javascript:(function() {"
                        + " var r;"
                        + " try {"
                        + "  r = (function() {"
                        + scriptCode + ";"
                        + "  }());"
                        + " } catch (e) {"
                        + "  r = 'Exception: ' + e;"
                        + " }"
                        + " prompt('calabash:'+r);"
                        + "}())";

				System.out.println("execute javascript1: " + script);

		        webView.loadUrl(script);
		        webResults.add(ccc.getResult());	

				return webResults;
				
			}
		});
    	
    	List<String> allResults = new ArrayList<String>(webResults.size());
    	boolean success = true;
    	for (WebFuture f : webResults) {
    		String result = f.getAsString();
			allResults.add(result);    		
			if (result.startsWith("Exception:")) {
				success = false;
			}			
    	}
    		
    	if (allResults.size() == 0) {
    		return new Result(false, "No WebView found");	
    	}
    	else {
			return new Result(success, allResults);
		}
	}

    public static String evaluateJavascript(final WebView webView, final String javascript) {
        Map result = (HashMap)UIQueryUtils.evaluateSyncInMainThread(new Callable<WebFuture>() {

            public WebFuture call() throws Exception {
                CalabashChromeClient ccc = CalabashChromeClient.prepareWebView(webView);

                final String script = "javascript:(function() {"
                        + " var r;"
                        + " try {"
                        + "  r = (function() {"
                        + javascript + ";"
                        + "  }());"
                        + " } catch (e) {"
                        + "  r = 'Exception: ' + e;"
                        + " }"
                        + " prompt('calabash:'+r);"
                        + "}())";

                System.out.println("execute javascript2: " + script);

                if (Build.VERSION.SDK_INT < 19) { // Android 4.4
                    JavaScriptExecuter javaScriptExecuter = new JavaScriptExecuter(webView);
                    javaScriptExecuter.executeJavaScript(script);
                } else {
                    webView.evaluateJavascript(script, null);
                }

                Object o = ccc.getResult();
                System.out.println("DRAI DRAI 2 Executed javascript: " + ((WebFuture)o).getAsString());


                return (WebFuture)o;
            }
        });

        return (String) result.get("result");
    }

	@Override
	public String key() {
		return "execute_javascript";
	}

}
