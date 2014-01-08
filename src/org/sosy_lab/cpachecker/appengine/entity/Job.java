/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2013  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.cpachecker.appengine.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.sosy_lab.cpachecker.appengine.dao.JobFileDAO;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.EmbedMap;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.OnSave;

@Entity
public class Job {

  public enum Status {
    PENDING, RUNNING, DONE, ABORTED, TIMEOUT
  }

  @Id private Long id;
  private Date creationDate;
  private Date executionDate;
  private Date terminationDate;
  private Status status;
  private String specification;
  private String configuration;
  private String queueName;
  private String taskName;
  @EmbedMap private Map<String, String> options;
  private List<Ref<JobFile>> files = new CopyOnWriteArrayList<>();

  // FIXME remove this stuff. It's only here for debugging just now.
  private String log;
  public void setLog(String pLog) {
    log = pLog;
  }
  public String getLog() {
    return log;
  }

  public Job() {
    init();
  }

  public Job(Long id) {
    init();
    this.id = id;
  }

  private void init() {
    status = Status.PENDING;
    creationDate = new Date();
  }

  public String getKey() {
    return Key.create(Job.class, getId()).getString();
  }


  public Date getExecutionDate() {
    return executionDate;
  }


  public void setExecutionDate(Date pExecutionDate) {
    executionDate = pExecutionDate;
  }


  public Date getTerminationDate() {
    return terminationDate;
  }


  public void setTerminationDate(Date pTerminationDate) {
    terminationDate = pTerminationDate;
  }


  public Status getStatus() {
    return status;
  }


  public void setStatus(Status pStatus) {
    status = pStatus;
  }


  public Map<String, String> getOptions() {
    return options;
  }


  public void setOptions(Map<String, String> pOptions) {
    options = pOptions;
  }

  /**
   * Since dots (.) must not be part of a key they are escaped upon saving.
   */
  @OnSave void escapeOptionKeys() {
    Map<String, String> escapedMap = new HashMap<>();
    for (String key : options.keySet()) {
      escapedMap.put(key.replace(".", "\\"), options.get(key));
    }
    setOptions(escapedMap);
  }

  /**
   * Since dots (.) must not be part of a key they were escaped upon saving
   * and therefore need to be unescaped after loading.
   */
  @OnLoad void unescapeOptionKeys() {
    Map<String, String> unescapedMap = new HashMap<>();
    for (String key : options.keySet()) {
      unescapedMap.put(key.replace("\\", "."), options.get(key));
    }
    setOptions(unescapedMap);
  }

  public String getSpecification() {
    return specification;
  }


  public void setSpecification(String pSpecification) {
    specification = pSpecification;
  }



  public String getConfiguration() {
    return configuration;
  }


  public void setConfiguration(String pConfiguration) {
    configuration = pConfiguration;
  }

  public Long getId() {
    return id;
  }


  public void setId(Long pId) {
    id = pId;
  }


  public Date getCreationDate() {
    return creationDate;
  }

  public List<Ref<JobFile>> getFiles() {
    return files;
  }

  /**
   * Returns the file with an ID of the given path.
   *
   * @param path The file's path.
   * @return The file or null if the file cannot be found
   */
  public JobFile getFile(String path) {
    return JobFileDAO.loadByPath(path, this);
  }

  public void addFile(JobFile file) {
    files.add(Ref.create(file));
  }

  public void removeFile(JobFile file) {
    files.remove(Ref.create(file));
  }

  public String getQueueName() {
    return queueName;
  }

  public void setQueueName(String pQueueName) {
    queueName = pQueueName;
  }

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String pTaskName) {
    taskName = pTaskName;
  }
}
