///////////////////////////////////////////////////////////////////////////////
// Title:            hw3
// Files:            HW3.java, KMeansResult.java, KMeans.java
// Semester:         CS 540 Spring 2016
//
// Author:           Jale Dinler
// Email:            dinler@wisc.edu
// CS Login:         jale
// Lecturer's Name:  Collin Engstrom

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A k-means clustering algorithm implementation with Euclidean distance.
 * @author Jale Dinler
 *
 */
public class KMeans {
	/**
	 * @param centroids 2D array to store the centroids
	 * @param instances 2D array to store the instances
	 * @param threshold for stopping the iterations of the algorithm
	 * @return a KMeansResult object
	 */
	public KMeansResult cluster(double[][] centroids, double[][] instances, double threshold) {
		//stores a KMeansReasult object
		KMeansResult result = new KMeansResult();
		//stores a distortion list that keeps the calculated distortions for each iteration
		LinkedList<Double> distortions = new LinkedList<Double>();
		//create the cluster assignment array
		result.clusterAssignment = new int[instances.length];
		//stores the change in distortion between successive iterations
		double changeInDistortion = 0;

		// Loop until done
		do {
			//initializing instances
			for(int i=0; i < instances.length; i++)
			{
				int centroidIndex = -1;
				double minDist = Double.MAX_VALUE;
				//iterate through centroids
				for(int k = 0; k < centroids.length; k++)
				{
					double dist = 0;
					//iterate through instances and calculate the distance of instance i
					//to the centroid k
					for(int j = 0; j < instances[i].length; j++)
					{
						dist+=Math.pow(instances[i][j]-centroids[k][j], 2);
					}
					dist = Math.sqrt(dist);

					//if the distance between instance i and centroid k is smaller than 
					//minDist, change minDist to dist and assign instance i to centroid k
					if(dist < minDist)
					{
						minDist=dist;
						centroidIndex = k;
					}
					result.clusterAssignment[i] = centroidIndex;
				}
			}
			//done initializing

			//checking if there is any orphaned centroid
			boolean isOrphan = false;
			while(!isOrphan)
			{
				//initialize the index of orphaned centroid to -1
				int orphanIndex = -1;

				//stores a counter to be able to say if there is an orphaned centroid
				int count = 0;

				//iterate through centroids
				for(int k = 0; k < centroids.length; k++)
				{
					//iterate through cluster assignments
					for(int i = 0; i < result.clusterAssignment.length; i++)
					{
						//if an instance i is not assigned to the centroid k, increase the count by 1
						if(result.clusterAssignment[i] != k)
						{
							count++;
						}
					}
					//when we are done iterating through cluster assignments and if there is 
					//no instance that is assigned to the centroid k, then k is orphaned 
					if(count == result.clusterAssignment.length)
					{
						orphanIndex = k;
						break;
					}
				}
				//if there is an orphaned index, assign the proper instance to that centroid
				if(orphanIndex > -1)
				{
					//initialize the maximum distance to zero
					double maxDist = 0;

					//initialize the index of the instance with the max distance to its 
					//assigned centroid to -1
					int index = -1;

					//iterate through instances
					for(int i = 0; i < instances.length; i++)
					{ 
						double dist = 0;
						//find the distance of the instance i to its assigned centroid
						for(int j = 0; j < instances[0].length; j++)
						{
							dist += Math.pow(instances[i][j]-centroids[result.clusterAssignment[i]][j], 2);
						}
						dist = Math.sqrt(dist);

						//if that distance is greater than the maxDist, change maxDist to that distance
						if(dist > maxDist)
						{
							maxDist = dist;
							index = i;
						}
					}
					//assign the instance with maximum distance to the orphaned centroid
					result.clusterAssignment[index] = orphanIndex;
				}//done with assigning

				//do nothing if there is no orphaned centroid
				else
					isOrphan = true;
			}
			//done with checking

			//reallocating instances

			//iterate through centroids
			for(int k = 0; k < centroids.length; k++)
			{
				//initialize the total number of instances that are assigned to the centroid k
				int total = 0;

				//store the sum of each coordinate of the instances that are assigned to the centroid k
				double[] temp = new double[centroids[k].length];

				//initialize the sum of coordinates to zero
				for(int j = 0; j < temp.length; j++)
				{
					temp[j]=0;
				}

				//iterate throgh instances
				for(int i = 0; i < result.clusterAssignment.length; i++)
				{
					//if the instance i is assigned to the centroid k, increase the number of 
					//instances that are assigned to the centroid k by 1 and add the each
					//coordinate j to temp[j] 
					if(result.clusterAssignment[i] == k)
					{          
						for(int j = 0; j < centroids[k].length; j++)
						{
							temp[j]+=instances[i][j];
						}
						total++;
					}
				}
				//find the new centroids by dividing temp[j] by the total number of instances
				//that are assigned to the centroid k
				for(int j = 0; j < centroids[k].length; j++)
				{	
					centroids[k][j]=temp[j]/total;
				}
			}			
			//done with reallocating

			//calculating distortion for the current iteration and adding it to the distortion list
			double distortion = 0;
			//iterate through instances
			for(int i = 0; i < instances.length; i++)
			{
				//calculate the distortion of each instance and sum them all
				for(int j = 0; j < instances[0].length; j++)
				{
					distortion+=Math.pow(instances[i][j]-centroids[result.clusterAssignment[i]][j],2);
				}
			}
			//add this iterations distortions to the distortion list
			distortions.add(distortion);

			//calculating change in distortion between successive iterations if we iterated more than once
			//if we iterated once, simply taking that iteration's distortion
			if(distortions.size() == 1)
			{
				changeInDistortion = distortions.get(0);
			}

			else if(distortions.size() > 1)
			{
				changeInDistortion = Math.abs((distortions.get(distortions.size()-1)- distortions.get(distortions.size() - 2))/(distortions.get(distortions.size() - 2)));
			}

		}while(changeInDistortion >= threshold);// Iterations completed

		//assigning the results to KMeansResult object
		result.centroids=centroids;
		result.distortionIterations = new double[distortions.size()];
		for(int i = 0; i < distortions.size(); i++)
		{
			result.distortionIterations[i] = distortions.get(i);
		}//done with assigning

		return result;
	}
}
