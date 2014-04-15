package com.builtio.builtfacebooklogin;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltUser;
import com.raweng.built.androidquery.AQuery;

/**
 * This is built.io android tutorial.
 * 
 * Short introduction of some classes with some methods.
 * Contain classes: 
 * 1. BuiltUser
 * 
 * For quick start with built.io refer "http://docs.built.io/quickstart/index.html#android"
 * 
 * @author raw engineering, Inc
 *
 */
public class UserDetails extends Activity {

	private TextView screenNameTextView;
	private ImageView profilePicImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_layout);

		screenNameTextView   = (TextView) findViewById(R.id.userName);
		profilePicImageView  = (ImageView) findViewById(R.id.profileImage);

		/*
		 * Extracting the data from saved user.  
		 */
		JSONObject applicationUserJSON  = (JSONObject) BuiltUser.getSession().get("application_user");
		JSONObject authJSON     = applicationUserJSON.optJSONObject("auth_data");
		JSONObject twitterJSON  = authJSON.optJSONObject("facebook");
		JSONObject userInfoJSON = twitterJSON.optJSONObject("user_profile");

		String profileUrl       ="http://graph.facebook.com/"+ userInfoJSON.optString("id")+"/picture?type=large";;
		String screen_name  	= userInfoJSON.optString("name");

		AQuery aQuery = new AQuery(UserDetails.this);
		aQuery.id(profilePicImageView).image(profileUrl, true, true);

		screenNameTextView.setText(screen_name);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action, menu);

		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logout:

			/*
			 * logging out the user.
			 */
			BuiltUser.getSession().logout(new BuiltResultCallBack() {

				@Override
				public void onSuccess() {
					/// after successfully success blog called.
					Toast.makeText(UserDetails.this, "Logout successfully...", Toast.LENGTH_SHORT).show();
					finish();
				}

				@Override
				public void onError(BuiltError error) {
					
					///If any error occurred while logout this error object provides more details
					Toast.makeText(UserDetails.this, "error :"+error.getErrorMessage(), Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onAlways() {
					// write code here that you want to execute
				    // regardless of success or failure of the operation
				}
			});


			return true;
		}
		return false;

	}



}
