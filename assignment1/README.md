Programming Assignment1 : DFS, BFS, DFSID   
=================================== 

## 1. 문제 정의  
  코드를 작성하기에 앞서, 각 알고리즘에 대한 state, node, action을 정의했다.  
* state : 가로, 세로의 길이가 n(입력값으로 주어짐)인 체스판에서 queen 들의 배치  
* node :  bfs, dfs, dfsid에 따라 조금씩 다르지만,  
               - 기본적으로 queen들끼리 서로 공격하지 못하게 하기 위해서 각 column에는 1개의 queen이 놓여야 한다는 방식 사용.  
               - column 1부터 queen을 배치시키므로, column i의 부모는(i>1) column i-1이 된다.  
               - column별로 queen의 위치를 저장하는 배열 생성.  
                 
* action : queen을 새로운 column의 row에 배치시키는 것.  
여기서 goal test는 queen 들끼리 가로, 세로, 대각선으로 서로 마주치지 않아서 공격할 수 없는지 확인하는 것을 의미한다.  
  
### 1.1. DFS  
* 재귀적으로 메소드를 호출하여 탐색하였다. 
* column 1부터 queen을 배치시켰고 column N이 되었을 때 goal test를 진행했다.  
* goal test를 통과하면 종료하였고, 이 때 저장된 queen들의 row 값을 출력했다.  
  
### 1.2. BFS  
* queue(fringe)의 add(=push), poll(pop, 단, 맨 앞 원소 리턴)를 사용하여 탐색하였다.  
* bfs는 dfs와 다르게 너비를 우선적으로 탐색하므로, 현재 저장된 멤버 변수 position의 값이 pop된 현재 노드의 position을 의미하지 않으므로 인자를 잘 설정해주어야 한다.  
* queue의 인자로는 현재 column값과 이때 위치시킬 queen을 row값, 그리고 현재 column 이전에 놓여진 queen의 row값들(배열)을 넣어주었다. 
* column 1부터 queen을 배치시켰고 column N이 되었을 때 goal test를 진행하여 통과하면 해당 답을 출력했다.  
     
### 1.3. DFSID  
* 1씩 증가시킨 깊이에 따라 메소드를 재귀적으로 호출하는 방식을 사용하였고 답을 찾으면 종료하여 답을 출력하였다.  
* dfs에 대해서는 재귀적으로 호출하였다.   
* 마찬가지로 column 1부터 queen을 배치시켰고 해당 깊이가 되었을 때 goal test를 진행했다.  
  
## 2. 코드 설명  
  구체적인 설명은 주석으로 표기하였고 여기서는 간단한 틀에 대해서만 서술하였다.  
  공통적으로 dfs, bfs, dfsid가 가지고 있는 요소(변수, 메소드)를 포함하여 Graph 클래스를 생성한 후, 상속했다.  
  Graph 클래스에서는, 아래와 같이   
```c++
	int nV; // 정점의 개수
	int[] position; // row마다 queen의 위치(index)
	boolean haveSolution=false; // 답의 존재 여부(존재하면 true, 존재 안하면 false)
	double beforeTime, afterTime; // 실행시간 측정(실행 전 : beforeTime, 실행 후 : afterTime)
```  
  를 멤버변수로 선언하였고,   
  
  아래와 같이 답을 출력하기 위한 메소드와,  	
```c++
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
```  
goal test를 위한 메소드 2가지를(아래와 같이 인자에 따라 달리 호출) 생성하였다.  
각 position을 -1로 초기화했기 때문에 1) -1인 경우, 2)다른 position과 같은 row에 있는 경우, 3)대각선으로 만나는 경우 false를 반환했다.    
```c++ 	
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
```  
  
### 1.1. DFS  
DFS에서는 DfsGraph 클래스를 정의하여 Graph 클래스를 상속하도록 했고,  
```c++
   class DfsGraph extends Graph{//DFS 수행을 위한 class(Graph class 상속)
```  
  dfs() 메소드를 호출하면 그래프를 탐색하는 dfsFunc을 내부에서 호출하도록 구현했다.  
```c++
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
    ```  
  그리고 dfsFunc은 재귀적으로 호출하도록 하여, goal test를 만족하는 경우에는 true를, 만족하지 않는 경우에는 false를 반환하도록 했다.  
```c++
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
```   
  
### 1.2. BFS  
  
