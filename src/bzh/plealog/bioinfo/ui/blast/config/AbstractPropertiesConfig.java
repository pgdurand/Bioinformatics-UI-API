/* Copyright (C) 2003-2016 Patrick G. Durand
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
package bzh.plealog.bioinfo.ui.blast.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import com.plealog.genericapp.api.log.EZLogger;

/**
 * Handle application configuration using some properties. This class actually
 * wraps a PropertiesConfiguration, so your data has to follow the specification
 * of that library. See the Apache Commons Configuration documentation.
 * 
 * @author Patrick G. Durand
 */
public class AbstractPropertiesConfig {
  private String                    _name = "config";
  protected String                  _path;
  protected PropertiesConfiguration _pConfig;

  /**
   * Default constructor.
   */
  public AbstractPropertiesConfig() {
    try {
      _pConfig = new PropertiesConfiguration();
      _pConfig.setDelimiterParsingDisabled(true);
    } catch (Exception e) {
      EZLogger.warn(e.toString());
    }
  }

  /**
   * Sets the name of this configuration.
   * 
   * @param name
   *          name
   */
  public void setName(String name) {
    _name = name;
  }

  /**
   * Returns the name of this configuration.
   * 
   * @return a name
   */
  public String getName() {
    return _name;
  }

  /**
   * Uploads a configuration. This method delegates the load to the load method
   * of class ConfigurationProperties.
   * 
   * @param inStream
   *          input stream to read properties
   * 
   */
  public void load(InputStream inStream) throws IOException {
    try {
      _pConfig.load(inStream);
      cleanValues();
    } catch (Exception e) {
      throw new IOException(e.getMessage());
    }
  }

  /**
   * Uploads a configuration file. This method delegates the load to the load
   * method of class ConfigurationProperties. So, a configuration file has to be
   * formatted according the ConfigurationProperties specifications.
   * 
   * @param path
   *          the absolute path to the file
   * @param listenReload
   *          set to true to monitor and automatically reload the file when it
   *          is modified on disk.
   */
  public void load(String path, boolean listenReload) throws IOException {
    try {
      _pConfig = new PropertiesConfiguration(path);
      _path = path;
      cleanValues();
    } catch (Exception e) {
      // this has been done for backward compatibility when replacing
      // standard Properties by ConfigurationProperties
      throw new IOException(e.getMessage());
    }

    if (listenReload) {
      _pConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
    }
  }

  /**
   * Returns the value corresponding to a particular key.
   * 
   * @return value or null if key is not found
   */
  public String getProperty(String key) {
    return _pConfig.getString(key);
  }

  /**
   * Sets a property.
   * 
   * @param key
   *          a key
   * @param value
   *          a value
   */
  public void setProperty(String key, String value) {
    _pConfig.setProperty(key, value);
  }

  /**
   * Returns an enumeration over the property names.
   * 
   * @return an enumeration over the property names.
   */
  public Enumeration<String> propertyNames() {
    return enumerator(_pConfig.getKeys());
  }

  /**
   * Dump the configuration using EZLogger.
   * 
   * @see com.plealog.genericapp.api.log.EZLogger
   */
  public void dumpConfig() {
    Iterator<String> iter = _pConfig.getKeys();
    String key;

    while (iter.hasNext()) {
      key = iter.next().toString();
      EZLogger.info(key + " = " + _pConfig.getString(key));
    }
  }

  /**
   * Save the configuration.
   * 
   * @param confPath
   *          absolute path to file used to save configuration
   * 
   * @param listenReload
   *          figures out whether or not this object listen to reload of
   *          configuration file.
   * 
   * @return true is success, false otherwise.
   */
  public boolean save(String confPath, boolean listenReload) {
    boolean bRet = true;

    _path = confPath;
    _pConfig.setFileName(confPath);
    if (listenReload)
      _pConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
    try {
      _pConfig.save();
    } catch (ConfigurationException e) {
      EZLogger.warn("unable to save configuration in " + _path + ": " + e);
      bRet = false;
    }
    return bRet;
  }

  /**
   * Reset the content of this configuration.
   */
  protected void cleanValues() {
    Enumeration<Object> menum;
    Iterator<String> iter;
    Properties props;
    String key, value;

    iter = _pConfig.getKeys();
    props = new Properties();
    // note _pConfig cannot be modified while it is read.
    // Step 1 : store updated values in a Properties
    while (iter.hasNext()) {
      key = (String) iter.next();
      value = _pConfig.getString(key).trim();
      props.setProperty(key, value);
    }

    // Step 2: store updated values in _pConfig
    menum = props.keys();
    while (menum.hasMoreElements()) {
      key = (String) menum.nextElement();
      _pConfig.setProperty(key, props.getProperty(key));
    }
  }

  private Enumeration<String> enumerator(final Iterator<String> iter) {
    return new Enumeration<String>() {
      public boolean hasMoreElements() {
        return iter.hasNext();
      }

      public String nextElement() {
        return iter.next();
      }
    };
  }

}
