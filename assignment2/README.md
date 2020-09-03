Programming Assignment2 : Hill Climbing Algorithm  
=================================== 

## 1. 알고리즘에 대한 설명 및 objective function  
  Hill Climbing 알고리즘이란 Local search algorithm에 속하는 것으로  
Objective function에 따라서 현재 노드를 기준으로 이웃 노드를 확인하여 더 좋은 노드로 이동하는 것이다.  
따라서 path를 저장하는 다른 알고리즘과는 다르게 결과를 중요시하여 메모리를 효율적으로 사용할 수 있다는 장점이 있다.  
코드는 뒤에서 자세히 설명하고 여기서는 간단한 알고리즘에 대해 서술하겠다. 

### 1.1. 알고리즘   
* 초기 상태는 queen의 위치를 랜덤하게 지정하였다.  
* 현재 상태에서 다음 상태로 이동할 때에는 heuristic 함수를 정의하여 해당 값이 가장 적은 상태로 이동할 수 있도록 하였다.    
* 만약 이 값이 현재 상태의 heuristic 값과 같으면 다시 초기 상태를 랜덤으로 지정하여 RandomRestart하도록 하였다.  
  
### 1.2. objective function  
* 현재 상태에서 한 개의 queen의 위치를 한 번 이동시켰을 때를 다음 상태로 보았다. 
* queen을 한 번 이동시켰을 때, 모든 queen사이에서 공격이 가능한 경우를 세어 heuristic 값으로 저장했다. 
* 작을 수록 좋은 값을 가진 상태를 의미한다. 

## 2. 코드 설명  
  구체적인 설명은 주석으로 표기하였고 여기서는 간단한 틀에 대해서만 서술하였다.  
  먼저 Position 클래스는 queen의 위치를 저장하기 위해서 배열을 활용할 때 사용하였으며,  
```c++
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
```  
  각 column마다 위치한 queen의 위치를 저장했다.  
이때, queen의 위치는 입력 값으로 N이 주어지는 경우, 0부터 N-1 값을 갖는다.  

먼저, N=2이거나 3인 경우는 답이 존재하지 않으므로 예외 처리를 해주었고,  

답이 존재하는 경우에는 초기 상태를 지정해주었다.  
초기 상태는 다음과 같이 랜덤으로 지정 하였으며,  
 	
```c++
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
```  
메인문에서는 위와 같이 heuristic 값이 0인 경우 종료시키고 아닌 경우에는 다음 state로 이동했다.  
다음 상태로 이동하는 경우는 아래와 같이 nextState 메소드를 이용하였는데,  

```c++ 	
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
```  

부분적으로 살펴보면,  
먼저, nextState에 현재 상태의 queen의 위치를 복사하였고,  

```c++
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
```  
각 Column마다 queen을 옮겨가면서 heuristic값을 구하였고,  
queen을 옮기는 데에는 changeRow 메소드를 정의하여 이용했다. 
이때 값 중에 가장 작은 값을 better에 저장했다. 

```c++
    if (better < cur_eval) {
        	heuristic = better;
            nextState[changedColumn].changeRow(changedRow);
        } else {
        	//작지 않으면 처음부터 랜덤으로 초기화한 상태로 다시 시작한다. 
            nextState = initial_state();
            heuristic = heuristicFunc(nextState);
            randomRestart++;
        }
    ```  
 그리고, 현재 heuristic 값인 cur_eval보다 better에 저장된 값이 더 좋은 경우(==더 작은 경우),  
nextState를 그 상태로 이동하였고,  
아닌 경우는 다시 랜덤으로 초기화하여 처음부터 시작하였다.  

heuristicFunc은 아래와 같이 정의하였는데,  
```c++
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
```   
모든 쌍에 대해서, 공격이 가능한 위치의 경우 heuristic 값을 1씩 증가시켰다.  

isAttack 메소드는 
```c++
public static boolean isAttack(Position x, Position y) {  
    	if(x.getRow() == y.getRow() || x.getColumn() == y.getColumn() || 
    	(Math.abs(x.getColumn()-y.getColumn()) == Math.abs(x.getRow()-y.getRow())))
            return true;
        return false;
    }
```  
같은 행이나 열에 queen이 있거나 같은 대각선상에 queen이 있는 경우 true를 return 하도록 했다.  
  
## 3. 실행 결과  
> 스크린샷 누락  
 입력되는 n값에 따라 생성된 txt파일의 결과를 스크린샷으로 첨부하였다.  
random restart를 몇 번하는지에 대해서도 출력했다.  

## 4. 결과 분석  
> 스크린샷 누락  
코드를 수정하여 각 n에 대하여 50번씩 실행했을 때 random restart 의 평균값을 구하도록 하였다. 
각 n에 대하여 50번씩 실행했을 때 random restart의 평균값은 다음과 같다.  

* n=1 : 0번  

* n=2 : no solution  

* n=3 : no solution  

* n=4 : 1.7번  

* n=5 : 0.46번  

* n=6 : 6.86번  

* n=7 : 2.88번

* n=8 : 5.76번  

* n=9 : 5.54번  

n 값이 커질수록 전체적으로 random restart 하는 경우가 늘어나는 것을 알 수 있다.  
