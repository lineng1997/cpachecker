/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.conditions.path;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.IntegerOption;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdgeType;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.CPAcheckerResult.Result;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Statistics;
import org.sosy_lab.cpachecker.core.interfaces.conditions.AvoidanceReportingState;
import org.sosy_lab.cpachecker.core.reachedset.ReachedSet;
import org.sosy_lab.cpachecker.cpa.value.ValueAnalysisState;
import org.sosy_lab.cpachecker.cpa.value.type.Value;
import org.sosy_lab.cpachecker.util.assumptions.PreventingHeuristic;
import org.sosy_lab.cpachecker.util.predicates.interfaces.BooleanFormula;
import org.sosy_lab.cpachecker.util.predicates.interfaces.view.FormulaManagerView;
import org.sosy_lab.cpachecker.util.states.MemoryLocation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/**
 * A {@link PathCondition} where the condition is based on the number of assignments (per identifier)
 * seen so far on the current path.
 */
@Options(prefix="cpa.conditions.path.assignments")
public class AssignmentsInPathCondition implements PathCondition, Statistics {
  /**
   * constant to signal that thresholds are disabled
   */
  private static final int DISABLED = -1;

  @Option(secure=true, description="sets the soft threshold for assignments (-1 for infinite). The semantics are that"
      + " variables are tracked up to this threshold, even if not being contained in the precison yet, and are removed"
      + " once there are more assignments for a variable then defined by this threshold. Once the variable is found to"
      + " be relevant, e.g., through refinement and interpolation, the variable is tracked again, until reaching the "
      + " hard threshold.")
  @IntegerOption(min=-1)
  private int softThreshold = DISABLED;

  @Option(secure=true, description="sets the hard threshold for assignments (-1 for infinite). A variable reaching this"
      + " assignment threshold is not tracked anymore, even if it is contained in the precision.")
  @IntegerOption(min=-1)
  private int hardThreshold = DISABLED;

  @Option(secure=true, description="determines if there should be a single assignment state per transition,"
      + "one after each assume edge, or a global one.")
  private String precision = "GLOBAL";

  @Option(secure=true, description="whether or not to each variable should have its own threshold.")
  private boolean individualThreshold = false;

  /**
   * the maximal number of assignments over all variables for all elements seen to far
   */
  private int maxNumberOfAssignments = 0;

  /**
   * the map handling which variable has which (individual) threshold
   */
  private Map<MemoryLocation, Integer> thresholds = new HashMap<>();

  /**
   * the data structure that keeps track how often a threshold was increased for a variable
   */
  private Multiset<MemoryLocation> incrementCounter = HashMultiset.create();

  public Map<MemoryLocation, Integer> getThres() {
    return thresholds;
  }

  public AssignmentsInPathCondition(Configuration config, LogManager logger) throws InvalidConfigurationException {
    config.inject(this);

    if(softThreshold == -1) {
      logger.log(Level.INFO, AssignmentsInPathCondition.class.getSimpleName() + " in use"
          + " with softThreshold set to infinite (-1), so only state information will be collected,"
          + " while no thresholds will be enforced.");
    }
  }

  @Override
  public AvoidanceReportingState getInitialState(CFANode node) {
    return new UniqueAssignmentsInPathConditionState();
  }

  @Override
  public AvoidanceReportingState getAbstractSuccessor(AbstractState pElement, CFAEdge pEdge) {

    UniqueAssignmentsInPathConditionState current = (UniqueAssignmentsInPathConditionState)pElement;

    maxNumberOfAssignments = Math.max(maxNumberOfAssignments, current.getMaximum());

    if (precision.equals("STATE")
        || (precision.equals("PATH") && pEdge.getEdgeType() == CFAEdgeType.AssumeEdge)) {
      return new UniqueAssignmentsInPathConditionState(0, HashMultimap.create(current.mapping));
    }

    else {
      return current;
    }
  }

  @Override
  public boolean adjustPrecision() {
    softThreshold *= 2;
    hardThreshold *= 2;
    return true;
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }

  @Override
  public void printStatistics(PrintStream out, Result result, ReachedSet reachedSet) {
    out.println("Threshold value (soft):     " + softThreshold);
    out.println("Threshold value (hard):     " + hardThreshold);
    out.println("Max. number of assignments: " + maxNumberOfAssignments);
  }

  public class UniqueAssignmentsInPathConditionState implements AbstractState, AvoidanceReportingState {

    /**
     * the mapping from variable name to the set of assigned values to this variable
     */
    private Multimap<MemoryLocation, Long> mapping = HashMultimap.create();

    /**
     * the maximal number of assignments over all variables
     */
    private int maximum;

