package padada.com.dal;

/**
 * Created by Alex on 04/12/2016.
 */

public class ApiResult<T> {

	T result;

	public ApiResult(T result) {
		this.result = result;
	}

	public T getResult() {
		return result;
	}

}
