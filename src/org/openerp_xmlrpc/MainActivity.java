// MainActivity.java
// OpenErp <-> Android Xml-Rpc Tester
// this class depends to OpenerpRpc.java
//
// Copyright 2013 Alessio Gerace <alessio.gerace@gmail.com> aka Archetipo
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


import java.util.Map;

import org.xmlrpc.android.XMLRPCException;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener{
	EditText testResult;
	OpenerpRpc connector;
	// Make sure you have download a Xml-Rpc.android from 
	//https://code.google.com/p/android-xmlrpc/
	// and add org.xmlrpc.android (files) in src folder 
	// Setup your data first and startup Test 
	String DBNAME="";
	String USER="";
	String PASS="";
	Button btnTest;
	@Override
	protected void onCreate(Bundle savedInstanceState)   {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		testResult = (EditText) findViewById(R.id.testResult);
		connector=new OpenerpRpc(getBaseContext());
		// set here the the data to connection e database name and object for the callback
		// 10.0.2.2 is localhost in AVD
		connector.Config("http://10.0.2.2:8069", DBNAME,this);
        btnTest = (Button) findViewById(R.id.btnTest); 
        btnTest.setOnClickListener(this);

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	// this is a callback func from OEconnector
	@SuppressWarnings("unchecked")
	public void oerpcRec(String rtype,Object res) throws XMLRPCException{
		if (rtype=="login"){ 
			
			connector.setModel("res.users");
			Object[] Ids = {Integer.parseInt(connector.userid)};
			// set here the fields you wont loads
			Object[] values={"name"};
			connector.Read(Ids,values);
		}
		if(rtype=="read"){
			Object[] ret=(Object[])res;
			Map<String, Object> map = (Map<String, Object>) ret[0];
			testResult.setText("Hello!! "+map.get("name"));
		}
		
		
	}
	
	public void onClick(View v) {
		try {
			//here set user and pass for login
			connector.Login(USER,PASS);
			testResult.setText((String)connector.allres);
			
		} catch (XMLRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
