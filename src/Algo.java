import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Algo {
	public static void main(String[] args) {
		// use a hashmap to store configuration
		Map<Character, Integer> configMap = new HashMap<>();
		List<String> queryList = new LinkedList<>();
		// read configuration
		try {
			FileReader config_reader = new FileReader(args[1]);
			configMap = ini_config(config_reader);
		} catch (Exception e) {
			System.out.println(args[0]);
			System.out.println("Fail to read configuration file!!");
			return;
		}
		// read queries
		try {
			FileReader query_reader = new FileReader(args[0]);
			queryList = read_query(query_reader);
		} catch (Exception e) {
			System.out.println("Fail to read query file!!");
			return;
		}
		System.out.println("===============================");
		for(String query: queryList) {
			// for each query, calculate and print the best plan
			calcu_plan(query, configMap);
		}
	}
	
	// The function that calculate and output the best plan for a query. (Basically implementing Algorithm 4.11)
	public static void calcu_plan(String query, Map<Character, Integer> configMap) {
		String s = query;	
		// record the selectivity of each basic term
		Map<Integer, Float> probMap = new HashMap<>();
		String[] probs = s.split(" ");
		for(int i = 0; i < probs.length; i++) {
			probMap.put(i+1, Float.parseFloat(probs[i]));
		}
		// Genearte all 2_(k-1) plans of subset using only &-terms and store them in subSetList in increasing order.
		// The terms in subSetList are in increasing order.
		List<String> subSetList = gener_candi(probs.length);
		Subset[] subSetArray = new Subset[subSetList.size()];
		Map<String, Subset> subSetMap = new HashMap<>();
		// Store all the plans of subsets for later use.
		for(int i = 0; i < subSetList.size(); i++) {
			Subset current_subset = new Subset(subSetList.get(i), probMap, configMap);
			subSetArray[i] = current_subset;
			subSetMap.put(subSetList.get(i), current_subset);
		}
		
		for(int i = 0; i < subSetArray.length; i++) {
			for(int j = 0; j < subSetArray.length; j++) {
				Subset s_left = subSetArray[j]; // pick one as left child
				Subset s_right = subSetArray[i];  // pick one as right child
				if(intersect(s_left.layout, s_right.layout)) {  // skip if there are intersections
					continue;
				}
				//  Lemma 4.8  skip the suboptimal plans
				else if(s_left.p >= s_right.getLeftMostElement().p && s_left.CMetric_term1 > s_right.getLeftMostElement().CMetric_term1) {
					continue;
				}
				else {
					// Lemma 4.9  skip the suboptimal plans
					if(s_left.p <= 0.5) 
					{
						List<Subset> rightElementList = s_right.getElements();
						for(Subset set: rightElementList) {
							if(s_left.p > set.p && s_left.fcost > set.fcost) {
								continue;
							}
						}
					}
					// Calculate the cost of Branching-And and compare with the original cost.
					double q = s_left.p > 0.5?1-s_left.p:s_left.p;
					double cost = s_left.fcost + configMap.get('m') * q + s_right.cost* s_left.p;
					char[] combine = (s_left.layout + s_right.layout).toCharArray();
					// Use subSetMap to locate the original cost of this combination term to check if we need to update it or not.
					Arrays.sort(combine);
					String combinedName = String.valueOf(combine);
					Subset current_subset = subSetMap.get(combinedName);
					double prev_cost = current_subset.cost;
					if(cost < prev_cost) {
						// Once new cost < previous cost, do three things:
						// 1. update the cost of the combination term
						// 2. update the left and right child of the combination term
						// 3. update the element list of the combination term by adding up the element list of left child and right child
						current_subset.left = s_left;
						current_subset.right = s_right;
						current_subset.cost = cost;
						List<Subset> temp = new LinkedList<Subset>();
						temp.addAll(s_left.getElements()); temp.addAll(s_right.getElements());
						current_subset.setElements(temp);
					}
				}
			}
		}
		Subset res = subSetArray[subSetArray.length-1];
		System.out.println(query);
		System.out.println("-------------------------------");
		System.out.println(resultPrint(res));
		System.out.println("-------------------------------");
		System.out.println("cost: " +res.cost);
		System.out.println("===============================");
	}
	
	
	
	// Function to print the optimal plan
	public static String resultPrint(Subset s) {
		StringBuilder sb = new StringBuilder();
		// If the result is a single &-term
		if(s.left == s && s.right == s) {
			// If the result is a single term with no-branch
			if(s.noBranchBit == true) {
				sb.append("answer[j] = i;\n").append("j += ").append(s).append(";");
			}
			// If the result si a single term with Logical-and
			else {
				sb.append("if").append(s.toString()).append(" {\n\tanswer[j++] = i;\n}");
			}
		}
		// If the result is a mixed term of & and &&
		else {
			sb.append("if").append(s.toString()).append("{\n");
			// No-brach can only be applied to the right-most &-term
			if(s.getRightMostElement().noBranchBit == true) {
				sb.append("\tanswer[j] = i;\n").append("\tj += ").append(s.getRightMostElement().toString()).append(";\n");
			}
			else {
				sb.append("\tanswer[j++] = i;\n");
			}
			sb.append("}");
		}
		return sb.toString();
	}
	
	// Judge if two terms have intersect basic term
	public static boolean intersect(String s1, String s2) {
		for(char c1: s1.toCharArray()) {
			for(char c2: s2.toCharArray()) {
				if(c1 == c2) {
					return true;
				}
			}
		}
		return false;
	}
	
	// Use a dfs way to generate 2_(k-1) subsets from length 1 to maximum length, thus maintaining the increasing order.
	public static List<String> gener_candi(int num){
		List<String> res = new LinkedList<String>();
		for(int len = 0; len < num; len ++) {
			for(int start = 1; start <= num; start ++) {
				res = dfs(start, num, len, res, String.valueOf(start));
			}
		}
		return res;
	}
	
	public static List<String> dfs(int start, int end, int len, List<String> res, String temp){
		if(len == 0) {
			res.add(temp);
			return res;
		}
		else if(start + len > end) {
			return res;
		}
		else {
			for(int new_start = start + 1; new_start <= end; new_start ++) {
				temp += String.valueOf(new_start);
				res = dfs(new_start, end, len-1, res, temp);
				temp = temp.substring(0, temp.length()-1);
			}
			return res;
		}
	}
	// read queries
	public static List<String> read_query(FileReader f) throws IOException{
		BufferedReader br = new BufferedReader(f);
		List<String> queryList = new LinkedList<String>();
		for(String line; (line = br.readLine()) != null; ) {
			queryList.add(line);
	    }
		return queryList;
	}
	// read configuration file
	public static Map<Character, Integer> ini_config(FileReader f) throws IOException{
		BufferedReader br = new BufferedReader(f);
		Map<Character, Integer> configMap = new HashMap<>();
		for(String line; (line = br.readLine()) != null; ) {
			configMap.put(line.split("=")[0].trim().charAt(0), Integer.parseInt(line.split("=")[1].trim()));
		}
		return configMap;
	}
}
