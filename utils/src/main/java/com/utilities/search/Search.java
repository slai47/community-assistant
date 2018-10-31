package com.utilities.search;

import android.os.AsyncTask;

import com.utilities.interfaces.IParser;
import com.utilities.interfaces.IResponse;

import org.greenrobot.eventbus.EventBus;


public class Search<Q extends Query, R extends Result<Q>> extends AsyncTask<Q, Void, R>{

	private IParser<Q,R> mParser;
	private IResponse<Q,R> mCallback;
    private EventBus mBus;

	public Search(IParser<Q,R> parser, IResponse<Q,R> callback, EventBus bus) {
		mParser = parser;
        mCallback = callback;
        mBus = bus;
	}
	
	@Override
	protected R doInBackground(Q... params) {
		return mParser.parse(params);
	}

    @Override
    protected void onPostExecute(R r) {
        if(mCallback != null){
            mCallback.onReceived(r);
        }
        if(mBus != null)
            mBus.post(r);
        super.onPostExecute(r);
    }
}
