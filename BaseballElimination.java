/* BaseballElimination.java
   CSC 226 - Summer 2018
   Assignment 4 - Baseball Elimination Program

   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java BaseballElimination

   To conveniently test the algorithm with a large input, create a text file
   containing one or more test divisions (in the format described below) and run
   the program with
	java -cp .;algs4.jar BaseballElimination file.txt (Windows)
   or
    java -cp .:algs4.jar BaseballElimination file.txt (Linux or Mac)
   where file.txt is replaced by the name of the text file.

   The input consists of an integer representing the number of teams in the division and then
   for each team, the team name (no whitespace), number of wins, number of losses, and a list
   of integers represnting the number of games remaining against each team (in order from the first
   team to the last). That is, the text file looks like:

	<number of teams in division>
	<team1_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	...
	<teamn_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>


   An input file can contain an unlimited number of divisions but all team names are unique, i.e.
   no team can be in more than one division.


   R. Little - 07/13/2018
*/

import edu.princeton.cs.algs4.*;
import java.util.*;
import java.io.File;

//Do not change the name of the BaseballElimination class
public class BaseballElimination{

	public class TeamFlowNetwork {
		public int wins;
		public int gamesLeft;
		public FlowNetwork FlowNet;
		public int[] W; // wins + gamesLeft - Wi
		public int N;
		public FlowEdge[] versusEdges;
		public String name;
		public boolean eliminated = false;
		public int index;
		public int[][] marked;

		public TeamFlowNetwork(int N, int i) {
			this.N = N;
			this.W = new int[N];
			this.FlowNet = new FlowNetwork(N*N + N+1);
			this.eliminated = false;
			this.index = i;
			this.marked = new int[N][N];
		}

		public boolean setWEdges(TeamFlowNetwork[] teams) {
			int W;
			for(int i = 0; i < this.N; i++) {
				if(i == this.index) continue;

				W = this.wins + this.gamesLeft - teams[i].wins;
				// System.out.printf("Team: %s: Wins: %d + gamesLeft: %d - %sWins(%d) = %d", this.name, this.wins, this.gamesLeft, teams[i].name, teams[i].wins, W);
				// System.out.println();
				if(W < 0) {
					this.eliminated = true;
					return true;
				}

				this.addEdge(new FlowEdge(i+1, this.V()-1, W));
			}
			return false;
		}

		public void addEdge(FlowEdge e) {
			FlowNet.addEdge(e);
		}

		public int V() {
			return FlowNet.V();
		}

	}

	// We use an ArrayList to keep track of the eliminated teams.
	public ArrayList<String> eliminated = new ArrayList<String>();
	private int N;
	TeamFlowNetwork[] Teams;
	private int[][] versusData;

	/* BaseballElimination(s)
		Given an input stream connected to a collection of baseball division
		standings we determine for each division which teams have been eliminated
		from the playoffs. For each team in each division we create a flow network
		and determine the maxflow in that network. If the maxflow exceeds the number
		of inter-divisional games between all other teams in the division, the current
		team is eliminated.
	*/
	public BaseballElimination(Scanner s){
		this.N = s.nextInt();
		// Initialize team flow networks
		this.Teams = this.initializeTeams(this.N);
		FordFulkerson Ff;

		// add data from each ith rows to each TeamFlowNetwork
		for(int i = 0; i < N; i++) {
			this.addTeamDataToFlows(i, s);
 		}

		// Set W edges for each TeamFlowNetwork
		for(int i = 0; i < N; i++) {
			this.Teams[i].setWEdges(this.Teams);
		}

		for(TeamFlowNetwork team : this.Teams) {
			if(team.eliminated) {
				this.eliminated.add(team.name);
			} else {
				Ff = new FordFulkerson(team.FlowNet, 0, team.V()-1);

				for(FlowEdge e : team.FlowNet.adj(0)) {
					if(e.capacity() != e.flow()) {
						// System.out.println(e);
						team.eliminated = true;
						this.eliminated.add(team.name);
						break;
					}
				}

			}
		}
	}

	public TeamFlowNetwork[] initializeTeams(int N) {
		TeamFlowNetwork[] Teams = new TeamFlowNetwork[N];
		for(int i = 0; i < Teams.length; i++) {
			Teams[i] = new TeamFlowNetwork(N, i);
		}
		return Teams;
	}

	public void addTeamDataToFlows(int i, Scanner s) {
		int N = this.N;
		int numGames;
		TeamFlowNetwork team = this.Teams[i]; // grab ith TeamFlowNetwork
		team.name = s.next();
		team.wins = s.nextInt();
		team.gamesLeft = s.nextInt();
		// System.out.printf("Name: %s\n Wins: %d\n GamesLeft: %d\n VersusData:\n", team.name, team.wins, team.gamesLeft);
		// for each versus column, add this data to all relevant TeamFlowNetworks
		for(int j = 0; j < N; j++) {
			numGames = s.nextInt();
			// System.out.printf("vs%d: %d\n", j-3, numGames);
			if(numGames != 0) {
				this.addVersusEdges(i, j, numGames);
			}
		}
	}

	public void addVersusEdges(int i, int j, int numGames) {
		TeamFlowNetwork[] teams = this.Teams;
		int N = this.N;
		int versusIndex;
		FlowEdge sToVersusEdge, iEdge, jEdge;
		// System.out.println();
		// System.out.printf("Adding Versus Edges from %s\n", teams[i].name);
		for(int k = 0; k < N; k++) {
			if( k == i || k == j || teams[k].marked[i][j] == 1 ) continue;
			versusIndex = i*N+j+1; // Represents the index of the i vs j games left vertex
			sToVersusEdge = new FlowEdge(0, versusIndex, numGames);
			iEdge = new FlowEdge(versusIndex, i+1, Double.POSITIVE_INFINITY);
			jEdge = new FlowEdge(versusIndex, j+1, Double.POSITIVE_INFINITY);

			teams[k].addEdge(sToVersusEdge);
			teams[k].addEdge(iEdge);
			teams[k].addEdge(jEdge);
			teams[k].marked[i][j] = 1;
			teams[k].marked[j][i] = 1;
			// System.out.printf("i: %d, j: %d\n", i+1, j+1);
			// System.out.println("adding: " + sToVersusEdge.toString() + " to " + (k+1));
			// System.out.println("adding: " + iEdge.toString() + " to " + (k+1));
			// System.out.println("adding: " + jEdge.toString() + " to " + (k+1));
			// System.out.println();
		}
	}

	/* main()
	   Contains code to test the BaseballElimantion function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below.
	*/
	public static void main(String[] args){
		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}

		BaseballElimination be = new BaseballElimination(s);

		if (be.eliminated.size() == 0)
			System.out.println("No teams have been eliminated.");
		else
			System.out.println("Teams eliminated: " + be.eliminated);
	}
}
