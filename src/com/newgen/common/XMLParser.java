// NEWGEN SOFTWARE TECHNOLOGIES LIMITED
// Group : CIG
// Product / Project : OmniFlow /Remfry-Sagar
// Module : Common for all Processes
// File Name : XMLParser.java
// Author : Sandeep Singh
// Date written : 01/10/2014
// Description : This Java File Contains functions Common
//				to all the processes for dealing with XML's
//				for parsing ,setting,validating.

package com.newgen.common;

import java.io.Serializable;

public class XMLParser implements Serializable
{
  /**
	 * 
	 */
	
	private String parseString;
  private String copyString;
  private int IndexOfPrevSrch;

  public XMLParser()
  {
  }

  public XMLParser(String parseThisString)
  {
    this.copyString = new String(parseThisString);
    this.parseString = toUpperCase(this.copyString, 0, 0);
  }
  	//Function Name           setInputXML
	//Input Parameters        ParseThisString
	//Output parameters       NA
	//Return Values           NA
	//Description             This method  sets Parameters
  	//						   for given String
	//Author                  Sandeep Singh
	//Date                    03/10/2014
  public void setInputXML(String ParseThisString)
  {
    if (ParseThisString != null)
    {
      this.copyString = new String(ParseThisString);
      this.parseString = toUpperCase(this.copyString, 0, 0);
      this.IndexOfPrevSrch = 0;
    }
    else
    {
      this.parseString = null;
      this.copyString = null;
      this.IndexOfPrevSrch = 0;
    }
  }
  	//Function Name           getServiceName
	//Input Parameters        ParseThisString
	//Output parameters       String
	//Return Values           Service Name
	//Description             This method  returns
  	//						 the service name from XML	
	//Author                  Sandeep Singh
	//Date                    03/10/2014
  public String getServiceName()
  {
    try
    {
      return new String(this.copyString.substring(this.parseString.indexOf(toUpperCase("<Option>", 0, 0)) + new String(toUpperCase("<Option>", 0, 0)).length(), this.parseString.indexOf(toUpperCase("</Option>", 0, 0))));
    }
    catch (StringIndexOutOfBoundsException e) {
      throw e;
    }
  }
  	//Function Name           getServiceName
	//Input Parameters        char
	//Output parameters       String
	//Return Values           Service Name
	//Description             This method  returns
	//						 the service name from XML	
	//Author                  Sandeep Singh
	//Date                    03/10/2014
  public String getServiceName(char chr)
  {
    try
    {
      if (chr == 'A')
        return new String(this.copyString.substring(this.parseString.indexOf("<AdminOption>".toUpperCase()) + new String("<AdminOption>".toUpperCase()).length(), this.parseString.indexOf("</AdminOption>".toUpperCase())));
      return "";
    } catch (StringIndexOutOfBoundsException e) {
    }
    return "NoServiceFound";
  }
  	//Function Name           validateXML
	//Input Parameters        NA
	//Output parameters       boolean
	//Return Values           True/False
	//Description             This method  validates 
  	//						  the XMl Passed in.
	//Author                  Sandeep Singh
	//Date                    05/10/2014
  public boolean validateXML()
  {
    try
    {
      return (this.parseString.indexOf("<?xml version=\"1.0\"?>".toUpperCase()) == -1);
    }
    catch (StringIndexOutOfBoundsException e)
    {
    }
    return false;
  }
  	//Function Name           getValueOf
	//Input Parameters        Tag 
	//Output parameters       String
	//Return Values           value in Tag
	//Description             This method  returns the 
  	//						  value in Tag.
	//Author                  Sandeep Singh
	//Date                    08/10/2014
  public String getValueOf(String valueOf)
  {
    try
    {
      return new String(this.copyString.substring(this.parseString.indexOf("<" + toUpperCase(valueOf, 0, 0) + ">") + valueOf.length() + 2, this.parseString.indexOf("</" + toUpperCase(valueOf, 0, 0) + ">")));
    } catch (StringIndexOutOfBoundsException e) {
    }
    return "";
  }
  //Function Name           getValueOf
	//Input Parameters        Tag, type
	//Output parameters       String
	//Return Values           value in Tag
	//Description             This method  returns the 
	//						  value in Tag depending upon Type.
	//Author                  Sandeep Singh
	//Date                    08/10/2014
  public String getValueOf(String valueOf, String type)
  {
    try
    {
      if (type.equalsIgnoreCase("Binary")) {
        int startPos = this.copyString.indexOf("<" + valueOf + ">");
        if (startPos == -1)
          return "";
        int endPos = this.copyString.lastIndexOf("</" + valueOf + ">");
        startPos += new String("<" + valueOf + ">").length();
        return this.copyString.substring(startPos, endPos);
      }
      return "";
    } catch (StringIndexOutOfBoundsException e) {
    }
    return "";
  }
  	//Function Name           getValueOf
	//Input Parameters        Tag,fromlast
	//Output parameters       String
	//Return Values           value in Tag
	//Description             This method  returns the 
	//						  value in Tag from start or end 
  	//							depending upon 2nd parameter.
	//Author                  Sandeep Singh
	//Date                    08/10/2014
  public String getValueOf(String valueOf, boolean fromlast)
  {
    try
    {
      if (fromlast) {
        return new String(this.copyString.substring(this.parseString.indexOf("<" + toUpperCase(valueOf, 0, 0) + ">") + valueOf.length() + 2, this.parseString.lastIndexOf("</" + toUpperCase(valueOf, 0, 0) + ">")));
      }
      return new String(this.copyString.substring(this.parseString.indexOf("<" + toUpperCase(valueOf, 0, 0) + ">") + valueOf.length() + 2, this.parseString.indexOf("</" + toUpperCase(valueOf, 0, 0) + ">")));
    } catch (StringIndexOutOfBoundsException e) {
    }
    return "";
  }
  	//Function Name           getValueOf
	//Input Parameters        Tag,start,end
	//Output parameters       String
	//Return Values           value in Tag
	//Description             This method  returns the 
	//						  value in Tag acc. to index.
	//Author                  Sandeep Singh
	//Date                    08/10/2014
  public String getValueOf(String valueOf, int start, int end)
  {
    try
    {
      if (start >= 0) {
        int endIndex = this.parseString.indexOf("</" + toUpperCase(valueOf, 0, 0) + ">", start);
        if ((endIndex > start) && (((end == 0) || (end >= endIndex))))
          return new String(this.copyString.substring(this.parseString.indexOf("<" + toUpperCase(valueOf, 0, 0) + ">", start) + valueOf.length() + 2, endIndex));
      }
      return "";
    } catch (StringIndexOutOfBoundsException e) {
    }
    return "";
  }
  	//Function Name           getStartIndex
	//Input Parameters        Tag,start,end
	//Output parameters       int
	//Return Values           index of Tag
	//Description             This method  returns the 
	//						  starting index of tag.
	//Author                  Sandeep Singh
	//Date                    12/10/2014
  public int getStartIndex(String tag, int start, int end)
  {
    try
    {
      if (start >= 0) {
        int startIndex = this.parseString.indexOf("<" + toUpperCase(tag, 0, 0) + ">", start);
        if ((startIndex >= start) && (((end == 0) || (end >= startIndex))))
          return (startIndex + tag.length() + 2);
      }
      return -1;
    } catch (StringIndexOutOfBoundsException e) {
    }
    return -1;
  }
  	//Function Name           getEndIndex
	//Input Parameters        Tag,start,end
	//Output parameters       int
	//Return Values           index of Tag
	//Description             This method  returns the 
	//						  end index of tag.
	//Author                  Sandeep Singh
	//Date                    12/10/2014
  public int getEndIndex(String tag, int start, int end)
  {
    try
    {
      if (start >= 0) {
        int endIndex = this.parseString.indexOf("</" + toUpperCase(tag, 0, 0) + ">", start);
        if ((endIndex > start) && (((end == 0) || (end >= endIndex))))
          return endIndex;
      }
      return -1;
    } catch (StringIndexOutOfBoundsException e) {
    }
    return -1;
  }
  	//Function Name           getTagStartIndex
	//Input Parameters        Tag,start,end
	//Output parameters       int
	//Return Values           index of Tag
	//Description             This method  returns the 
	//						  start index of tag.
	//Author                  Sandeep Singh
	//Date                    14/10/2014
  public int getTagStartIndex(String tag, int start, int end)
  {
    try
    {
      if (start >= 0) {
        int startIndex = this.parseString.indexOf("<" + toUpperCase(tag, 0, 0) + ">", start);
        if ((startIndex >= start) && (((end == 0) || (end >= startIndex))))
          return startIndex;
      }
      return -1;
    } catch (StringIndexOutOfBoundsException e) {
    }
    return -1;
  }
  	//Function Name           getTagEndIndex
	//Input Parameters        Tag,start,end
	//Output parameters       int
	//Return Values           value in Tag
	//Description             This method  returns the 
	//						  start index of tag.
	//Author                  Sandeep Singh
	//Date                    14/10/2014
  public int getTagEndIndex(String tag, int start, int end)
  {
    try
    {
      if (start >= 0) {
        int endIndex = this.parseString.indexOf("</" + toUpperCase(tag, 0, 0) + ">", start);
        if ((endIndex > start) && (((end == 0) || (end >= endIndex))))
          return (endIndex + tag.length() + 3);
      }
      return -1;
    } catch (StringIndexOutOfBoundsException e) {
    }
    return -1;
  }
  //Function Name           getFirstValueOf
	//Input Parameters        valueOf
	//Output parameters       String
	//Return Values           first value of tag
	//Description             This method  returns the 
	//						  first value of read tag.
	//Author                  Sandeep Singh
	//Date                    14/10/2014
  public String getFirstValueOf(String valueOf)
  {
    try
    {
      this.IndexOfPrevSrch = this.parseString.indexOf("<" + toUpperCase(valueOf, 0, 0) + ">");
      return new String(this.copyString.substring(this.IndexOfPrevSrch + valueOf.length() + 2, this.parseString.indexOf("</" + toUpperCase(valueOf, 0, 0) + ">")));
    } catch (StringIndexOutOfBoundsException e) {
    }
    return "";
  }
  //Function Name           getFirstValueOf
	//Input Parameters        valueOf,start
	//Output parameters       String
	//Return Values           first value of tag with start index
	//Description             This method  returns the 
	//						  first value of read tag with index start.
	//Author                  Sandeep Singh
	//Date                    14/10/2014
  public String getFirstValueOf(String valueOf, int start)
  {
    try
    {
      this.IndexOfPrevSrch = this.parseString.indexOf("<" + toUpperCase(valueOf, 0, 0) + ">", start);
      return new String(this.copyString.substring(this.IndexOfPrevSrch + valueOf.length() + 2, this.parseString.indexOf("</" + toUpperCase(valueOf, 0, 0) + ">", start)));
    } catch (StringIndexOutOfBoundsException e) {
    }
    return "";
  }
  	//Function Name           getNextValueOf
	//Input Parameters        valueOf
	//Output parameters       String
	//Return Values           Next value of tag 
	//Description             This method  returns the 
	//						  Next value of read tag
	//Author                  Sandeep Singh
	//Date                    16/10/2014
  public String getNextValueOf(String valueOf)
  {
    try
    {
      this.IndexOfPrevSrch = this.parseString.indexOf("<" + toUpperCase(valueOf, 0, 0) + ">", this.IndexOfPrevSrch + valueOf.length() + 2);
      return new String(this.copyString.substring(this.IndexOfPrevSrch + valueOf.length() + 2, this.parseString.indexOf("</" + toUpperCase(valueOf, 0, 0) + ">", this.IndexOfPrevSrch)));
    } catch (StringIndexOutOfBoundsException e) {
    }
    return "";
  }
  	//Function Name           getNoOfFields
	//Input Parameters        tag
	//Output parameters       int
	//Return Values           No. of Fields in Tag.
	//Description             This method  returns the 
	//						  No. of fields in a Tag.
	//Author                  Sandeep Singh
	//Date                    16/10/2014
  public int getNoOfFields(String tag)
  {
    int noOfFields = 0;
    int beginPos = 0;
    try {
      tag = toUpperCase(tag, 0, 0) + ">";
      while (this.parseString.indexOf("<" + tag, beginPos) != -1) {
        ++noOfFields;
        beginPos = this.parseString.indexOf("</" + tag, beginPos);
        if (beginPos == -1)
          break;
        beginPos += tag.length() + 2;
      }
    } catch (StringIndexOutOfBoundsException e) {
    }
    return noOfFields;
  }
  //Function Name           getNoOfFields
	//Input Parameters        tag,startPos,endPos
	//Output parameters       int
	//Return Values           No. of Fields in Tag b/w given index's
	//Description             This method  returns the 
	//						  No. of fields in a Tag b/w given index's.
	//Author                  Sandeep Singh
	//Date                    16/10/2014
  public int getNoOfFields(String tag, int startPos, int endPos)
  {
    int noOfFields = 0;
    int beginPos = startPos;
    try {
      tag = toUpperCase(tag, 0, 0) + ">";
      //break label91:
      do { beginPos = this.parseString.indexOf("</" + tag, beginPos) + tag.length() + 2;
        if ((beginPos != -1) && (((beginPos <= endPos) || (endPos == 0))))
          ++noOfFields;
        label91: if (this.parseString.indexOf("<" + tag, beginPos) == -1) break;  }
      while ((beginPos < endPos) || (endPos == 0));
    }
    catch (StringIndexOutOfBoundsException e)
    {
    }

    return noOfFields;
  }
  	//Function Name           convertToSQLString
	//Input Parameters        strName
	//Output parameters       String
	//Return Values           SQLString of general String 
	//Description             This method  returns the 
	//						  SQLString format of general String .
	//Author                  Sandeep Singh
	//Date                    16/10/2014
  public String convertToSQLString(String strName)
  {
    int count;
    try
    {
      count = strName.indexOf("[");
      while (count != -1) {
        strName = strName.substring(0, count) + "[[]" + strName.substring(count + 1, strName.length());
        count = strName.indexOf("[", count + 2);
      }
    }
    catch (Exception e) {
    }
    try {
    int  e = strName.indexOf("_");
      while (e != -1) {
        strName = strName.substring(0, e) + "[_]" + strName.substring(e + 1, strName.length());
        e = strName.indexOf("_", e + 2);
      }
    }
    catch (Exception e) {
    }
    try {
     int e = strName.indexOf("%");
      while (e != -1) {
        strName = strName.substring(0, e) + "[%]" + strName.substring(e + 1, strName.length());
        e = strName.indexOf("%", e + 2);
      }
    }
    catch (Exception e) {
    }
    strName = strName.replace('?', '_');
    return strName;
  }
  //Function Name             getValueOf
	//Input Parameters        valueOf,type,from,end
	//Output parameters       String
	//Return Values           returns value of Tag
	//Description             This method  returns the 
	//						  value of Tag for given type,startIndex,endIndex .
	//Author                  Sandeep Singh
	//Date                    19/10/2014
  public String getValueOf(String valueOf, String type, int from, int end)
  {
    try
    {
      if (type.equalsIgnoreCase("Binary")) {
        int startPos = this.copyString.indexOf("<" + valueOf + ">", from);
        if (startPos == -1)
          return "";
        int endPos = this.copyString.indexOf("</" + valueOf + ">", from);
        if (endPos > end)
          return "";
        startPos += new String("<" + valueOf + ">").length();
        return this.copyString.substring(startPos, endPos);
      }
      return "";
    } catch (StringIndexOutOfBoundsException e) {
    }
    return "";
  }
  	//Function Name           toUpperCase
	//Input Parameters        valueOf,begin,end
	//Output parameters       String
	//Return Values           returns value of Tag in UpperCase
	//Description             This method  returns the 
	//						  value of Tag for given startIndex,endIndex 
  	//						  in Upper Case .
	//Author                  Sandeep Singh
	//Date                    19/10/2014
  public String toUpperCase(String valueOf, int begin, int end)
    throws StringIndexOutOfBoundsException
  {
    String returnStr = "";
    try {
      int count = valueOf.length();
      char[] strChar = new char[count];
      valueOf.getChars(0, count, strChar, 0);
      while (count-- > 0)
        strChar[count] = Character.toUpperCase(strChar[count]);
      returnStr = new String(strChar);
    }
    catch (ArrayIndexOutOfBoundsException e) {
    }
    return returnStr;
  }
  	//Function Name           changeValue
	//Input Parameters        ParseString,TagName,NewValue
	//Output parameters       String
	//Return Values           returns  XML with new Value in Tag.
	//Description             This method  returns the 
	//						  XML with new Value in Tag.
	//Author                  Sandeep Singh
	//Date                    21/10/2014
  public String changeValue(String ParseString, String TagName, String NewValue)
  {
    try
    {
      String ParseStringTmp = ParseString.toUpperCase();
      String StrTag = new String("<" + TagName + ">").toUpperCase();

      int StartIndex = ParseStringTmp.indexOf(StrTag) + StrTag.length();
      int EndIndex = ParseStringTmp.indexOf(new String("</" + TagName + ">").toUpperCase());

      String RetStr = ParseString.substring(0, StartIndex);
      RetStr = RetStr + NewValue + ParseString.substring(EndIndex);
      return RetStr;
    } catch (Exception e) {
    }
    return "";
  }
  	//Function Name           changeValue
	//Input Parameters        TagName,NewValue
	//Output parameters       String
	//Return Values           returns tag with new Value in Tag.
	//Description             This method  returns the 
	//						  tag with new Value in Tag.
	//Author                  Sandeep Singh
	//Date                    21/10/2014
  public void changeValue(String TagName, String NewValue)
  {
    try
    {
      int EndIndex;
      String RetStr;
      String StrTag = "<" + TagName + ">".toUpperCase();

      int StartIndex = this.parseString.indexOf(StrTag);
      if (StartIndex > -1)
      {
        StartIndex += StrTag.length();
        EndIndex = this.parseString.indexOf("</" + TagName + ">".toUpperCase());

        RetStr = this.copyString.substring(0, StartIndex);
        this.copyString = RetStr + NewValue + this.copyString.substring(EndIndex);
      }
      else {
        EndIndex = StartIndex = this.parseString.lastIndexOf("</");
        RetStr = this.copyString.substring(0, StartIndex);
        this.copyString = RetStr + "<" + TagName + ">" + NewValue + "</" + TagName + ">" + this.copyString.substring(EndIndex);
      }
      this.parseString = toUpperCase(this.copyString, 0, 0);
    }
    catch (Exception e)
    {
    }
  }

