package com.utilities.interfaces;


import com.utilities.search.Query;
import com.utilities.search.Result;

public interface IResponse<Q extends Query,R extends Result<Q>> {

	void onReceived(R result);
}
