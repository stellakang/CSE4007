package ai_assignment2;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

//queen의 위치를 저장하기 위한 클래스 
class Position{
	//column마다 저장되어있는 queen의 위치를 row에 저장함(배열로 전체 queen의 위치 저장)
	private int row;
	private int column; 
	
	public Position(int row, int column) {
		 this.row = row;
	     this.column = column;
	}
	
	public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
    
    //row를 바꾸는 메소드
    public void changeRow (int row) {
        this.row=row;
    }
}

public class Hill_climbing {
    private static int nV;//정점의 갯수 
    private static int heuristic = 0;
    private static int randomRestart = 0;
	private static double beforeTime, afterTime; // 실행시간 측정(실행 전 : beforeTime, 실행 후 : afterTime)
	private static Position[] current;
	private static boolean haveSolution=false; // 답의 존재 여부(존재하면 true, 존재 안하면 false)
	public static BufferedOutputStream bs = null;
	
	public Hill_climbing() {
		current = new Position[nV];
		clearPosition();
	}
    //처음 queen의 위치를 랜덤하게 지정한다. 
    public static Position[] initial_state() {
        Random rand = new Random();
        Position[] init_state=new Position[nV];
        for(int i=0; i<nV; i++){
            init_state[i] = new Position(rand.nextInt(nV),i);
        }
        return init_state;
    }

    //답이 존재할 경우에는 이 메소드 호출하여 queen의 위치 출력
    private static void printResult(Position[] result) {
        for (int i=0; i<nV; i++) {
        	try {
        		bs.write((Integer.toString(result[i].getRow())+" ").getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        try {
        	bs.write("\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //저장된 queen의 position값을 이용하여 n개의 queen이 서로 공격하지 않는지 체크
    public static boolean isAttack(Position x, Position y) {  
    	if(x.getRow() == y.getRow() || x.getColumn() == y.getColumn() || 
    	(Math.abs(x.getColumn()-y.getColumn()) == Math.abs(x.getRow()-y.getRow())))
            return true;
        return false;
    }
    
    // 현재 퀸의 위치를 저장하는 current 배열을 -1값으로 초기화하는 메소드 
    public static void clearPosition() {
        for(int i=0; i<nV; i++) {
        	current[i] = new Position(-1,i);
        }
    }
    
    // copyFrom에서 copyTo로 값 복사하는 메소드
    public static void copyPosition(Position[] copyTo, Position[] copyFrom) {  
    	for (int k=0; k<nV; k++) {
            copyTo[k] = new Position(copyFrom[k].getRow(), copyFrom[k].getColumn());
        }
    }
    
    // heuristic 값을 구하는 메소드
    // 작을수록 좋은 것으로 정의함. 
    public static int heuristicFunc(Position[] state) {
        int heuristic = 0;
        for (int i = 0; i< nV; i++) {
            for (int j=i+1; j<nV; j++ ) {
            	//모든 쌍에 대해서 서로 공격이 가능한 위치인 경우 하나 증가 시킨다. 
                if (isAttack(state[i],state[j])) {
                    heuristic++;
                }
            }
        }
        return heuristic;
    }

    // 다음 state를 정의하는 메소드 
    public static Position[] nextState (Position[] curState) {
        Position[] nextState = new Position[nV]; // 다음 state의 queen의 위치를 저장할 공간 
        
        int cur_eval = heuristicFunc(curState);// 현재 state의 heuristic 값 
        
        int better = cur_eval; // Heuristic 값이 가장 작은거 저장 
        int changedRow=-1,changedColumn=-1; // 가장 작은 값을 갖는 경우 이동시킨 Column의 queen의 위치를 저장 

        copyPosition(nextState, curState); // 일단 nextState에 현재 queen의 위치를 저장 
        
        for (int i=0; i<nV; i++) {//Column
            for (int j=0; j<nV; j++) {//Row
            	//현재 curState값과 같으면 continue;
            	if(curState[i].getRow()==j)continue; 
            	
                //Column i의 queen의 위치를 j로 바꾼다. 
            	nextState[i].changeRow(j);
            	//이때 heuristic 값 구한다. 
                int tmp = heuristicFunc(nextState);
                
                //better에 저장된 값보다 작으면 갱신 
                if (tmp < better) {
                    better = tmp;
                    changedRow=j;
                    changedColumn=i;               
                }
                //curState의 queen위치로(원상태로) 돌려놓는다. 
                nextState[i].changeRow(curState[i].getRow());
            }
        }
        // better에 저장된 값이 현재 휴리스틱 값보다 작으면 
        // 그 상태를 다음 상태로 정의한다. 
        if (better < cur_eval) {
        	heuristic = better;
            nextState[changedColumn].changeRow(changedRow);
        } else {
        	//작지 않으면 처음부터 랜덤으로 초기화한 상태로 다시 시작한다. 
            nextState = initial_state();
            heuristic = heuristicFunc(nextState);
            randomRestart++;
        }
        return nextState;
    }

    public static void main(String[] args) {
    	nV=Integer.parseInt(args[0]); //가로, 세로 길이 입력값 저장 
		try {
			bs = new BufferedOutputStream(new FileOutputStream(args[1]+"/result"+Integer.toString(nV)+".txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    if ( nV == 2 || nV ==3) {
	    	haveSolution=false;
	    }
	    else {
	    	haveSolution=true;
	    }
	    try {
			bs.write(">Hill Climbing\n".getBytes());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        if(haveSolution) {
	    	beforeTime = System.currentTimeMillis(); //실행 전 시간 측정 
	        
	    	//처음 state 랜덤하게 지정 
	        Position[] curState=new Position[nV];
	    	curState=initial_state();
	    	
	    	// 초기 state의 heuristic값 저장 
	        heuristic = heuristicFunc(curState);
	        
	        while (true){
	        	//heuristic 값이 0인 경우(goal) 종료한다. 
	        	if(heuristic==0) {
	        		break;
	        	}
	        	// 다음 state로 이동 
	            curState = nextState(curState);
	        }
	    	afterTime = System.currentTimeMillis(); //실행 후 시간 측정 
	
	        //결과 출력 
    		printResult(curState);
    		try {
    			bs.write(("Total Elapsed Time: "+Double.toString((afterTime - beforeTime)/1000)).getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		try {
    			bs.write(("\nNumber of random restarts: "+Integer.toString(randomRestart)).getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
    	}
    	else {
    		try {
    			bs.write("No solution\nTotal Elapsed Time : 0.0\n".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        try {
			bs.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}