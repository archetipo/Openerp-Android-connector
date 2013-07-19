// OpenerpRPC.java
// OpenErp <-> Android Xml-Rpc
// this class depends to xmlrpc.android project
// download it at https://code.google.com/p/android-xmlrpc/
//
// 
//
// Copyright 2013 Alessio Gerace <alessio.gerace@gmail.com>
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
// MA 02110-1301, USA.
//
//


package org.openerp_xmlrpc;
import java.net.URI;

import org.apache.http.conn.HttpHostConnectException;
import org.xmlrpc.android.Tag;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import org.xmlrpc.android.XMLRPCFault;
import org.xmlrpc.android.XMLRPCSerializable;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class OpenerpRpc {
	private Context context;
	
	private String server="";
	private String port="";
	private Boolean configured=false;
	
	private String model="";
	private String dbname="";
	private String dbuser="";
	private String dbpass="";
	public String userid="";
	
	public String rtype="login";
	private XMLRPCClient client;
	private URI uri;
	public Object allres=null;
	public Boolean exec=false;
	// change the name (MainActivity) of class that you want use as parent class to callback 
	private MainActivity parent;
	
	private static final String TAG = "OERPC";
	public OpenerpRpc(Context context) {
		this.context=context;
		
	}
	// change the neme (MainActivity) of class that you want use as parent class to callback 
	public void Config(String serverName , String DbName,MainActivity parent){
		server=serverName;
		dbname=DbName;
		this.parent=parent;
		configured=true;
	}
	
	public void Login(String user,String pass) throws XMLRPCException{
		if (configured){
			dbuser=user;
			dbpass=pass;
			rtype="login";
			uri = URI.create(server+"/xmlrpc/common");
			client = new XMLRPCClient(uri);
	        XMLRPCMethod method = new XMLRPCMethod("login",this);     
	        Object[] params = {dbname,dbuser,dbpass};
	        method.call(params);
		}
	}
	
	public void Search(Object[] Params) throws XMLRPCException{
		if (configured){
			rtype="search";
			uri = URI.create(server+"/xmlrpc/object");
			client = new XMLRPCClient(uri);
			XMLRPCMethod method = new XMLRPCMethod("execute",this);   
	        Object[] darray = {Params};
	        Object[] params = {dbname,Integer.parseInt(userid),dbpass,model,"search",darray};		        
	        method.call(params);
		}
	}	
	
	public void Read(Object[] Ids,Object[] Params) throws XMLRPCException{
		if (configured){
			rtype="read";
			uri = URI.create(server+"/xmlrpc/object");
			client = new XMLRPCClient(uri);
			XMLRPCMethod method = new XMLRPCMethod("execute",this); 
	        Object[] params = {dbname,Integer.parseInt(userid),dbpass,model,"read",Ids,Params};		        
	        method.call(params);
		}
	}	
	
	public void Write(Object[] Ids,Object[] Params) throws XMLRPCException{
		if (configured){
			rtype="write";
			uri = URI.create(server+"/xmlrpc/object");
			client = new XMLRPCClient(uri);
			XMLRPCMethod method = new XMLRPCMethod("execute",this);
	        Object[] params = {dbname,Integer.parseInt(userid),dbpass,model,"write",Ids,Params};		        
	        method.call(params);
		}
	}		

	public void Create(Object[] Params) throws XMLRPCException{
		if (configured){
			rtype="create";
			uri = URI.create(server+"/xmlrpc/object");
			client = new XMLRPCClient(uri);
			XMLRPCMethod method = new XMLRPCMethod("execute",this);
	        Object[] params = {dbname,Integer.parseInt(userid),dbpass,model,"write",Params};
	        method.call(params);
	        
		}
	}		
	
	public void resultcall(Object result) throws XMLRPCException{
		
		allres=result;
		if (rtype.equals("login")){
			//Isn't impossible cast the result var with (String) because cause crash..why?
			userid=""+result;
		}
		// name of callback function to use in parent class (MainActivity) for receive data
		this.parent.oerpcRec(rtype,allres);
			
	}
	
	
	public void setModel(String modelName){
		model=modelName;
	}

	
	class XMLRPCMethod extends Thread {
		private String method;
		private Object[] params;
		private Handler handler;
		public Object result;
		private OpenerpRpc callBack;
		public XMLRPCMethod(String method, OpenerpRpc callBack) {
			this.method = method;
			this.callBack = callBack;

			handler = new Handler();
		}
		public void call() {
			call(null);
		}
		public void call(Object[] params) {;
			this.params = params;
			start();
		}
		@Override
		public void run() {
    		try {
    			result = client.callEx(method, params);
    			handler.post(new Runnable() {
					public void run() {
						try {
							callBack.resultcall(result);
						} catch (XMLRPCException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
    			});
    		} catch (final XMLRPCFault e) {
    			handler.post(new Runnable() {
					public void run() {
						Log.d("Test", "error", e);
					}
    			});
    		} catch (final XMLRPCException e) {
    			handler.post(new Runnable() {
					public void run() {
						Throwable couse = e.getCause();
						if (couse instanceof HttpHostConnectException) {
							Log.d(TAG, "error"+uri.getHost());
						} else {
							Log.d("Test", "error", e);
						}
						Log.d("Test", "error", e);
					}
    			});
    		}
		}
	}	

}
