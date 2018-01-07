package pl.springui.http;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import pl.springui.components.UiComponentI;
import pl.springui.components.exceptions.UiException;
import pl.springui.components.exceptions.UserVisibleError;
import pl.springui.components.form.Message;
import pl.springui.components.form.NotificationProcessor;
import pl.springui.components.form.NotificationProcessorI;
import pl.springui.components.utils.RedirectPage;
import pl.springui.utils.Profiler;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PageProcessor implements ComponentsProcessor {

  protected UiCtx ctx;

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  protected NotificationProcessorI notificationProcessor;

  @Autowired
  protected NotificationProcessor notifications;

  @Lazy
  @Autowired
  protected RedirectPage redirectPage;

  @Autowired
  public PageProcessor(UiCtx ctx) {
    super();
    this.ctx = ctx;
  }

  private String appendScriptToBody(String result, String js) {
    StringBuilder sb = new StringBuilder(result);
    sb.append("<script>");
    sb.append(js);
    sb.append("</script>");
    result = sb.toString();
    return result;
  }

  public NotificationProcessorI getNotificationProcessor() {
    return notificationProcessor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.http.ComponentsProcessor#process(javax.servlet.http. HttpServletRequest,
   * javax.servlet.http.HttpServletResponse, pl.springui.components.UiComponent)
   */
  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.http.ComponentsProcessor#process(javax.servlet.http. HttpServletRequest,
   * javax.servlet.http.HttpServletResponse, pl.springui.components.UiComponent)
   */
  @Override
  @Profiler
  public void process(HttpServletRequest request, HttpServletResponse response, UiComponentI page) {

    ctx.createNewTree();
    try {
      writeHtmlPage(request, response, page);
    } catch (Exception e) {
      logger.error("HTML component processing error", e);
      if (ctx.isProductionMode()) {
        throw new UserVisibleError("500");
      } else {
        throw new UiException(e);
      }
    }
  }

  protected void setHeaders(HttpServletResponse response) {
    response.setContentType("text/html;charset=UTF-8");
    // disable page cache - prevents from invalid view id
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Expires", "0");

  }

  public void setNotificationProcessor(NotificationProcessorI notificationProcessor) {
    this.notificationProcessor = notificationProcessor;
  }

  protected void writeHtmlPage(HttpServletRequest request, HttpServletResponse response,
      UiComponentI page) throws IOException {
    long currentTimeMillis = System.currentTimeMillis();

    String result = page.executePhases();

    if (result != null && result.startsWith("redirect:")) {
      // response.sendRedirect(result.substring("redirect:".length() - 1));
      String uri = result.substring("redirect:".length());
      setHeaders(response);
      response.getWriter().write(redirectPage.renderResponse(uri));
      response.getWriter().close();
      return;
    }

    setHeaders(response);
    ctx.checkForDanglingComponents();
    // checkForARootComponent();

    // https://stackoverflow.com/questions/11763779/how-to-read-flash-attributes-after-redirection-in-spring-mvc-3-1
    Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
    if (inputFlashMap != null) {
      Object object = inputFlashMap.get("msg");
      if (object instanceof Message) {
        ctx.addMessage((Message) object);
        logger.debug("Add a message");
      }
    }

    if (notificationProcessor != null) {
      logger.debug("Processing messages ({}) ", ctx.getMessages());
      String js = notificationProcessor.processMessages(ctx.getMessages());
      if (js != null) {
        result = appendScriptToBody(result, js);
      }
    } else {
      logger.debug("There is no notificationProcessor");
    }

    if (result != null) {
      response.getWriter().write(result);
    }
    response.getWriter().close();
    logger.debug("ts {} ms for viewGuid : {}",
        "" + (System.currentTimeMillis() - currentTimeMillis), ctx.getViewGuid());
  }

}
