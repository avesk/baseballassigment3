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

	// We use an ArrayList to keep track of the eliminated teams.
	public ArrayList<String> eliminated = new ArrayList<String>();
	private String[] teams;
	private int[][] teamData;
	private int N;

	/* BaseballElimination(s)
		Given an input stream connected to a collection of baseball division
		standings we determine for each division which teams have been eliminated
		from the playoffs. For each team in each division we create a flow network
		and determine the maxflow in that network. If the maxflow exceeds the number
		of inter-divisional games between all other teams in the division, the current
		team is eliminated.
	*/
	public BaseballElimination(Scanner s){
		this.buildTeamsData(s);
		// this.printTeamData();
	}

	public void printTeamData() {
		for(int i = 0; i < this.N; i++) {
			System.out.printf("%s:    ", this.teams[i]);
			for(int j = 0; j < N+2; j++) {
				System.out.printf("%d, ", this.teamData[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	public void buildTeamsData(Scanner s) {
		this.N = s.nextInt();
		// Rows: Team
		// Columns: 0: wins, 1: games left, (2 to N+2-1): games left versus Ni
		this.teamData = new int[N][N+2];
		this.teams = new String[N];

		for(int i = 0; i < N; i++) {
			this.setDataForTeam(i, s);
 		}
	}

	public void setDataForTeam(int i, Scanner s) {
		this.teams[i] = s.next();
		for(int j = 0; j < N+2; j++) {
			this.teamData[i][j] = s.nextInt();
			printTeamData();
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
