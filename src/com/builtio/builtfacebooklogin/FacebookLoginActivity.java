package com.builtio.builtfacebooklogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltUser;

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
public class FacebookLoginActivity extends Activity{

	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private Button facebookLoginButton;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		
		facebookLoginButton = (Button) findViewById(R.id.facebookLogin);
		
		/*
		 *  Checking user session is present on disc 
		 */
		if(BuiltUser.getSession() != null){
			Intent detailIntent = new Intent(FacebookLoginActivity.this, UserDetails.class);
			startActivity(detailIntent);
			finish();
		}
		
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
			}
		}
		updateView();
	}

	/**
	 * Login on built.io using facebook access token.
	 * 
	 * 
	 * @param facebookAccessToken
	 */
	public void builtFacebookLogin(String facebookAccessToken){
		
		/*
		 * BuiltUser class provides various logins.
		 * Here's user can login on built with facebook account. 
		 */
		final BuiltUser user = new BuiltUser();
		user.loginWithFacebookAuthAccessToken(facebookAccessToken, new BuiltResultCallBack() {
			
			@Override
			public void onSuccess() {
				
				/// After login successful BuiltUser object provided in success callback.
				
				/*
				 * Saving user data or session on disc.
				 */
				try {
					user.saveSession();
				} catch (Exception e) {
					e.printStackTrace();
				}

				Intent detailsIntent = new Intent(FacebookLoginActivity.this, UserDetails.class);
				startActivity(detailsIntent);
				finish();
			}
			
			@Override
			public void onError(BuiltError error) {
				
				/// there is an error while login.
				/// builtErrorObject contains more details of error.
				Toast.makeText(FacebookLoginActivity.this, "error :"+error.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onAlways() {
				
				/// write code here that user want to execute.
				/// regardless of success or failure of the operation.
				onClickLogout();
			}
		});
		
	}
	
	private void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}
	}

	private void onClickLogout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	private void updateView() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
			
			builtFacebookLogin(session.getAccessToken());
			
			facebookLoginButton.setOnClickListener(new OnClickListener() {
				public void onClick(View view) { onClickLogout(); }
			});
		} else {
			facebookLoginButton.setOnClickListener(new OnClickListener() {
				public void onClick(View view) { onClickLogin(); }
			});
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			updateView();
		}
	}
}
