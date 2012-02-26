/**
 * Copyright
 */
package getrest.android.config;

import getrest.android.entity.Marshaller;
import getrest.android.entity.Packer;
import getrest.android.executor.RequestHandlerFactory;
import getrest.android.request.RequestController;
import getrest.android.service.Representation;
import getrest.android.service.ServiceRequestExecutor;

public interface ResourceContextContribution {

    void setPacker(Packer packer);

    <T> void setMarshaller(Marshaller<T, Representation> marshaller);

    void setServiceRequestExecutor(ServiceRequestExecutor serviceRequestExecutor);

    void setRequestController(RequestController requestController);

    void setRequestHandlerFactory(RequestHandlerFactory requestHandler);

}
