package droid;

import com.sun.org.apache.xpath.internal.XPathAPI;

import java.io.*;
import java.util.regex.PatternSyntaxException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class UpdateToken extends org.apache.tools.ant.Task
{
  private String token;
  private String replace;
  private String[] xpath;
  private File fileSrc;
  private String divider;
  private boolean isLog;
  private String dtdFilePath;
  private String dtdDefPath;

  public void execute()
    throws BuildException
  {
    try
    {
      Document dom = parseXML(getFileSrc(), this.isLog);

      if ((getString() == null) || (getReplace() == null) || (getXpath()[0] == null) || (getFileSrc() == null) || (dom == null))
      {
        throw new Exception("Required attribute(s) is(are) missing or file parsed unsuccessfully.");
      }

      if (getDivider() == null) {
        this.divider = "~";
        System.out.println("Using default divider.");
      }
      int multiple;
      try {
        multiple = multipleParse(getXpath());
      } catch (PatternSyntaxException e) {
        throw new Exception("Invalid character or string for the divider");
      }

      if (multiple > 1) {
        pluralReplace(getXpath(), dom, getString(), getReplace());
      }
      else {
        singleReplace(getXpath(), dom, getString(), getReplace());
      }

      transformXML(dom, getFileSrc(), this.isLog);
    }
    catch (Exception e) {
      throw new BuildException(e.getMessage(), e);
    }
  }

  private void singleReplace(String[] xpath, Document dom, String token, String replace)
  {
    System.out.println(xpath[0]);
    try
    {
      replace(xpath[0], dom, token, replace);
    } catch (TransformerException e) {
      System.err.println("Transformer exception thrown.");
    }
  }

  private void pluralReplace(String[] xpath, Document dom, String token, String replace)
    throws TransformerException
  {
    try
    {
      for (int j = 0; j < xpath.length; ++j) {
        System.out.println(xpath[j]);
        replace(xpath[j], dom, token, replace);
      }
    } catch (TransformerException e) {
      throw new TransformerException("Replace in pluralReplace has thrown Transformer Exception.", e);
    }
  }

  private Document parseXML(File f, boolean isLog)
    throws Exception
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try
    {
      DocumentBuilder build;
      if (this.dtdFilePath != null) {
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        build = factory.newDocumentBuilder();

		build.setEntityResolver( new EntityResolver() {
		   	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		       return new InputSource(new InputStreamReader(new FileInputStream(dtdFilePath)));
		   		}
			}
		);

      }
      else
      {
        factory.setValidating(false);
        factory.setExpandEntityReferences(false);
        build = factory.newDocumentBuilder();
        if (isLog)
        {
          build.setEntityResolver(new Log4jEntityResolver());
        }
      }

      Document doc = build.parse(f);
      return doc;
    }
    catch (Exception e) {
      throw new Exception(e.getClass().getName() + " is thrown from parseXML for file '" + f.getName() + "'", e);
    }
  }

  private void transformXML(Document doc, File f, boolean isLog)
    throws TransformerConfigurationException, TransformerException
  {
    TransformerFactory factory = TransformerFactory.newInstance();
    try
    {
      Source src = new DOMSource(doc);
      Result dest = new StreamResult(f);

      Transformer trans = factory.newTransformer();
      if (isLog)
        trans.setOutputProperty("doctype-system", "log4j.dtd");
      else if (null != this.dtdDefPath) {
        trans.setOutputProperty("doctype-system", this.dtdDefPath);
      }

      trans.transform(src, dest);
    }
    catch (TransformerConfigurationException e) {
      throw new TransformerConfigurationException("Transformer Configuration Exception thrown from transformXML.");
    }
    catch (TransformerException e2) {
      throw new TransformerException("Transformer Exception thrown from transformXML");
    }
  }

  private int multipleParse(String[] s)
    throws PatternSyntaxException
  {
    String tmp = this.xpath[0];
    this.xpath = tmp.split(this.divider);
    return this.xpath.length;
  }

  private void replace(String xpath, Document dom, String token, String replace)
    throws TransformerException
  {
    try
    {
      NodeList nodes = XPathAPI.selectNodeList(dom, xpath);

      System.out.println("Node List retrieved.");

      for (int i = 0; i < nodes.getLength(); ++i)
      {
        Node node = nodes.item(i);

        if (node.getNodeType() == 2) {
          Attr atr = (Attr)nodes.item(i);
          String buffer = atr.getFirstChild().getNodeValue();

          if (-1 != buffer.indexOf(token)) {
            String oldBuffer = buffer;
            buffer = buffer.replace(token, replace);
            atr.getFirstChild().setNodeValue(buffer);
          }
        }
        else {
          Element elem = (Element)nodes.item(i);
          if (elem.getFirstChild() != null) {
            String buffer = elem.getFirstChild().getNodeValue();

            if (-1 != buffer.indexOf(token)) {
              String oldBuffer = buffer;
              buffer = buffer.replace(token, replace);
              elem.getFirstChild().setNodeValue(buffer);
            }
          }
        }
      }
    }
    catch (TransformerException e) {
      throw new TransformerException("Transformer Exception thrown from replace method.", e);
    }
  }

  public void setString(String s)
  {
    this.token = s;
  }

  public void setReplace(String s)
  {
    this.replace = s;
  }

  public void setSrc(File f)
  {
    this.fileSrc = f;
  }

  public void setXpath(String s)
  {
    this.xpath = new String[1];
    this.xpath[0] = s;
  }

  public void setDivider(String s)
  {
    this.divider = s;
  }

  public String getReplace() {
    return this.replace;
  }

  public String[] getXpath() {
    return this.xpath;
  }

  public String getString() {
    return this.token;
  }

  public File getFileSrc() {
    return this.fileSrc;
  }

  public String getDivider() {
    return this.divider;
  }

  public void setLog(boolean b) {
    this.isLog = b;
  }

  public String getDtdFilePath() {
    return this.dtdFilePath;
  }

  public void setDtdFilePath(String dtdFilePath) {
    this.dtdFilePath = dtdFilePath;
  }

  public String getDtdDefPath() {
    return this.dtdDefPath;
  }

  public void setDtdDefPath(String dtdDefPath) {
    this.dtdDefPath = dtdDefPath;
  }
}