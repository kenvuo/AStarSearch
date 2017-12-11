import java.util.*;
import java.io.*;

class Board {

	int[][] board;
	int manDistance;
	int[][] goal;
	int size;
	String action = "";
	Node current;

	public Board(int [][] board2) {
		//Add board and goal
		//Copy board over
		size = board2.length;
		board = new int[size][size];
		goal = new int[size][size];
		int counter = 1;
		for(int i = 0; i<size; i++) {
			for (int j = 0; j<size; j++) {
				board[i][j] = board2[i][j];
				//Add the final state, 3x3 it would be 1,2,3,4,5,6,7,8,0
				goal[i][j] = counter;
				counter++; 
			}
		}
		//Set last index to 0
		goal[size - 1][size - 1] = 0;

		//Create node class
		current = new Node();
	}

	//For other paths
	public Board(int [][] board2,Board previous, int c, int h) {
		size = board2.length;
		board = new int[size][size];
		goal = new int[size][size];
		int counter = 1;
		for(int i = 0; i<size; i++) {
			for (int j = 0; j<size; j++) {
				board[i][j] = board2[i][j];
				goal[i][j] = counter;
				counter++; 
			}
		}
		goal[size - 1][size - 1] = 0;
		current = new Node(previous, c, h);
	}

	void addAction(String action) {
		this.action = action;
	}

	int [][] copyBoard(int[][] board2) {
		int size = board2.length;
		int[][] board = new int[size][size];
		for(int i = 0; i<size; i++) {
			for (int j = 0; j<size; j++) {
				board[i][j] = board2[i][j];
			}
		}
		return board;
	}

	int [][] getBoard() {
		return board;
	}

	void printBoard(int[][] board) {
		System.out.println("-----");
		for(int i = 0; i<size; i++) {
			for(int j = 0; j<size; j++) {
				System.out.print(board[i][j] +" ");
			}
			System.out.print("\n");
		}
		System.out.println("-----");
	}


	void printBoard() {
		System.out.println("-----");
		for(int i = 0; i<size; i++) {
			for(int j = 0; j<size; j++) {
				System.out.print(board[i][j] +" ");
			}
			System.out.print("\n");
		}
		System.out.println("-----");
	}

	//Calculate manhattan
	int manhattan() {
		int distance = 0;
		for(int i = 0; i<size; i++) {
			for (int j = 0; j<size; j++) {
				int value = board[i][j];
				if(value != 0) {
					int row = (value - 1) / 3;
					int col = (value - 1) % 3;
					distance += Math.abs(row - (i)) + Math.abs(col - (j));
				}
			}
		}
		return distance;
	}

	boolean isSolved() {
		if(Arrays.deepEquals(board, goal))
			return true;
		return false;

	}

	int findCost() {
		return 1;
	}

	int findRow() {
		//Find where the location of 0 is
		for(int i = 0; i<size; i++)
			for(int j = 0; j<size; j++)
				if(board[i][j] == 0)
					return i;
		return -1;
	}

	int findCol() {
		//Find where the location of 0 is
		for(int i = 0; i<size; i++)
			for(int j = 0; j<size; j++)
				if(board[i][j] == 0)
					return j;
		return -1;
	}

	//Generate boards for pathing
	ArrayList<Board> genPath() {
		ArrayList<Board> paths = new ArrayList<Board>();
		int posRow = findRow();
		int posCol = findCol();
		//Check if you can move left.
		if(posCol != 0) {
			moveBoard(posRow, posCol, posRow, posCol - 1, paths, "L");
		} 
		//Check if you can move up.
		if(posRow != 0 ) {
			moveBoard(posRow, posCol, posRow - 1, posCol, paths, "U");
		}

		//Check if you can move down.
		if(posRow != 2) {
			moveBoard(posRow, posCol, posRow + 1, posCol, paths, "D");
		}

		//Check if you can move right.
		if(posCol != 2) {
			moveBoard(posRow, posCol, posRow, posCol + 1, paths, "R");
		}
		return paths;
	}

