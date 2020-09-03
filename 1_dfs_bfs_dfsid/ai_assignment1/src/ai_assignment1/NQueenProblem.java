package ai_assignment1;
import java.util.*;
import java.io.*;

class Triple {//queue에 add할 때 필요한 class 
	
    int row,column; // 현재 queen의 위치를 저장 (row[0..nV-1], column[1..nV] 값으로 정) 
    int[] pos; // 현재 column 이전까지의 queen의 위치를 저장 

    public Triple(int row, int column, int[] pos, int n) {
      this.row=row;
      this.column=column;
      this.pos=new int[n];
      for(int i=0;i<n-1;i++) //pos에 n-2개의 queen의 position값 복사 
    	  this.pos[i]=pos[i];
    }
  }

class Graph{//bfs, dfs, dfid에 공통적으로 필요한 요소(변수 및 메소드)를 위한 Graph 클래스 정의
	
	int nV; // 정점의 개수
	int[] position; // row마다 queen의 위치(index)
	boolean haveSolution=false; // 답의 존재 여부(존재하면 true, 존재 안하면 false)
	double beforeTime, afterTime; // 실행시간 측정(실행 전 : beforeTime, 실행 후 : afterTime)
	
	public Graph(int nV) { //생성자 
		this. nV = nV;
		this.position = new int[this.nV+1];
		this.haveSolution=false;
		this.clearPosition();
	}
	
