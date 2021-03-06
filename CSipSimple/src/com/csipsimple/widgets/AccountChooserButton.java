/**
 * Copyright (C) 2010 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.csipsimple.widgets;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csipsimple.R;
import com.csipsimple.api.ISipService;
import com.csipsimple.api.SipProfile;
import com.csipsimple.db.DBAdapter;
import com.csipsimple.utils.AccountListUtils;
import com.csipsimple.utils.AccountListUtils.AccountStatusDisplay;
import com.csipsimple.utils.CallHandler;
import com.csipsimple.utils.CallHandler.onLoadListener;
import com.csipsimple.utils.Log;
import com.csipsimple.wizards.WizardUtils;

public class AccountChooserButton extends LinearLayout implements OnClickListener {

	protected static final String THIS_FILE = "AccountChooserButton";

	private TextView textView;
	private ImageView imageView;
	private HorizontalQuickActionWindow quickAction;
	private SipProfile account = null;

	private DBAdapter database;
	private boolean showExternals = true;
	private ISipService service;
	
	private OnAccountChangeListener onAccountChange = null;

	/**
	 * Interface definition for a callback to be invoked when 
	 * PjSipAccount is choosen
	 */
	public interface OnAccountChangeListener {
		
		/**
		 * Called when the user make an action
		 * 
		 * @param keyCode keyCode pressed
		 * @param dialTone corresponding dialtone
		 */
		void onChooseAccount(SipProfile account);
	}

	public AccountChooserButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.account_chooser_button, this, true);
		LinearLayout root = (LinearLayout) findViewById(R.id.quickaction_button);
		root.setOnClickListener(this);
		

		textView = (TextView) findViewById(R.id.quickaction_text);
		imageView = (ImageView) findViewById(R.id.quickaction_icon);
		setAccount(null);

		if (database == null) {
			database = new DBAdapter(context);
		}

	}
	
	public void updateService(ISipService aService) {
		service = aService; 
	}

	@Override
	public void onClick(View v) {
		Log.d(THIS_FILE, "Click the account chooser button");
		int[] xy = new int[2];
		v.getLocationInWindow(xy);
		Rect r = new Rect(xy[0], xy[1], xy[0] + v.getWidth(), xy[1] + v.getHeight());
		
		if(quickAction == null) {
			LinearLayout root = (LinearLayout) findViewById(R.id.quickaction_button);
			quickAction = new HorizontalQuickActionWindow(getContext(), root);
		}
		
		quickAction.setAnchor(r);
		quickAction.removeAllItems();
		
		if(service != null) {
			database.open();
			List<SipProfile> accountsList = database.getListAccounts(true);
			database.close();
	
			for (final SipProfile account : accountsList) {
				AccountStatusDisplay accountStatusDisplay = AccountListUtils.getAccountDisplay(getContext(), service, account.id);
				if(accountStatusDisplay.availableForCalls) {
					BitmapDrawable drawable = new BitmapDrawable(WizardUtils.getWizardBitmap(getContext(), account));
					quickAction.addItem(drawable, account.display_name, new OnClickListener() {
						public void onClick(View v) {
							setAccount(account);
							quickAction.dismiss();
						}
					});
				}
			}
		}
		
		if(showExternals) {
			// Add external rows
			HashMap<String, String> callHandlers = CallHandler.getAvailableCallHandlers(getContext());
			for(String packageName : callHandlers.keySet()) {
				CallHandler ch = new CallHandler(getContext());
				ch.loadFrom(packageName, null, new onLoadListener() {
					@Override
					public void onLoad(final CallHandler ch) {
						quickAction.addItem(ch.getIconDrawable(), ch.getLabel().toString(), new OnClickListener() {
							@Override
							public void onClick(View v) {
								setAccount(ch.getFakeProfile());
								quickAction.dismiss();
							}
						});
					}
				});
			}
		}
		
		quickAction.show();
	}

	public void setAccount(SipProfile aAccount) {
		account = aAccount;

		if (account == null) {
			textView.setText(getResources().getString(R.string.gsm));
			imageView.setImageResource(R.drawable.ic_wizard_gsm);
		} else {
			textView.setText(account.display_name);
			imageView.setImageDrawable(new BitmapDrawable(WizardUtils.getWizardBitmap(getContext(), account)));
		}
		if(onAccountChange != null) {
			onAccountChange.onChooseAccount(account);
		}
		
	}

	public void updateRegistration(boolean canChangeIfValid) {
		if(service != null) {
			database.open();
			List<SipProfile> accountsList = database.getListAccounts(true);
			database.close();
			if(accountsList.contains(account) && !canChangeIfValid) {
				return;
			}
			
			Log.d(THIS_FILE, "update acc sel button "+canChangeIfValid);
			if(service != null) {
				for(SipProfile account: accountsList) {
					AccountStatusDisplay accountStatusDisplay = AccountListUtils.getAccountDisplay(getContext(), service, account.id);
					if(accountStatusDisplay.availableForCalls) {
						setAccount(account);
						return;
					}
				}
			}
			//Fallback
			setAccount(null);
			
		}
		
	}

	public SipProfile getSelectedAccount() {
		if(account == null) {
			SipProfile retAcc = new SipProfile();
			HashMap<String, String> handlers = CallHandler.getAvailableCallHandlers(getContext());
			for(String callHandler : handlers.keySet()) {
				// Try to prefer the GSM handler
				if(callHandler.equalsIgnoreCase("com.csipsimple/com.csipsimple.plugins.telephony.CallHandler")) {
					Log.d(THIS_FILE, "Prefer GSM");
					retAcc.id = CallHandler.getAccountIdForCallHandler(getContext(), callHandler);
					return retAcc;
				}
			}
			// Fast way to get first if exists
			for(String callHandler : handlers.values()) {
				retAcc.id = CallHandler.getAccountIdForCallHandler(getContext(), callHandler);
				return retAcc;
			}
				
			retAcc.id = SipProfile.INVALID_ID;
			return retAcc;
		}
		return account;
	}
	
	public void setOnAccountChangeListener(OnAccountChangeListener anAccountChangeListener) {
		onAccountChange = anAccountChangeListener;
	}

	public void setShowExternals(boolean b) {
		showExternals = b;		
	}

}
