package pl.springui.components.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slieb.formatter.HtmlExceptionFormatOptions;
import org.slieb.formatter.HtmlExceptionFormatter;

/**
 * Contains a description of a generated view for debugging purposes
 * 
 * @author dsu
 *
 */
public class ViewModelTrace {
  private Class engineClass;
  private long executionMs;
  private Date generated;
  Map<String, String> mapSouroce;
  private String modelTrace;

  private Exception processingException;

  String xmlString;

  public ViewModelTrace(Class engineClass) {
    super();
    this.engineClass = engineClass;
    generated = new Date();
  }

  public Class getEngineClass() {
    return engineClass;
  }

  public long getExecutionMs() {
    return executionMs;
  }

  public Date getGenerated() {
    return generated;
  }

  public Map<String, String> getMapSouroce() {
    return mapSouroce;
  }

  public String getModelTrace() {
    return modelTrace;
  }

  public Exception getProcessingException() {
    return processingException;
  }

  public String getXmlString() {
    return xmlString;
  }

  public String printException() {
    if (processingException != null) {

      HtmlExceptionFormatOptions options = new HtmlExceptionFormatOptions();
      options.setPrintDetails(true);
      String html = new HtmlExceptionFormatter().toString(processingException);
      return html;
    }
    return null;
  }

  public void setEngineClass(Class engineClass) {
    this.engineClass = engineClass;
  }

  public void setExecutionMs(long executionMs) {
    this.executionMs = executionMs;
  }

  public void setMapSouroce(Map<String, Object> mapSouroce) {

    // write all the data to string, so thr references can bee garbage
    // collected
    HashMap<String, String> map = new HashMap<>();
    for (Entry<String, Object> e : mapSouroce.entrySet()) {
      map.put(e.getKey(), String.valueOf(e.getValue()));
    }

    this.mapSouroce = map;
  }

  public void setModelTrace(String modelTrace) {
    this.modelTrace = modelTrace;
  }

  public void setProcessingException(Exception processingException) {
    this.processingException = processingException;
  }

  public void setXmlString(String xmlString) {
    this.xmlString = xmlString;
  }

  @Override
  public String toString() {
    return "ViewModelTrace [generated=" + generated + ", modelTrace=" + modelTrace
        + ", engineClass=" + engineClass + ", executionMs=" + executionMs
        + ", is processingException=" + (processingException != null) + ", mapSouroce is null ="
        + (null != mapSouroce) + ", xmlString=" + xmlString + "]";
  }

}