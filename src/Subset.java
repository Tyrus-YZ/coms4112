import java.util.*;

// Class to store all attributes of a subset as well as some functions to do basic calculations about the subset.
public class Subset {
	Map<Character, Integer> configMap;
	Map<Integer, Float> probMap;
	double p;
	double CMetric_term1;
	double cost;
	double fcost;
	int num;
	boolean noBranchBit; // bit to record if the optimal plan for this &-term is no-branching or logical-and
	String layout;
	private List<Subset> subSetList; // record all the &-term that constitues the current optimal plan for this subset.
	Subset left;
	Subset right;
	Subset(String s, Map<Integer, Float> probMap, Map<Character, Integer> configMap){
		this.num = s.length();
		this.subSetList = new LinkedList<Subset>();
		subSetList.add(this);
		this.layout = s;
		this.left = this;
		this.right = this;
		this.configMap = configMap;
		this.probMap = probMap;
		this.p = calcuP();
		this.fcost = calcuFcost();
		this.CMetric_term1 = calcuCMetric();
		if(logicalAnd() > noBranch()) {
			this.cost = noBranch();
			this.noBranchBit = true;
		}
		else {
			this.cost = logicalAnd();
			this.noBranchBit = false;
		}
	}
	// Calculate the Fcost of this term.
	private double calcuFcost() {
		return num * configMap.get('r') + (num-1) * configMap.get('l') + num * configMap.get('f') + configMap.get('t');
	}
	// Calculate (p-1)/fcost(E) term of CMetric
	private double calcuCMetric() {
		return (p-1)/fcost;
	}
	// Calculate the cost of applying no-branching for this subset.
	private double noBranch() {
		return num * configMap.get('r') + (num-1) * configMap.get('l') + num * configMap.get('f') + configMap.get('a');
	}
	// Calculate the cost of applying logical-and for this subset.
	private double logicalAnd() {
		double q = p<=0.5?p:1-p;
		return num * configMap.get('r') + (num-1) * configMap.get('l') + num * configMap.get('f') + configMap.get('t') + q * configMap.get('m') + p * configMap.get('a');
	}
	// Get all the &-terms that constitute the current best plan for this subset. (Used to check for Lemma 4.9)
	public List<Subset> getElements() {
		return subSetList;
	}
	
	public void setElements(List<Subset> lst) {
		this.subSetList = lst;
	}
	// Get leftmost &-term. (Used to check Lemma 4.8)
	public Subset getLeftMostElement() {
		Subset res = this;
		while(res.left != res) {
			res = res.left;
		}
		return res;
	}
	// Get rightmost &-term. (Used when print optimal plan)
	public Subset getRightMostElement() {
		Subset res = this;
		while(res.right != res) {
			res = res.right;
		}
		return res;
	}
	// Calculate combined selectivity.
	private double calcuP() {
		double p = 1;
		for(char c: layout.toCharArray()) {
			p *= probMap.get(Character.getNumericValue(c));
		}
		return p;
	}
	
	public String getElementString() {
		StringBuilder sb = new StringBuilder();
		for(char c: this.layout.toCharArray()) {
			sb.append("t" + c + "[o" + c + "[i]] & ");
		}
		String str = sb.toString().substring(0, sb.length() - 3);
		return str.contains("&")?"(" + str + ")":str;
	}
	// recursively construct the String form of the current plan.(Mixed with &-term and &&).
	public String toString() {
		if(left == this && right == this) {
			return getElementString();
		}
		else {
			if(right.right == right && right.noBranchBit == true) {
				return "(" + left.toString() + ")";
			}
			else {
				return "(" + left.toString() + " && " + right.toString() + ")";
			}
		}
	}
}