  public String toString()
  {
    return this.copyString;
  }
  	//Function Name           ParseFieldValue
	//Input Parameters        pField,pNode,pNodeValue
	//Output parameters       String
	//Return Values           returns Parsed Value in Tag.
	//Description             This method  returns the 
	//						  Parsed Value in Tag.
	//Author                  Sandeep Singh
	//Date                    24/10/2014
  public String ParseFieldValue(String pField, String pNode, String pNodeValue)
  {
    try
    {
      int iStartIndex = this.parseString.indexOf("<" + toUpperCase(pField, 0, 0) + ">" + "<" + toUpperCase(pNode, 0, 0) + ">" + toUpperCase(pNodeValue, 0, 0) + "</" + toUpperCase(pNode, 0, 0) + ">");

      if (iStartIndex != -1)
      {
        iStartIndex += pField.length() + 2;
        return new String(this.copyString.substring(iStartIndex, this.parseString.indexOf("</" + toUpperCase(pField, 0, 0) + ">", iStartIndex)));
      }

      return "";
    }
    catch (StringIndexOutOfBoundsException e)
    {
      e.printStackTrace(); }
    return "";
  }
  	//Function Name           getValueListXml
	//Input Parameters        pFieldName
	//Output parameters       String
	//Return Values           returns value in a Field.
	//Description             This method  returns the 
	//						  value in a Field..
	//Author                  Sandeep Singh
	//Date                    26/10/2014
  public String getValueListXml(String pFieldName)
  {
    try
    {
      int iOffset = this.parseString.indexOf("<NAME>" + pFieldName.toUpperCase() + "</NAME>");
      if (iOffset != -1)
      {
        int iValueListIndex = this.parseString.indexOf("<VALUELIST>", iOffset);
        if (iValueListIndex != -1)
        {
          int iEndIndex = this.parseString.indexOf("</FIELD>", iOffset);
          if ((iEndIndex != -1) && (iEndIndex > iValueListIndex))
          {
            return this.copyString.substring(iValueListIndex, iEndIndex);
          }
        }
      }
    }
    catch (StringIndexOutOfBoundsException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  	//Function Name           getFieldValue
	//Input Parameters        pFieldName
	//Output parameters       String
	//Return Values           returns field Value for given Field.
	//Description             This method  returns the 
	//						  field Value for given Field
	//Author                  Sandeep Singh
	//Date                    24/10/2014
  public String getFieldValue(String pFieldName)
  {
    try
    {
      StringBuffer sbFieldName = new StringBuffer("<NAME>");
      sbFieldName.append(pFieldName.toUpperCase());
      sbFieldName.append("</NAME>");
      int iIndex = this.parseString.indexOf(sbFieldName.toString());
      if (iIndex != -1)
      {
        iIndex = this.parseString.indexOf("<VALUE>", iIndex);
        String strValue = this.copyString.substring(iIndex + 7, this.parseString.indexOf("</VALUE>", iIndex));
        return strValue;
      }
    }
    catch (StringIndexOutOfBoundsException e)
    {
      e.printStackTrace();
    }
    return null;
  }
}