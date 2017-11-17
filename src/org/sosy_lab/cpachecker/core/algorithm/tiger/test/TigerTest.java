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
package org.sosy_lab.cpachecker.core.algorithm.tiger.test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sosy_lab.cpachecker.core.algorithm.AlgorithmResult;
import org.sosy_lab.cpachecker.core.algorithm.tiger.goals.Goal;
import org.sosy_lab.cpachecker.core.algorithm.tiger.test.CombinedVariableProperty.Combinators;
import org.sosy_lab.cpachecker.core.algorithm.tiger.test.VariableProperty.Comparators;
import org.sosy_lab.cpachecker.core.algorithm.tiger.test.VariableProperty.GoalPropertyType;
import org.sosy_lab.cpachecker.core.algorithm.tiger.util.TestCase;
import org.sosy_lab.cpachecker.core.algorithm.tiger.util.TestSuite;
import org.sosy_lab.cpachecker.util.test.CPATestRunner;
import org.sosy_lab.cpachecker.util.test.TestResults;

public class TigerTest {

  private static final String EXAMPLE_C = "test/programs/tiger/products/example_v0.c";

  private static final String EXAMPLE_V1_C = "test/programs/tiger/products/example_v1.c";

  private static final String EMAIL_SIMULATOR_C = "test/programs/tiger/simulator/email_simulator.c";

  private static final String EXAMPLE_LOOP = "test/programs/tiger/products/example_loop.c";



  private static final String EXAMPLE_ONLY_INFEASIBLE =
      "test/programs/tiger/products/example_only_infeasible.c";

  private static final String EXAMPLE_IfCOMBINATIONS =
      "test/programs/tiger/products/example_ifCombinations.c";

  private static List<ExpectedGoalProperties> exampleGoalProperties;

  private static List<ExpectedGoalProperties> exampleGoalProperties_v1;

  private static List<ExpectedGoalProperties> exampleGoalProperties_loop;

  private static List<ExpectedGoalProperties> exampleGoalProperties_onlyInfeasible;

  private static List<ExpectedGoalProperties> exampleGoalProperties_ifCombinations;

  @BeforeClass
  public static void initializeExpectedGoalProperties() {

    //example.c
    exampleGoalProperties = Lists.newLinkedList();
    ExpectedGoalProperties g1 = new ExpectedGoalProperties("G1", true);
    g1.addVariableProperty(new RelativeVariableProperty("relevant: x", "relevant: y",
        Comparators.LT, GoalPropertyType.INPUT));
    List<String> addXandY = Lists.newLinkedList();
    addXandY.add("relevant: x");
    addXandY.add("relevant: y");
    g1.addVariableProperty(new CombinedRelativeVariableProperty("tmp", addXandY,
        Combinators.ADD, Comparators.EQ, GoalPropertyType.INPUTANDOUTPUT));
    exampleGoalProperties.add(g1);
    ExpectedGoalProperties g2 = new ExpectedGoalProperties("G2", true);
    g2.addVariableProperty(new RelativeVariableProperty("relevant: x", "relevant: y",
        Comparators.LT, GoalPropertyType.INPUT));
    g2.addVariableProperty(new CombinedRelativeVariableProperty("tmp", addXandY,
        Combinators.ADD, Comparators.EQ, GoalPropertyType.INPUTANDOUTPUT));
    exampleGoalProperties.add(g2);

    //example_v1.c
    exampleGoalProperties_v1 = Lists.newLinkedList();
    g1 = new ExpectedGoalProperties("G1", true);
    g1.addVariableProperty(new RelativeVariableProperty("relevant: x", "relevant: y",
        Comparators.LT, GoalPropertyType.INPUT));
    g1.addVariableProperty(new CombinedRelativeVariableProperty("tmp", addXandY,
        Combinators.ADD, Comparators.EQ, GoalPropertyType.INPUTANDOUTPUT));
    exampleGoalProperties_v1.add(g1);
    g2 = new ExpectedGoalProperties("G2", false);
    exampleGoalProperties_v1.add(g2);
    ExpectedGoalProperties g3 = new ExpectedGoalProperties("G3", true);
    g3.addVariableProperty(new RelativeVariableProperty("relevant: x", "relevant: y",
        Comparators.GT, GoalPropertyType.INPUT));
    g3.addVariableProperty(new RelativeVariableProperty("tmp", "relevant: y",
        Comparators.EQ, GoalPropertyType.INPUTANDOUTPUT));
    exampleGoalProperties_v1.add(g3);

    //example_loop.c
    exampleGoalProperties_loop = Lists.newLinkedList();
    g1 = new ExpectedGoalProperties("G1", true);
    g1.addVariableProperty(new RelativeVariableProperty("relevant: x", "relevant: y",
        Comparators.LT, GoalPropertyType.INPUT));
    exampleGoalProperties_loop.add(g1);

    //example_only_infeasible.c
    exampleGoalProperties_onlyInfeasible = Lists.newLinkedList();
    g1 = new ExpectedGoalProperties("G1", false);
    exampleGoalProperties_onlyInfeasible.add(g1);

    //example_ifCombinations.c
    exampleGoalProperties_ifCombinations = Lists.newLinkedList();
    g1 = new ExpectedGoalProperties("G1", true);
    g1.addVariableProperty(new RelativeVariableProperty("relevant: x", "relevant: y",
        Comparators.LT, GoalPropertyType.INPUT));
    g1.addVariableProperty(new RelativeVariableProperty("relevant: y", "relevant: z",
        Comparators.LT, GoalPropertyType.INPUT));
    addXandY = Lists.newLinkedList();
    addXandY.add("relevant: x");
    addXandY.add("relevant: y");
    g1.addVariableProperty(new CombinedRelativeVariableProperty("tmp", addXandY,
        Combinators.ADD, Comparators.LE, GoalPropertyType.INPUTANDOUTPUT));
    exampleGoalProperties_ifCombinations.add(g1);
    g2 = new ExpectedGoalProperties("G2", true);
    g2.addVariableProperty(new RelativeVariableProperty("relevant: x", "relevant: y",
        Comparators.LT, GoalPropertyType.INPUT));
    g2.addVariableProperty(new CombinedRelativeVariableProperty("tmp", addXandY,
        Combinators.ADD, Comparators.LE, GoalPropertyType.INPUTANDOUTPUT));
    List<String> addXandYandZ = Lists.newLinkedList();
    addXandYandZ.addAll(addXandY);
    addXandYandZ.add("relevant: z");
    g2.addVariableProperty(new CombinedRelativeVariableProperty("tmp", addXandYandZ,
        Combinators.ADD, Comparators.EQ, GoalPropertyType.INPUTANDOUTPUT));
    exampleGoalProperties_ifCombinations.add(g2);
  }

