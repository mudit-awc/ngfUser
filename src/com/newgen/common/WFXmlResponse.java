/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.common;


public class WFXmlResponse
{
  private String xmlString = null;
  private String strUpperCaseXmlString;

  public WFXmlResponse()
  {
  }

  public WFXmlResponse(String responseXml)
  {
    this.xmlString = responseXml;
    if (this.xmlString == null)
      return;
    this.strUpperCaseXmlString = this.xmlString;
  }

  public String getXmlString()
  {
    return this.xmlString;
  }

  public void setXmlString(String xmlString)
  {
    this.xmlString = xmlString;
    if (xmlString == null) {
      return;
    }

    this.strUpperCaseXmlString = xmlString;
  }

  public String getVal(String tag)
  {
    int idTag = this.strUpperCaseXmlString.indexOf("<" + tag + ">", 0);
    if (idTag == -1)
    {
      return "";
    }
    return this.xmlString.substring(idTag + tag.length() + 2, this.strUpperCaseXmlString.indexOf("</" + tag + ">", 0));
  }

  public WFXmlList createList(String listStartTag, String elementTag)
  {
    int startPos = this.strUpperCaseXmlString.indexOf("<" + listStartTag + ">");
    if (startPos == -1)
    {
      return new WFXmlList("", elementTag, 0, 0);
    }
    startPos += 2 + listStartTag.length();
    int endPos = this.strUpperCaseXmlString.indexOf("</" + listStartTag + ">", startPos);
    return new WFXmlList(this.xmlString, elementTag, startPos, endPos);
  }

  public String toString()
  {
    return this.xmlString;
  }

  public void insertValue(String strTagName, String strTagValue)
  {
    int nPos = this.strUpperCaseXmlString.lastIndexOf("</");
    String strPart1 = this.xmlString.substring(0, nPos);
    String strPart2 = this.xmlString.substring(nPos, this.strUpperCaseXmlString.length());
    String strTempTagXml = "<" + strTagName + ">" + strTagValue + "</" + strTagName + ">";
    this.xmlString = strPart1 + strTempTagXml + strPart2;
    this.strUpperCaseXmlString = this.xmlString;
  }
}