	//Create the state after moving
	void moveBoard(int x, int y, int x2, int y2, ArrayList<Board> paths, String action) {
		int [][] copy = copyBoard(board);
		int tmp = copy[x][y];
		copy[x][y] = copy [x2][y2];
		copy[x2][y2] = tmp;
		Board board = new Board(copy);
		board.addAction(action);
		paths.add(board);
	}

	class Node {

		Board parent;
		int cost;
		int hCost;
		int fCost;

		//Root
		Node() {
			parent = null;
			cost = 0;
			hCost = 0;
			fCost = 0;	
		}

		//Other paths
		Node(Board previous, int c, int h) {
			parent = previous;
			cost = c;
			hCost = h;
			fCost = cost + 	hCost;
		}

		int getCost() {
			return cost;
		}

		int getfCost() {
			return fCost;
		}

		Board getParent() {
			return parent;
		}
	}

}

class Solver {


	void solve(int[][] board) {
		Board root = new Board(board);
		Queue<Board> queue = new LinkedList<Board>();
		queue.add(root);

		//Iterations
		int searchCount = 1; 

		while(!queue.isEmpty()) {
			Board tmp = queue.poll();
			//If not solved
			if(!tmp.isSolved()) {
				//Generate the possible paths for the board
				ArrayList<Board> tmpPaths = tmp.genPath();
				ArrayList<Board> paths = new ArrayList<Board>();

				//loop through paths, find their cost.
				for(int i = 0; i<tmpPaths.size(); i++) {
					Board newBoard = new Board(tmpPaths.get(i).getBoard(),
											tmp, 
											tmp.current.getCost() + tmpPaths.get(i).findCost(),
											tmpPaths.get(i).manhattan());
					newBoard.action = tmpPaths.get(i).action;
					paths.add(newBoard);
					//SearchCount is potato. Just takes all paths. Doesnt care about visited nodes.
					//Can be solved with using a boolean array or something, then count amount of true
					searchCount++;
				}


				if(paths.size() == 0)
					continue;

				Board lowestF = paths.get(0);

				//Finds lowest F(n)
				for(int i = 0; i<paths.size(); i++) {
					if(lowestF.current.getfCost() > paths.get(i).current.getfCost() ) {
						lowestF = paths.get(i);
					}
				}

				int minVal = lowestF.current.getfCost();
				for(int i = 0; i<paths.size(); i++) {
					if (paths.get(i).current.getfCost() == minVal) {
						queue.add(paths.get(i));
					}
				}

			} else {
				//Solution found

				Stack<Board> solutionPath = new Stack<Board>();
				solutionPath.push(tmp);
				tmp = tmp.current.getParent();
				
				while(tmp.current.getParent() != null) {
					solutionPath.push(tmp);
					tmp = tmp.current.getParent();
				}
				solutionPath.push(tmp);
				int size = solutionPath.size();
				String stringPath = "";
				for(int i = 0; i<size; i++) {
					tmp = solutionPath.pop();
					tmp.printBoard();
					stringPath += tmp.action;
				}
				System.out.println("Solution");
				System.out.println(tmp.current.getCost());
				System.out.println(stringPath);
				System.out.println(searchCount);
				System.out.println("0");

				try {
					PrintWriter out = new PrintWriter(new File("out.txt"));
					out.println(tmp.current.getCost());
					out.println(stringPath);
					out.println(searchCount);
					out.println("0");
					out.close();
				} catch (FileNotFoundException e) {}

			}
		}
	}

}


class AStarSearch {
	public static void main(String[] args) {

		if(args.length == 1) {
			Scanner in;
			int length;
			int task[][];
			try {
				in = new Scanner(new File(args[0]));
				length = in.nextInt();
				task = new int[length][length];
				for(int i = 0; i<length; i++){
					for(int j = 0; j<length; j++) {
						task[i][j] = in.nextInt();
					}
				}
				Solver solve = new Solver();
				solve.solve(task);
			}
			catch (FileNotFoundException e) {
				System.out.print("Error under reading");
			}

		}
		else {				
			System.out.println("Wrong args. Try: java AStarSearch [FILE]");;	
		}
	}

}
