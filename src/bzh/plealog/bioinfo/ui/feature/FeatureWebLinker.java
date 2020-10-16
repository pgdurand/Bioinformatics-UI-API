/* Copyright (C) 2006-2016 Patrick G. Durand
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/agpl-3.0.txt
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 */
package bzh.plealog.bioinfo.ui.feature;

import java.io.FileReader;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.StringTokenizer;

import com.plealog.genericapp.api.log.EZLogger;

import bzh.plealog.bioinfo.api.data.feature.AnnotationDataModelConstants;
import bzh.plealog.bioinfo.ui.resources.SVMessages;
import bzh.plealog.bioinfo.util.CoreUtil;

/**
 * This class handles web links that can be used by the FeatureViewer to allow
 * starting a web browser. This can be used to display external links displayed
 * within a feature table. <br>
 * <br>
 * 
 * Actually, this class is used to handle file "featureWebLink.config" located
 * in the "conf" directory of this project.
 * 
 * @author Patrick G. Durand
 */
public class FeatureWebLinker {
  private Properties _properties = new Properties();
  private HashSet<String> _linkTags;

  private static final String LINK_TAG_KEY = "link.tags";
  private static final String ID_TAG = "@ID@";

  /**
   * Constructor.
   * 
   * Auto-load featureWebLink.conf resource located in the resources package.
   */
  public FeatureWebLinker() {
    this(SVMessages.class.getResourceAsStream("featureWebLink.conf"));
  }

  /**
   * Constructor.
   * 
   * @param is
   *          a Stream enabling to read a featureWebLink.conf resource.
   */
  public FeatureWebLinker(InputStream is) {
    try {
      Properties props = new Properties();
      props.load(is);
      initProperties(props);
    } catch (Exception ex) {
      EZLogger.warn("Unable to read FeatureWebLinker IO stream: "+ex);
      return;
    }
  }

  /**
   * Constructor.
   * 
   * @param confpath a path targeting a featureWebLink.conf resource.
   */
  public FeatureWebLinker(String confpath){
    try (FileReader fr = new FileReader(confpath)){
      Properties props = new Properties();
      props.load(fr);
      initProperties(props);
    } catch (Exception ex) {
      EZLogger.warn("Unable to read FeatureWebLinker: "+confpath+": "+ex);
      return;
    }
  }
  private void initProperties(Properties props) {
    String tags, key, value;
    String[] tagsList;
    Enumeration<?> e;

    e = props.keys();
    while (e.hasMoreElements()) {
      key = (String) e.nextElement();
      value = props.getProperty(key).trim();
      if (key.equals(LINK_TAG_KEY)) {
        _properties.setProperty(key, value);
        continue;
      }
      _properties.setProperty(key.toLowerCase(), value);
    }

    _linkTags = new HashSet<String>();
    tags = _properties.getProperty(LINK_TAG_KEY);
    if (tags == null)
      return;
    tagsList = CoreUtil.tokenize(tags);
    for (String tag : tagsList) {
      _linkTags.add(tag);
    }
  }

  /**
   * Figures out if a qualifier name corresponds to a database link. Parameter
   * qualName is compared with the list of link tags reported in the
   * configuration file associated to this FeatureWebLinker.
   */
  public boolean isLinkTag(String qualName) {
    if (_linkTags == null)
      return false;
    else
      return _linkTags.contains(qualName);
  }

  public String[] getData(String qualName, String qualValue) {
    StringTokenizer tokenizer;
    String dbCode, dbId;
    int idx;

    // when qualName corresponds to a db xref, we can have three solutions
    // to retrieve the db ID:
    // either qualValue is formatted as <dbCode>:<dbId>
    // or it is formated as <dbCode>;<dbId>;
    // or qualValue is directly a dbID
    idx = qualValue.indexOf(';');
    if (idx != -1) {
      tokenizer = new StringTokenizer(qualValue, ";");
      if (tokenizer.countTokens() >= 2) {
        dbCode = tokenizer.nextToken().trim();
        dbId = tokenizer.nextToken().trim();
      } else {
        dbCode = qualName;
        dbId = qualValue;
      }
    } else if ((idx = qualValue.indexOf(':')) != -1) {
      dbCode = qualValue.substring(0, idx);
      dbId = qualValue.substring(idx + 1);
    } else {
      dbCode = qualName;
      dbId = qualValue;
    }
    return new String[] { dbCode, dbId };
  }

  public String getURLFromQualifier(String qualName, String qualValue) {
    String[] str;
    String qName;
    
    if (qualName.isEmpty()) {
      qName = AnnotationDataModelConstants.FEATURE_QUALIFIER_XREF;
    }
    else {
      qName = qualName;
    }
    if (isLinkTag(qName)) {
      str = getData(qName, qualValue);
      return getURL(str[0], str[1]);
    } else {
      return (null);
    }
  }

  /**
   * Given qualifier name and value, figures out if there is a possible link to
   * a remote database source.
   */
  public boolean isLinkable(String qualName, String qualValue) {
    return (getURLFromQualifier(qualName, qualValue) != null);
  }

  /**
   * Returns a full URL given dbCode and id. Parameter dbCode is used to query
   * the list of database tags reported in the configuration file associated to
   * this FeatureWebLinker. If that tag exists, the method gets a URL where it
   * replaces @ID@ by parameter id. If dbCode does not exist in the
   * configuration file, then the method returns null.
   */
  public String getURL(String dbCode, String id) {
    String url;

    if (dbCode == null || id == null)
      return null;
    url = _properties.getProperty(dbCode.toLowerCase());
    if (url == null)
      return null;
    return url.replace(ID_TAG, id);
  }
}