	public void printResult() { //답이 존재할 경우에는 이 메소드 호출하여 queen의 위치 출력 
		try {
			NQueenProblem.bs.write(("Location : ").getBytes());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i=1;i<=this.nV;i++) {
			try {
				NQueenProblem.bs.write((Integer.toString(position[i])+" ").getBytes());//문자열로 변환하여 출력 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			NQueenProblem.bs.write("\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isSolution() { //저장된 queen의 position값을 이용하여 n개의 queen이 서로 공격하지 않는지 체크 
		for(int i=1;i<=this.nV;i++) {
			if(position[i]==-1)return false;
			for(int j=1;j<=this.nV;j++) {
				if(i==j)continue;
				if(position[i]==position[j] || Math.abs(j-i)==Math.abs(position[i]-position[j]))
					return false;
			}
		}
    	return true;
    }
	
	public boolean isSolution(int[] pos) {//위의 isSolution()메소드에
										  //매개변수 pos의 값을 해당 객체의 position에 복사하는 과정만 추가 
		
		for(int i=0;i<pos.length;i++) {
			position[i]=pos[i];
		}
		
		for(int i=1;i<=this.nV;i++) {
			if(position[i]==-1)return false;
			for(int j=1;j<=this.nV;j++) {
				if(i==j)continue;
				if(position[i]==position[j] || Math.abs(j-i)==Math.abs(position[i]-position[j]))
					return false;
			}
		}
    	return true;
    }
	//getter
	public int[] getGraph() { 
        return this.position;
    }
	
	//queen의 위치 저장하는 position 초기화 
	public void clearPosition() {
        for(int i=0; i<=this.nV; i++) {
        	this.position[i] = -1;
        }
    }
	
}

class DfsGraph extends Graph{//DFS 수행을 위한 class(Graph class 상속)
	
    public DfsGraph(int nV) {//생성자 
        super(nV);
    }
    public void dfs() { //dfsFunc 실행하는 메소드 
    	haveSolution=false;
    	beforeTime = System.currentTimeMillis(); //실행 전 시간 측정 
    	for(int i=0;i<this.nV;i++) {//column 1의 모든 위치에 대해 dfsFunc(int row, int column) 호출 
    		if(dfsFunc(i,1)) { // true 리턴시 가장 먼저 찾은 답이므로 종료 
    			haveSolution=true;
    			break;
    		}
    		clearPosition();
    	}
    	afterTime = System.currentTimeMillis(); //실행 후 시간 측정 
    	try {
			NQueenProblem.bs.write(">DFS\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(!haveSolution)//답이 존재하지 않는 경우 
			try {
				NQueenProblem.bs.write("No solution\nTime : 0.0\n".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else {//답이 존재하는 경우 
    		printResult();
    		try {
				NQueenProblem.bs.write(("Time : "+Double.toString((afterTime - beforeTime)/1000)).getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		try {
    			NQueenProblem.bs.write("\n".getBytes());
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }
    
    public boolean dfsFunc(int row, int column) { // 그래프 탐색 (재귀호출)
        this.position[column]=row; //현재 column의 queen의 위치 업데이트 
        if(column==this.nV) {//모든 column에 대해 queen의 위치 업데이트한 경우 답의 조건에 맞는지 isSolution() 호출 
        	if(isSolution()==true)
        		return true;
        	else
        		return false;
        }
        
        for(int i=0; i<this.nV; i++) {//다음 column의 모든 row에 대해  
            if(dfsFunc(i,column+1))   // dfsFunc() 재귀호출
            	return true;    
        }
        return false;
    }
}

class BfsGraph extends Graph{//BFS 수행을 위한 class(Graph class 상속)
	Queue<Triple> queue = new LinkedList<Triple>();//queue 선언 
    
    public BfsGraph(int nV) {//생성자
    	super(nV);
    }
    public void bfs() {//bfsFunc 실행하는 메소드 
    	haveSolution=false;
    	beforeTime = System.currentTimeMillis();//실행 전 시간 측정
    	for(int i=0;i<this.nV;i++) {
    		int[] tmp = new int[2];//column 1에 대한 queen의 위치를 저장하기 위해서 크기 2의 int[] tmp 선언 
    		queue.add(new Triple(i,1,tmp,2));
    		if(bfsFunc()) {// true 리턴시 가장 먼저 찾은 답이므로 종료 
    			haveSolution=true;
    			break;
    		}
    		clearPosition();
    	}
    	afterTime = System.currentTimeMillis();//실행 후 시간 측정
    	try {
			NQueenProblem.bs.write(">BFS\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(!haveSolution)//답이 존재하지 않는 경우 
			try {
				NQueenProblem.bs.write("No solution\nTime : 0.0\n".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else {//답이 존재하는 경우 
    		printResult();
    		try {
				NQueenProblem.bs.write(("Time : "+Double.toString((afterTime - beforeTime)/1000)).getBytes());
    			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		try {
    			NQueenProblem.bs.write("\n".getBytes());
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }
  
    public boolean bfsFunc() {// 그래프 탐색 
        
        while(!queue.isEmpty()) { //queue가 빌 때까지 실행 
        	Triple fr_val=queue.poll();
        	
        	int cur_row=fr_val.row;//현재 column에 위치시킬 queen의 index값[0..nV-1] 
        	int cur_column=fr_val.column;//현재 column의 위치[1..nV] 
        	int[] cur_pos=fr_val.pos;//현재 column 전 까지의 queen의 position 저장 
        	
        	cur_pos[cur_column]=cur_row;//현재 column의 queen의 위치 저장 
        	
        	if(cur_column==this.nV) {//모든 column에 대해 queen의 위치 업데이트한 경우 답의 조건에 맞는지 isSolution() 호출 
        		if(isSolution(cur_pos)==true)
            		return true;
        		else 
        			continue;
        	}
        	
        	for(int i=0;i<this.nV;i++) {
        		queue.add(new Triple(i,cur_column+1,cur_pos, cur_column+2));
        	}
        	
        }
        
        return false;
    }
}

class DfsIdGraph extends Graph{//DFSID 수행을 위한 class(Graph class 상속)
	
    public DfsIdGraph(int nV) {//생성자 
        super(nV);
    }
    
    public void dfsid() {//dfsidFunc 실행하는 메소드 
    	beforeTime = System.currentTimeMillis();//실행 전 시간 측정 
    	for(int d=1;d<=this.nV;d++) {//깊이 d를 1씩 증가시킴에 따라 dfs실행 
	    	for(int i=0;i<this.nV;i++) {
	    		if(dfsidFunc(i,1,d)) {
	    			haveSolution=true;
	    			break;
	    		}
	    		clearPosition();
	    	}
	    	if(haveSolution)
	    		break;
    	}
    	afterTime = System.currentTimeMillis();//실행 후 시간 측정
    	try {
			NQueenProblem.bs.write(">DFID\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(!haveSolution)//답이 존재하지 않는 경우 
			try {
				NQueenProblem.bs.write("No solution\nTime : 0.0\n".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else {//답이 존재하는 경우 
    		printResult();
    		try {
				NQueenProblem.bs.write(("Time : "+Double.toString((afterTime - beforeTime)/1000)).getBytes());
    			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		try {
    			NQueenProblem.bs.write("\n".getBytes());
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    }

    public boolean dfsidFunc(int row, int column, int depth) { // 그래프 탐색

        this.position[column]=row;//현재 column의 queen의 위치 업데이트 
        if(column==depth) { //지정된 깊이까지 탐색을 완료한 경우 isSolution() 호출 
        	if(isSolution()==true)
        		return true;
        	else
        		return false;
        }
        
        for(int i=0; i<this.nV; i++) {
            if(column+1<=depth) {                //depth보다 작거나 같은경우 
                if(dfsidFunc(i,column+1, depth))// dfsidFunc() 재귀호출
                	return true;    
            }
        }
        return false;
    }
}

public class NQueenProblem {
	
	public static BufferedOutputStream bs = null;
	
	public static void main(String[] args){//메인 메소드 
		int nV=Integer.parseInt(args[0]); //가로, 세로 길이 입력값 저장 
		try {
			bs = new BufferedOutputStream(new FileOutputStream(args[1]+"/result"+Integer.toString(nV)+".txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DfsGraph dfsgraph=new DfsGraph(nV);//dfs를 위한 객체생성 
		dfsgraph.dfs();
		BfsGraph bfsgraph=new BfsGraph(nV);//bfs를 위한 객체생성 
		bfsgraph.bfs();
		DfsIdGraph dfsidgraph=new DfsIdGraph(nV);//dfsid를 위한 객체생성 
		dfsidgraph.dfsid();
		try {
			bs.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
