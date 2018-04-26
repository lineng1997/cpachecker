/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2017  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.arg;

import org.sosy_lab.cpachecker.core.interfaces.InferenceObject;

public class ARGInferenceObject implements InferenceObject {

  private static final long serialVersionUID = 721626977113066162L;

  private final InferenceObject wrappedObject;
  private final ARGState parent;

  public ARGInferenceObject(InferenceObject pWrappedObject, ARGState pParent) {
    wrappedObject = pWrappedObject;
    parent = pParent;
  }

  @Override
  public boolean hasEmptyAction() {
    return wrappedObject.hasEmptyAction();
  }

  public InferenceObject getWrappedObject() {
    return wrappedObject;
  }

  public ARGState getParent() {
    return parent;
  }
}
