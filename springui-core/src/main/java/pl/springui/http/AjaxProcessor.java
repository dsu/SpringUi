package pl.springui.http;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.JsRenderer;
import pl.springui.components.UiAction;
import pl.springui.components.UiComponentI;
import pl.springui.components.UiPhaseListener;
import pl.springui.components.exceptions.UiException;
import pl.springui.components.exceptions.UserVisibleError;
import pl.springui.components.form.NotificationProcessor;
import pl.springui.utils.Profiler;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AjaxProcessor implements AjaxComponentsProcessor {

  @Autowired
  protected ApplicationContext appContext;

  @Autowired
  protected UiCtx ctx;

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Lazy
  @Autowired
  protected NotificationProcessor notifications;

  /*
   * (non-Javadoc)
   * 
   * @see pl.springui.http.AjaxComponentsProcessor#process(javax.servlet.http. HttpServletRequest,
   * javax.servlet.http.HttpServletResponse, java.lang.String)
   */
  @Override
  @Profiler
  public void process(UiAjaxRequest ajaxCtx) throws IOException {
    logger.debug("processAjax of {}", ajaxCtx);

    if (!ctx.canRestoreView()) {
      ajaxCtx.getResponse().sendError(418);
      return;
    }

    logger.debug("Ajax : Valid view {}, valid: {}", ctx.getViewGuid(), ctx.canRestoreView());

    try {
      setJsonHeaders(ajaxCtx.getResponse());

      long currentTimeMillis = System.currentTimeMillis();

      List<UiComponentI> components = ajaxCtx.restoreComponentsFromCtx(ctx);
      List<UiPhaseListener> listeners = ajaxCtx.restoreListeners(appContext);
      List<UiAction> actions = ajaxCtx.restoreActions(appContext);

      JSONArray componetsArray = new JSONArray();

      executePreRenderingPhases(components, listeners, actions);

      renderComponents(components, listeners, componetsArray);

      List<UiComponentI> extraProcess = ctx.getExtraProcess();
      if (extraProcess.size() > 0) {
        executePreRenderingPhases(extraProcess, listeners, actions);
        renderComponents(extraProcess, listeners, componetsArray);
      }

      // ignore?
      // ctx.checkForDanglingComponents();

      String js = notifications.processMessages(ctx.getMessages());
      if (js != null) {
        JSONObject componetResponse = new JSONObject();
        componetResponse.put("js", js);
        componetsArray.put(componetResponse);
      }

      logger.debug("Json response: {}", componetsArray);
      ajaxCtx.getResponse().getWriter().write(componetsArray.toString());
      ajaxCtx.getResponse().getWriter().close();
      logger.debug("ts: {}", (System.currentTimeMillis() - currentTimeMillis));
    } catch (

    Exception e) {
      if (ctx.isProductionMode()) {
        logger.error("Ajax component processing error", e);
        throw new UserVisibleError("500", e);
      } else {
        throw new UiException(e);
      }
    }

  }

  private void renderComponents(List<UiComponentI> components, List<UiPhaseListener> listeners,
      JSONArray componetsArray) throws JSONException {
    for (UiComponentI c : components) {
      String result = c.renderResponse();
      c.afterRenderResponse();

      if (listeners != null) {
        for (UiPhaseListener l : listeners) {
          l.beforeAfterRenderResponse(c);
        }
      }

      JSONObject componetResponse = new JSONObject();
      componetResponse.put("html", result);

      if (AopUtils.getTargetClass(c.getClass()).isAssignableFrom(JsRenderer.class)) {
        componetResponse.put("js", ((JsRenderer) c).renderJs());
      } else {
        componetResponse.put("js", "");
      }
      componetResponse.put("ids", c.getClientId());
      componetsArray.put(componetResponse);

    }
  }

  private void executePreRenderingPhases(List<UiComponentI> components,
      List<UiPhaseListener> listeners, List<UiAction> actions) {
    for (UiComponentI c : components) {
      c.clearPhases();
    }

    for (UiComponentI c : components) {
      if (listeners != null) {
        for (UiPhaseListener l : listeners) {
          l.beforeAjax(c);
        }
      }
    }

    for (UiAction action : actions) {
      action.beforeApplyRequest();
    }

    for (UiComponentI c : components) {
      if (listeners != null) {
        for (UiPhaseListener l : listeners) {
          l.beforeApplyRequest(c);
        }
      }
      c.applyRequest();
    }

    for (UiAction action : actions) {
      action.beforeRender();
    }

    for (UiComponentI c : components) {
      if (listeners != null) {
        for (UiPhaseListener l : listeners) {
          l.beforeProcess(c);
        }
      }

      c.process();
    }

    for (UiAction action : actions) {
      action.beforeRender();
    }

    for (UiComponentI c : components) {

      if (listeners != null) {
        for (UiPhaseListener l : listeners) {
          l.beforeRenderResponse(c);
        }
      }
      c.beforeRenderResponse();
    }
  }

  protected void setJsonHeaders(HttpServletResponse response) {
    response.setContentType("application/json;charset=UTF-8");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
  }

}