BFS에 대해서는 BfsGraph 클래스를 정의하였고 Graph 클래스를 상속하였으며, queue를 멤버변수로 추가했다.  
```c++
class BfsGraph extends Graph{//BFS 수행을 위한 class(Graph class 상속)
	Queue<Triple> queue = new LinkedList<Triple>();//queue 선언 
```  
bfs() 메소드를 정의하였으며 이 메소드를 호출하면, 전체 그래프를 bfs로 탐색하며 bfsFunc을 호출한다.  
  
```c++
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
  ```     
  그리고 bfsFunc은 아래와 같이 queue가 빌 때까지 탐색하며 답을 찾으면 종료한다.  
```c++
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
```
  
### 1.3. DFSID  
  
DFSID에 대해서는 DfsIdGraph 클래스를 정의하였고, Graph를 상속한다. 
```c++
class DfsIdGraph extends Graph{//DFSID 수행을 위한 class(Graph class 상속)
```  
그리고, dfsid 메소드는 깊이를 1부터 1씩 늘려가면서 해당 깊이까지 dfs를 실행하며, 이때 dfsidFunc을 호출한다.  
```c++ 
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
```  
dfsidFunc은 재귀적으로 호출되며, 지정된 깊이를 만족하는 경우 goal test를 통해서 올바른 답을 찾는다.  
```c++
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
```  
  
  
## 3. 실행 결과  
 입력되는 n값에 따라 생성된 txt파일의 결과를 스크린샷으로 첨부하였다.  
n=2일때와 n=3일 때는 해당 답이 존재하지 않았다. (no solution 출력)   
  ![image](/uploads/d23f59fb4caa863a349be6bf5d296ecb/image.png)  

* n=1 일때,   
![image](/uploads/94732b122d820c7a3600813a5dc4358e/image.png)
  
* n=2 일때,  
![image](/uploads/c2fb2eefc732a20dbe26191b4ec8ac47/image.png)  
 
* n=3 일때,  
![image](/uploads/e73c0604bb0ebf069980a3c15696c382/image.png)  

* n=4 일때,  
![image](/uploads/ded9a32a149db433b99d5a16424719a1/image.png)  

* n=5 일때,  
![image](/uploads/9c76fa3cda3d8c35638433718a090daa/image.png)  
  

* n=6 일때,  
![image](/uploads/c0eb571a153aa2d8c0290f801f5ac67d/image.png)  
   

* n=7 일때,  
![image](/uploads/742d3471ecef378760bed1f3c067f436/image.png)  
  

* n=8 일때,  
![image](/uploads/16dee36ca7ac92a5596294e631957b79/image.png)   
  
  
n이 9 이후인 경우에 대해서는 java 의 heap 공간이 부족하다고 했다.  (아래 결과 이미지 첨부)
이 경우에는 4번의 개선점 부분에서 다루겠다. 
![image](/uploads/7e862860142bb4f94572263c908496be/image.png)

## 4. 개선점 및 다른 방안  
  알고리즘적으로 메모리를 개선할 수 있는 방안에 대해 적어보았다.  
###     4.1. 개선점  
  이 과제에서는 명세에 prunning을 진행하지 않는 것으로 되어 각 열별로 queen을 하나씩 넣을 수 있는 모든 경우에 대해 조사했다. 
 이 경우에는 각 column에 대해서 queen이 하나씩 놓일 수 있다는 제한만 두었기 때문에,  
bfs를 실행할 때, queue에 이전 경로를 저장하기 위한 메모리 공간이 크게 필요하다는 단점이 있다. 
이를 개선하기 위해서는 prunning을 진행할 수 있는데, 
1. 각 column에 두 개 이상의 queen이 놓일 수 없으며, 각 row에 두 개 이상의 queen이 놓일 수 없다. 
2. 각 queen끼리 공격하지 못하게 하기 위해서는 queen끼리 나이트의 이동(한 칸 이동 + 대각선으로 한 칸 이동)으로 설명될 수 있어야 한다.  
등의 제한을 두고 진행할 수 있다.  
dfs나 dfsid, bfs의 경우 위 조건을 만족하지 않는 경우 해당 메소드를 호출하지 않거나, 해당 원소를 queue에 넣지 않는 등의 방법을 사용하면 해결할 수 있다.   

###     4.2. 다른 방안  
dfs, bfs, dfsid의 방법을 이용하지 않고 nqueen의 해를 구할 수 있는 방법이 있다.  
단순한 계산을 통해서 구할 수 있는 방법이 
[링크](https://en.wikipedia.org/wiki/Eight_queens_puzzle#Explicit_solutions)  
여기에 제시되어 있다.  
 
