package com.utilities.interfaces;

import com.utilities.search.Query;
import com.utilities.search.Result;

public interface IParser<Q extends Query, R extends Result<Q>> {

	public R parse(Q... results);
}
