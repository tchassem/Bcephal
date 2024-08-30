/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * @author Joseph Wambo
 * @param <P>
 *
 */
public class CombinaisonUtil<P> {
	
	public List<Set<Integer>> combinaison(int max){
		Set<Integer> set = new HashSet<>(0);
		for(int i = 0; i < max; i++) {
			set.add(i);
		}
				
		CombinaisonUtil<Integer> util = new CombinaisonUtil<>();		
		List<Set<Integer>> combinations = util.combinaison(set);		
		return combinations;
	}
		
	public List<Set<P>> combinaison(Set<P> set){
		List<Set<P>> combinations = new ArrayList<>(0);
		for(int cardinality = 1; cardinality <= set.size(); cardinality++) {
			Set<Set<P>> combinaison = combinaison(set, cardinality);
			combinations.addAll(combinaison);
		}
		return combinations;
	}
	
	public Set<Set<P>> combinaison(Set<P> set, int cardinality){
		Set<Set<P>> combinations = Sets.combinations(set, cardinality);		
		return combinations;
	}
	
	public double combinaisonCount(int n){
		double count = 0;
		for(int i = n; i > 0; i--){
			count = count + combinaisonCount(n, i);
		}		
		return count;
	}
	
	public double combinaisonCount(long n, long k){
		double fatoN = factorial(n);
		double factoK = factorial(k);
		double foctoNK = factorial(n-k);
		double count = fatoN/(factoK * foctoNK);		
		return count;
	}
	
	public double factorial(long n){
		double facto = 1;
		for(long i = n; i > 0; i--){
			facto = facto * i;
		}
		return facto;
	}
	
}
