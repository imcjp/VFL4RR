package alg;
/**
 * The code is for paper <b>"Efficient Vertical Federated Learning Method for Ridge Regression of Large-Scale Samples via Least-Squares Solution"</b>. <br/>
 * This class is a JavaBean, which is used to store some common information in the process of the algorithms running.
 */
public class AlgInfo {
	private static int __TestId=0;
	private String expName;
	private double lambda;
	private double learningRate;
	private double momentum;
	private int iter;
	private int testId;
	private int[] dim;
	private double beta1;
	private double beta2;
	private double epsilon;
	private double initBias;
	
	public AlgInfo() {
		this.testId=__TestId++;
	}
	public String getExpName() {
		return expName;
	}
	public void setExpName(String expName) {
		this.expName = expName;
	}
	public double getLambda() {
		return lambda;
	}
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
	public double getLearningRate() {
		return learningRate;
	}
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}
	public double getMomentum() {
		return momentum;
	}
	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}
	public String getParty(int i) {
		return getPartyGroup()+i;
	}
	public String getPartyGroup() {
		return expName+".participant";
	}
	public String getCoordinator() {
		return expName+".coordinator";
	}
	public int getIter() {
		return iter;
	}
	public void setIter(int iter) {
		this.iter = iter;
	}
	public int getTestId() {
		return testId;
	}
	public int[] getDim() {
		return dim;
	}
	public void setDim(int[] dim) {
		this.dim = dim;
	}
	public int getMxPid() {
		return this.dim.length;
	}
	public double getBeta1() {
		return beta1;
	}
	public void setBeta1(double beta1) {
		this.beta1 = beta1;
	}
	public double getBeta2() {
		return beta2;
	}
	public void setBeta2(double beta2) {
		this.beta2 = beta2;
	}
	public double getEpsilon() {
		return epsilon;
	}
	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}
	public double getInitBias() {
		return initBias;
	}
	public void setInitBias(double initBias) {
		this.initBias = initBias;
	}
}
