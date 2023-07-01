package es.udc.ws.app.thriftservice;

import es.udc.ws.app.thrift.ThriftAppService;
import es.udc.ws.util.servlet.ThriftHttpServletTemplate;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

public class ThriftAppServiceServlet extends ThriftHttpServletTemplate {
    public ThriftAppServiceServlet() {
        super(createProcessor(), createProtocolFactory());
    }

    private static TProcessor createProcessor() {
        return new ThriftAppService.Processor<ThriftAppService.Iface>(
                new ThriftAppServiceImpl());

    }
    private static TProtocolFactory createProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }
}
