
package com.mobillium.omnicrow.webservice;


public interface ServiceCallback<T> {
    void success(T result);

    void error(ServiceException e);
}