  @Test
  public void testPredicateExample() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.checkCoverage", "false");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")+(\"EDGES(ID)*\".(EDGES(@LABEL(G2))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_C);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 2);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 2);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 0);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties) {
      assertTrue(exp.checkProperties(testSuite));
    }
  }

  @Test
  public void testPredicateExampleWithReuse() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.checkCoverage", "true");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")+(\"EDGES(ID)*\".(EDGES(@LABEL(G2))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_C);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 1);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 2);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 0);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties) {
      assertTrue(exp.checkProperties(testSuite));
    }
  }

  @Test
  public void testPredicateExampleAllCoveredGoals() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.checkCoverage", "false");
    prop.put("tiger.allCoveredGoalsPerTestCase", "true");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")+(\"EDGES(ID)*\".(EDGES(@LABEL(G2))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_C);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 1);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 2);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 0);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties) {
      assertTrue(exp.checkProperties(testSuite));
    }
    List<String> goalsToBeCovered = Lists.newLinkedList();
    goalsToBeCovered.add("G1");
    goalsToBeCovered.add("G2");
    for(TestCase t : testSuite.getMapping().keySet()){
      List<Goal> coveredGoals = testSuite.getMapping().get(t);
      List<String> goalLabels = Lists.newLinkedList();
      for(Goal g : coveredGoals) {
        goalLabels.add(testSuite.getTestGoalLabel(g));
      }
      assertThat(goalsToBeCovered.size()==goalLabels.size());
      for(String goal : goalsToBeCovered) {
        assertThat(goalLabels.contains(goal));
      }
    }
  }

  @Test
  public void testPredicateExample_v1() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")+(\"EDGES(ID)*\".(EDGES(@LABEL(G2))).\"EDGES(ID)*\")+(\"EDGES(ID)*\".(EDGES(@LABEL(G3))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_V1_C);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 2);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 2);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 1);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties_v1) {
      assertTrue(exp.checkProperties(testSuite));
    }
  }

  @Test
  public void testPredicateEmailSimulatorForTimeout() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants.properties"));
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(feat_AutoResp))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EMAIL_SIMULATOR_C);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTimedoutTestGoals() > 0);
  }

  @Test
  public void testPredicateExample_loop() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_LOOP);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 1);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 1);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 0);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties_loop) {
      assertTrue(exp.checkProperties(testSuite));
    }
  }

  @Test
  public void testPredicateExample_only_infeasible() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_ONLY_INFEASIBLE);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 0);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 0);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 1);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties_onlyInfeasible) {
      assertTrue(exp.checkProperties(testSuite));
    }
  }

  @Test
  public void testPredicateExample_ifCombinations() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")+(\"EDGES(ID)*\".(EDGES(@LABEL(G2))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_IfCOMBINATIONS);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 2);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 2);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 0);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties_ifCombinations) {
      assertTrue(exp.checkProperties(testSuite));
    }
  }

  @Test
  public void testValueExample() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants-value.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")+(\"EDGES(ID)*\".(EDGES(@LABEL(G2))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_C);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 1);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 2);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 0);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties) {
      assertTrue(exp.checkProperties(testSuite));
    }
  }

  @Test
  public void testValueExample_v1() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants-value.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")+(\"EDGES(ID)*\".(EDGES(@LABEL(G2))).\"EDGES(ID)*\")+(\"EDGES(ID)*\".(EDGES(@LABEL(G3))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_V1_C);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 2);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 2);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 1);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties_v1) {
      assertTrue(exp.checkProperties(testSuite));
    }
  }

  @Test
  public void testValueExample_loop() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants-value.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_LOOP);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 1);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 1);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 0);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties_loop) {
      assertTrue(exp.checkProperties(testSuite));
    }
  }

  @Test
  public void testValueExample_only_infeasible() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants-value.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_ONLY_INFEASIBLE);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 0);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 0);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 1);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties_onlyInfeasible) {
      assertTrue(exp.checkProperties(testSuite));
    }
  }

  @Test
  public void testValueExample_ifCombinations() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants-value.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")+(\"EDGES(ID)*\".(EDGES(@LABEL(G2))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_IfCOMBINATIONS);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 2);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 2);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 0);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties_ifCombinations) {
      assertTrue(exp.checkProperties(testSuite));
    }
  }

  @Test
  public void testValueExampleWithReuse() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants-value.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.checkCoverage", "true");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")+(\"EDGES(ID)*\".(EDGES(@LABEL(G2))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_C);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 1);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 2);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 0);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties) {
      assertTrue(exp.checkProperties(testSuite));
    }
  }

  @Test
  public void testValueExampleAllCoveredGoals() throws Exception {
    Map<String, String> prop = TigerTestUtil.getConfigurationFromPropertiesFile(
        new File("config/tiger-variants-value.properties"));
    prop.put("tiger.inputInterface", "x,y,z");
    prop.put("tiger.outputInterface", "tmp");
    prop.put("tiger.checkCoverage", "false");
    prop.put("tiger.allCoveredGoalsPerTestCase", "true");
    prop.put("tiger.fqlQuery",
        "COVER (\"EDGES(ID)*\".(EDGES(@LABEL(G1))).\"EDGES(ID)*\")+(\"EDGES(ID)*\".(EDGES(@LABEL(G2))).\"EDGES(ID)*\")");

    TestResults results = CPATestRunner.run(prop, EXAMPLE_C);
    AlgorithmResult result = results.getCheckerResult().getAlgorithmResult();

    assertThat(result).isInstanceOf(TestSuite.class);
    TestSuite testSuite = (TestSuite) result;

    assertTrue(testSuite.getNumberOfTestCases() == 1);
    assertTrue(testSuite.getNumberOfFeasibleTestGoals() == 2);
    assertTrue(testSuite.getNumberOfInfeasibleTestGoals() == 0);
    assertTrue(testSuite.getNumberOfTimedoutTestGoals() == 0);
    for (ExpectedGoalProperties exp : exampleGoalProperties) {
      assertTrue(exp.checkProperties(testSuite));
    }
    List<String> goalsToBeCovered = Lists.newLinkedList();
    goalsToBeCovered.add("G1");
    goalsToBeCovered.add("G2");
    for(TestCase t : testSuite.getMapping().keySet()){
      List<Goal> coveredGoals = testSuite.getMapping().get(t);
      List<String> goalLabels = Lists.newLinkedList();
      for(Goal g : coveredGoals) {
        goalLabels.add(testSuite.getTestGoalLabel(g));
      }
      assertThat(goalsToBeCovered.size()==goalLabels.size());
      for(String goal : goalsToBeCovered) {
        assertThat(goalLabels.contains(goal));
      }
    }
  }



}