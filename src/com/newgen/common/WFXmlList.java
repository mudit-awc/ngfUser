/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.common;



import java.util.regex.Pattern;

public class WFXmlList
{
  public String xmlString = null;
  public String listTag = null;
  int startPos = 0;
  int endPos = 0;
  int elementStart = 0;
  int elementEnd = 0;
  private String strUpperCaseXmlString;
  private String strUpperCaseListTag;
  boolean hasSameLen = true;

  public WFXmlList()
  {
  }

  public WFXmlList(String outXml, String tag, int startPos, int endPos)
  {
    this.xmlString = outXml;
    this.listTag = tag;

    this.strUpperCaseListTag = tag.toUpperCase();

    if (this.xmlString.length() != this.xmlString.toUpperCase().length()) {
      this.hasSameLen = false;
    }
    if (!(this.hasSameLen)) {
      this.strUpperCaseXmlString = Pattern.compile(tag, 2).matcher(this.xmlString).replaceAll(tag.toUpperCase());
    }
    else
      this.strUpperCaseXmlString = this.xmlString.toUpperCase();
    this.startPos = startPos;
    this.endPos = endPos;
    this.elementStart = this.startPos;
    this.elementEnd = this.strUpperCaseXmlString.indexOf("</" + this.strUpperCaseListTag + ">", this.elementStart);
    if (this.elementEnd == -1)
      return;
    this.elementEnd += this.listTag.length() + 3;
  }

  public WFXmlList(String outXml, String tag, int startPos, int endPos, boolean hasSameLength)
  {
    this.xmlString = outXml;
    this.listTag = tag;
    this.hasSameLen = hasSameLength;
    if (!(this.hasSameLen)) {
      this.strUpperCaseXmlString = Pattern.compile(tag, 2).matcher(this.xmlString).replaceAll(tag.toUpperCase());
    }
    else
      this.strUpperCaseXmlString = this.xmlString.toUpperCase();
    this.strUpperCaseListTag = tag.toUpperCase();
    this.startPos = startPos;
    this.endPos = endPos;
    this.elementStart = this.startPos;
    this.elementEnd = this.strUpperCaseXmlString.indexOf("</" + this.strUpperCaseListTag + ">", this.elementStart);
    if (this.elementEnd == -1)
      return;
    this.elementEnd += this.listTag.length() + 3;
  }

  public void reInitialize()
  {
    this.elementStart = this.startPos;
    this.elementEnd = this.strUpperCaseXmlString.indexOf("</" + this.strUpperCaseListTag + ">", this.elementStart);
    if (this.elementEnd == -1)
      return;
    this.elementEnd += this.strUpperCaseListTag.length() + 3;
  }

  public void reInitialize(boolean isAscending)
  {
    if (isAscending)
    {
      this.elementStart = this.startPos;
      this.elementEnd = this.strUpperCaseXmlString.indexOf("</" + this.strUpperCaseListTag + ">", this.elementStart);

      if (this.elementEnd == -1)
        return;
      this.elementEnd += this.strUpperCaseListTag.length() + 3;
    }
    else
    {
      reInitializeDesc();
    }
  }

  boolean reInitializeDesc()
  {
    this.elementEnd = this.strUpperCaseXmlString.lastIndexOf("</" + this.strUpperCaseListTag + ">", this.endPos);

    if (this.elementEnd == -1)
    {
      return false;
    }
    this.elementEnd += this.strUpperCaseListTag.length() + 3;
    this.elementStart = this.strUpperCaseXmlString.lastIndexOf("<" + this.strUpperCaseListTag + ">", this.elementEnd);

    return true;
  }

  public WFXmlList createList(String listStartTag, String elementTag)
  {
    int initPos = this.strUpperCaseXmlString.indexOf("<" + listStartTag.toUpperCase() + ">", this.elementStart);
    if (initPos == -1)
    {
      return new WFXmlList("", elementTag, 0, 0);
    }
    initPos += 2 + listStartTag.length();
    int lastPos = this.strUpperCaseXmlString.indexOf("</" + listStartTag.toUpperCase() + ">", initPos);

    return new WFXmlList(this.xmlString, elementTag, initPos, lastPos);
  }

  public boolean hasMoreElements()
  {
    int index1 = this.strUpperCaseXmlString.indexOf("<" + this.strUpperCaseListTag + ">", this.elementStart);
    int index2 = this.strUpperCaseXmlString.indexOf("</" + this.strUpperCaseListTag + ">", this.elementStart);

    return ((index2 != -1) && (index1 != -1) && (index2 > index1) && (index2 < this.endPos));
  }

  public boolean hasMoreElements(boolean isAscending)
  {
    if (this.elementEnd == -1)
    {
      return false;
    }
    if (isAscending)
    {
      int index = this.strUpperCaseXmlString.indexOf("<" + this.strUpperCaseListTag + ">", this.elementStart);
      return ((index != -1) && (index < this.endPos));
    }

    return hasMoreElementsDesc();
  }

  boolean hasMoreElementsDesc()
  {
    int index = this.strUpperCaseXmlString.lastIndexOf("<" + this.strUpperCaseListTag + ">", this.elementStart);

    return ((index != -1) && (index >= this.startPos));
  }

  public void skip()
  {
    this.elementStart = this.elementEnd;
    this.elementEnd = (this.strUpperCaseXmlString.indexOf("</" + this.strUpperCaseListTag + ">", this.elementStart) + this.strUpperCaseListTag.length() + 3);
  }

  public void skip(boolean isAscending)
  {
    if (isAscending)
    {
      this.elementStart = this.elementEnd;
      this.elementEnd = this.strUpperCaseXmlString.indexOf("</" + this.strUpperCaseListTag + ">", this.elementStart);

      if (this.elementEnd == -1)
        return;
      this.elementEnd += this.strUpperCaseListTag.length() + 3;
    }
    else
    {
      skipDesc();
    }
  }

  void skipDesc()
  {
    this.elementEnd = this.elementStart;
    this.elementStart = this.strUpperCaseXmlString.lastIndexOf("<" + this.strUpperCaseListTag + ">", this.elementEnd - 1);

    if (this.elementStart != -1)
      return;
    this.elementEnd = -1;
  }

  @SuppressWarnings("unused")
public String getVal(String tag)
  {
    String tempString = "";
    if ((this.elementStart == -1) || (this.elementEnd == -1))
    {
     //System.out.println("Problem in call sequence.");
      return "";
    }

    int startIndex = this.strUpperCaseXmlString.indexOf("<" + tag.toUpperCase() + ">", this.elementStart);
    if ((startIndex == -1) || (startIndex >= this.elementEnd))
    {
      return "";
    }
    return this.xmlString.substring(startIndex + tag.length() + 2, this.strUpperCaseXmlString.indexOf("</" + tag.toUpperCase() + ">", this.elementStart));
  }

  public String toString()
  {
    if (this.xmlString == null)
    {
      return "";
    }

    return this.xmlString;
  }

  public String getCurrentXml()
  {
    if (this.xmlString == null)
    {
      return "";
    }

    return this.xmlString.substring(this.elementStart, this.elementEnd);
  }
}
