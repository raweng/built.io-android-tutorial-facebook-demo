package com.builtio.builtfacebooklogin;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raweng.built.Built;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltUser;
import com.raweng.built.androidquery.AQuery;
import com.raweng.built.utilities.BuiltConstant;

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

    private BuiltApplication builtApplication;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_layout);

		screenNameTextView   = (TextView) findViewById(R.id.userName);
		profilePicImageView  = (ImageView) findViewById(R.id.profileImage);

        try {
            builtApplication = Built.application(this, "YOUR_APP_API_KEY");
        } catch (Exception e) {
            e.printStackTrace();
        }
		/*
		 * Extracting the data from saved user.  
		 */

		JSONObject applicationUserJSON  = (JSONObject) builtApplication.getCurrentUser().get("application_user");
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
			builtApplication.getCurrentUser().logoutInBackground(new BuiltResultCallBack() {

                @Override
                public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                    if (builtError == null){
                        Toast.makeText(UserDetails.this, "Logout successfully...", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(UserDetails.this, "error :" + builtError.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


			return true;
		}
		return false;

	}



}