    private UniqueAssignmentsInPathConditionState() {
      this(0, HashMultimap.<MemoryLocation, Long>create());
    }

    public UniqueAssignmentsInPathConditionState(int pMaximum, Multimap<MemoryLocation, Long> pMapping) {
      maximum = pMaximum;
      mapping = pMapping;
    }

    public void reset() {
      maximum = 0;
      mapping = HashMultimap.<MemoryLocation, Long>create();
    }

    /**
     * This method returns the maximal number of assignments over all variables.
     *
     * @return the maximal amount of assignments over all variables
     */
    public int getMaximum() {
      return maximum;
    }

    @Override
    public BooleanFormula getReasonFormula(FormulaManagerView formulaManager) {
      return PreventingHeuristic.ASSIGNMENTSINPATH.getFormula(formulaManager, maximum);
    }

    @Override
    public boolean mustDumpAssumptionForAvoidance() {
      return softThreshold != DISABLED && maximum > AssignmentsInPathCondition.this.softThreshold;
    }

    /**
    * This method decides if the number of assignments for the given variable would exceed the soft threshold, taking
    * into account the current assignment from the given explicit-value state.
    *
    * @param state the current assignment in form of a explicit-value state
    * @param memoryLocation the variable to check
    * @return true, if the number of assignments for the given variable would exceed the soft threshold, else false
    */
    public boolean wouldExceedSoftThreshold(ValueAnalysisState state, MemoryLocation memoryLocation) {
      if (softThreshold == DISABLED) {
        return false;
      }

      else if (softThreshold == 0) {
        return true;
      }

      int increment = mapping.containsEntry(memoryLocation, state.getValueFor(memoryLocation)) ? 0 : 1;
      return (mapping.get(memoryLocation).size() + increment) > softThreshold;
    }

    /**
    * This method decides if the number of assignments for the given memory location exceeds the hard threshold.
    *
    * @param memoryLocation the memory location to check
    * @return true, if the number of assignments for the given memory location exceeds the hard threshold, else false
    */
    public boolean exceedsHardThreshold(MemoryLocation memoryLocation) {

      if(thresholds.containsKey(memoryLocation)) {
        Integer individualThreshold = thresholds.get(memoryLocation);
        return individualThreshold != DISABLED && mapping.get(memoryLocation).size() > individualThreshold;
      }

      else {
        return hardThreshold != DISABLED && mapping.get(memoryLocation).size() > hardThreshold;
      }
    }

    /**
     * This method updates this state's unique assignment information with an assignment of the given memory location.
     *
     * @param memoryLocation the memory location for which to set assignment information
     */
    public void updateAssignmentInformation(MemoryLocation memoryLocation, Value value) {
      mapping.put(memoryLocation, value.asNumericValue().longValue());
      maximum = Math.max(maximum, mapping.get(memoryLocation).size());

      // lazy-initialization of the the individual thresholds
      if(!thresholds.containsKey(memoryLocation)) {
        thresholds.put(memoryLocation, hardThreshold);
      }
    }

    @Override
    public String toString() {
      return mapping.toString() + " [max: " + maximum + "]";
    }

    /**
     * This method return those memory locations that exceed the hard threshold
     *
     * @return the memory locations that exceed the hard threshold
     */
    public Set<MemoryLocation> getMemoryLocationsExceedingHardThreshold() {
      Set<MemoryLocation> exceedingMemoryLocations = new HashSet<>();
      for (MemoryLocation memoryLocation : mapping.keys()) {
        if(mapping.get(memoryLocation).size() > hardThreshold) {
          exceedingMemoryLocations.add(memoryLocation);
        }
      }

      return exceedingMemoryLocations;
    }

    public Set<MemoryLocation> getExceedingMemoryLocations() {
      Set<MemoryLocation> exceedingMemoryLocations = new HashSet<>();
      for (MemoryLocation memoryLocation : mapping.keys()) {
        if(exceedsHardThreshold(memoryLocation)) {
          exceedingMemoryLocations.add(memoryLocation);
        }
      }

      return exceedingMemoryLocations;
    }

    public void increaseThreshold(MemoryLocation pMemoryLocation) {
      thresholds.put(pMemoryLocation, thresholds.get(pMemoryLocation) * 2);
      //thresholds.put(pMemoryLocation, mapping.get(pMemoryLocation).size());
      //Syso("increasing threshold to " + thresholds.get(pMemoryLocation) + " for " + pMemoryLocation);
      incrementCounter.add(pMemoryLocation);

      if(incrementCounter.count(pMemoryLocation) >= 4) {
        thresholds.put(pMemoryLocation, DISABLED);
      }
    }

    public Map<MemoryLocation, Integer> getThresholds() {
      return thresholds;
    }
  }
}
