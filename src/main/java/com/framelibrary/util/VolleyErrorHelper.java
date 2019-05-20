package com.framelibrary.util;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.framelibrary.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.content.Context;

/**
 * @author fanming
 *
 */
public class VolleyErrorHelper {
    
	  public static String getMessage(Object error, Context context) {
	      if (error instanceof TimeoutError) {
	          return context.getResources().getString(R.string.connect_server_timeout);
	      }
	      else if (isServerProblem(error)) {
	          return handleServerError(error, context);
	      }
	      else if (isNetworkProblem(error)) {
	          return context.getResources().getString(R.string.connect_network_bad);
	      }else if(error == null){
	    	  return "未知错误！";
	      }else {
	    	  return error.toString();
		}
	  }
	  
	 
	  private static boolean isNetworkProblem(Object error) {
	      return (error instanceof NetworkError) || (error instanceof NoConnectionError);
	  }
	 
	  private static boolean isServerProblem(Object error) {
	      return (error instanceof ServerError) || (error instanceof AuthFailureError);
	  }
	 
	  private static String handleServerError(Object err, Context context) {
	      VolleyError error = (VolleyError) err;
	  
	      NetworkResponse response = error.networkResponse;
	  
	      if (response != null) {
	          switch (response.statusCode) {
	            case 404:
	            case 422:
	            case 401:
	                try {
	                    // server might return error like this { "error": "Some error occured" }
	                    // Use "Gson" to parse the result
	                    HashMap<String,String> result = new Gson().fromJson(new String(response.data),
	                            new TypeToken<Map<String,String>>() {
	                            }.getType());

	                    if (result != null && result.containsKey("error")) {
	                        return result.get("error");
	                    }

	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                // invalid request
	                return StringUtils.isBlank(error.getMessage())?String.valueOf(response.statusCode):error.getMessage();

	            default:
	                return context.getResources().getString(R.string.connect_server_fail);
	            }
	      }
	        return context.getResources().getString(R.string.connect_server_fail);
	  }
	}
