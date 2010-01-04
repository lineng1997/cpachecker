package cpa.concrete;

import cpa.common.defaults.FlatLatticeDomain;

public class ConcreteAnalysisDomain extends FlatLatticeDomain {
  private static ConcreteAnalysisDomain mInstance = new ConcreteAnalysisDomain();
  
  private ConcreteAnalysisDomain() {
    super(ConcreteAnalysisTopElement.getInstance(), ConcreteAnalysisBottomElement.getInstance());
  }

  @Override
  public ConcreteAnalysisTopElement getTopElement() {
    return ConcreteAnalysisTopElement.getInstance();
  }
  
  @Override
  public ConcreteAnalysisBottomElement getBottomElement() {
    return ConcreteAnalysisBottomElement.getInstance();
  }
  
  public static ConcreteAnalysisDomain getInstance() {
    return mInstance;
  }
}
