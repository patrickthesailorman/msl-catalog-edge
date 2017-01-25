package org.jboss.resteasy.plugins.server.netty;

import com.google.inject.Inject;
import com.kenzan.msl.common.utils.Utils;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import io.swagger.api.impl.CatalogEdgeSessionToken;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.Failure;

import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * TODO : hack to add CORS into header
 * <p>
 * {@link org.jboss.netty.channel.SimpleChannelUpstreamHandler} which handles the requests and dispatch them.
 * <p>
 * This class is {@link org.jboss.netty.channel.ChannelHandler.Sharable}.
 *GIT
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @author Norman Maurer
 * @version $Rev: 2368 $, $Date: 2010-10-18 17:19:03 +0900 (Mon, 18 Oct 2010) $
 */
@ChannelHandler.Sharable
public class RequestHandler extends SimpleChannelUpstreamHandler {


    private DynamicStringProperty CLIENT_PORT =
            DynamicPropertyFactory.getInstance().getStringProperty("clientPort", "3000");

    protected final RequestDispatcher dispatcher;
    private final static Logger logger = Logger.getLogger(RequestHandler.class);

    @Inject
    private static CatalogEdgeSessionToken catalogEdgeSessionToken;

    public RequestHandler(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof NettyHttpRequest) {
            NettyHttpRequest request = (NettyHttpRequest) e.getMessage();

            request.getResponse().getOutputHeaders().add("Access-Control-Allow-Origin", Utils.getHost(request.getUri().getBaseUri().toString(), CLIENT_PORT.get()));
            request.getResponse().getOutputHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            request.getResponse().getOutputHeaders().add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Content-Length");
            request.getResponse().getOutputHeaders().add("Access-Control-Allow-Credentials", "true");

            catalogEdgeSessionToken.setDomain(request.getUri().getRequestUri().getHost());
            catalogEdgeSessionToken.updateSessionToken(request);

            if (request.is100ContinueExpected()) {
                send100Continue(e);
            }

            NettyHttpResponse response = request.getResponse();
            try {
                dispatcher.service(request, response, true);
            } catch (Failure e1) {
                response.reset();
                response.setStatus(e1.getErrorCode());
                return;
            } catch (Exception ex) {
                response.reset();
                response.setStatus(500);
                logger.error("Unexpected", ex);
                return;
            }

            // Write the response.
            ChannelFuture future = e.getChannel().write(response);

            // Close the non-keep-alive connection after the write operation is done.
            if (!request.isKeepAlive()) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }

    }

    private void send100Continue(MessageEvent e) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
        e.getChannel().write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        // handle the case of to big requests.
        if (e.getCause() instanceof TooLongFrameException) {
            DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE);
            e.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            e.getCause().printStackTrace();
            e.getChannel().close();
        }

    }
}
