Programming Assignment3 : Genetic Algorithm  
=================================== 

## 1. 개요    
  알고리즘을 구현한 방식과 각 메소드에 대한 대략적인 설명을 넣었습니다.   

### 1.1. 구현 방식     
![image](/uploads/4be92065cf8d65fe949bb610c27c0ccb/image.png)
전체적인 흐름은 위와 같습니다.  

### 1.2. 변수 설명   
구현에 사용한 변수는 아래와 같습니다.  
  * private int nV;//정점의 개수   
  * private int[][] currentGen;//현재 세대의 개체 모음   
  * private int[][] nextGen;// 다음 세대의 개체 모음(이후 현재 세대로 복사)  
  * private Boolean[] selected;// 선택되었던 부모는 다시 뽑지 않음   
  * private static final int POPULATION=50000;// 한 세대당 개체 수  
  * private static final int TOURNAMENT_SELECTION=7;// 부모 선택시 tournament selection에서 k값  
  * private static int MOSTFIT;// 가장 적합한 경우에 해당하는 fitnessFunction 값( nV combination 2)  
  * private static int MUTATION_NUM;// mutation 실행 횟수 -> MUTATION_NUM+1만큼 실행  
  * private static double CRITERIA=0.7;// tournament selection에서 사용하는 기준값   

### 1.3. 메소드별 설명  
메소드 별로 하는 역할에 대해 서술했습니다. 구체적으로 구현한 코드는 2. 코드 구현 에 제시했습니다.   
* public void initFirstGen()  
가장 처음 세대를 생성하는 메소드 입니다. (0세대로 봐도 무방하나 저는 1세대로 보았습니다. )  
Random 클래스의 nextInt 메소드를 이용하여 퀸의 위치 랜덤하게 생성  
-> Collections.shuffle 메소드 이용하여 배열을 랜덤 순서로  섞어줌 

* public void next_Generation()  
다음 세대를 정하는 메소드 입니다.  
![image](/uploads/10e4d3a46b4f3b6c82e34e3932e796a5/image.png)

위와 같이 내부에서 tourna_Selection(), crossOverFunc(), mutationFunc()을 호출하여 진행되며 해당 메소드는 아래에 서술했습니다.  

* public void tourna_Selection()  
부모를 선정하는 메소드입니다.  
![image](/uploads/cb7aea30142ce8dc943fced13488125f/image.png)
위와 같은 과정을 통해 적합한 개체를 선택할 수 있는 가능성을 높여서 답으로 수렴하는 속도를 높이되,  
premature한 수렴을 줄이도록 다양한 개체를 선정할 수 있는 가능성을 높이고자 했습니다.  

* public int hasMostFit()  
적합한 솔루션이 존재하는 경우 해당 개체의 인덱스를 리턴하는 메소드 입니다.  
적합한 솔루션은 fitnessFunction값으로 정의되며 이 값이 MOSTFIT 변수의 값과 같을 경우 적합하다고 판단합니다.  

* public int fitnessFunc(Position[] position)  
퀸의 위치가 position 배열로 주어지면 모든 쌍에 대해서 퀸끼리 서로 공격이 불가한 경우의 수를 fitness 값으로 정의하였습니다.  
해당 값이 (정점의 갯수)combination 2와 같으면 (MOSTFIT 값과 같으면) 적합한 것으로 판단합니다.  

* public int[] crossOverFunc(int first, int second)  
1-point crossover를 사용한 메소드 입니다.  
자세한 코드 설명은 아래에 있습니다.  

* public int[] mutationFunc(int[] child)  
mutation을 진행하는 메소드 입니다.  
 

## 2. 코드 구현  
1.3 메소드별 설명'에서 서술한 메소드에 대한 구현과 부가적인 메소드에 대한 코드는 아래와 같습니다.  

* public void initFirstGen()  
```{.c++}  
        public void initFirstGen() {
		Random rand = new Random();
                 int[] random_pos=new int[nV];
		for(int i=0;i<POPULATION;i++) {
			for(int j=0;j<nV;j++) {
				int num=rand.nextInt(nV);
				random_pos[j]=num;
			}//랜덤으로 정한 후, 
			Collections.shuffle(Arrays.asList(random_pos));// shuffle을 통해 랜덤으로 섞어주는 과정 
			for(int j=0;j<nV;j++)
				currentGen[i][j]=random_pos[j];// 현재 세대에 추가 
		}
		
	}
```  
random_pos 배열에 nextInt(nV) 를 호출하여 각각 랜덤한 퀸의 위치를 인덱스로 저장했습니다.  
0부터 nV-1 중에서 랜덤한 값을 저장합니다.  
이후에는 shuffle 메소드를 이용하여 랜덤으로 배열의 순서를 변경하도록 했습니다.  

* public void next_Generation()  
```{.c++}
        //다음 세대를 정하는 메소드 
	public void next_Generation(){
		Random rand = new Random();
		//System.out.println(currentGen.size());
		tourna_Selection(); // 부모를 정한다.
		//System.out.println("size : "+nextGen.size());
		
		for(int i=POPULATION/10;i<POPULATION;i++) {
			//System.out.println("size : "+nextGen.size());
			int firstIndx=rand.nextInt(POPULATION/10);
			int secondIndx=rand.nextInt(POPULATION/10);
			
			// 서로 다른 부모로 선정 
			while(firstIndx==secondIndx) {
				firstIndx=rand.nextInt(POPULATION/10);
				secondIndx=rand.nextInt(POPULATION/10);
			}
			
			int[] crossover_mutation=new int[nV];
			//크로스오버 메소드 실행 
			crossover_mutation=crossOverFunc(firstIndx, secondIndx);
			//뮤테이션 메소드 실행 
			crossover_mutation=mutationFunc(crossover_mutation);
			
			//부모로 추가 
			nextGen[i]=crossover_mutation;
		}
	}  
```
* public void tourna_Selection()  
부모를 선정하는 메소드로  코드는 아래와 같습니다. 

```{.c++}  
        double BestOrTournament=Math.random();
```  
위와 같이 난수를 발생시켜 두가지 상황으로 나누어서 부모를 선정했습니다.  
0.5보다 작은 경우에는  
적합한 것을 선정할지 적합하지 않은 것을 선택할지 난수를 발생하여 정하였고,  
```{.c++}  
        if(BestOrTournament<0.5) {// 좋은 것을 선정할지 상대적으로 나쁜 것을 선정할지 정한다. 
		int indx=rand.nextInt(POPULATION);// 한가지 먼저 선택 
		max=fitnessFunc(intToPosition(currentGen[indx]));
		max_indx=indx;
		for(int j=1;j<TOURNAMENT_SELECTION;j++) { 
			indx=rand.nextInt(POPULATION);
			// 한번도 뽑히지 않았던 부모로 선정 
			while(selected[indx]==true) {
				indx=rand.nextInt(POPULATION);
			}
			
			int fitnum=fitnessFunc(intToPosition(currentGen[indx]));
			double chooseGoodOrBad=Math.random();// 난수 생성 
			
			//난수 생성해서 CRITERIA보다 크면 좋은거 선택, 작으면 나쁜거 선택 
			if((fitnum>max && chooseGoodOrBad>CRITERIA) || (fitnum<max && chooseGoodOrBad<CRITERIA)) {
					max=fitnum;
					max_indx=indx;
			}
		}
	}
```
0.5보다 크거나 같은 경우에는 아래와 같이 가장 좋은 것을 선정하도록 했습니다.  
```{.c++}  
          else {// 무조건 좋은 것을 선정한다.
	        for(int j=0;j<TOURNAMENT_SELECTION;j++) {
		        int indx=rand.nextInt(POPULATION);
					
		        while(selected[indx]==true) {
		    	      indx=rand.nextInt(POPULATION);
		        }
		        int fitnum=fitnessFunc(intToPosition(currentGen[indx]));
		        if(fitnum>max) {
			    max=fitnum;
			    max_indx=indx;
		        }
	        }			
        }
```
또한 중복으로 부모를 선정하는 것을 막기 위해서  
```{.c++}  
          for(int i=0;i<POPULATION;i++)
	         selected[i]=false;
```  
선정되는 것은 true로 변경하는 배열을 선언했고 위와 같이 초기화 했습니다.  

* public int hasMostFit()  
```{.c++}
        // 현재 세대에서 솔루션을 찾았으면 해당 인덱스 반환 (아니면 -1)
	public int hasMostFit() {
		for(int i=0;i<POPULATION;i++) {
			int fitnessnum=fitnessFunc(intToPosition(currentGen[i]));
			//System.out.println("fitness num "+ fitnessnum);
			if(fitnessnum==MOSTFIT)
				return i;
		}
		return -1;
	}
```  
현재 세대의 모든 개체에 대해서 fitnessFunction값을 계산하고 이 값이 찾고자하는 개체의 fitness값과 일치하면 이를 출력하고 종료하도록 했습니다.   
적합한 개체를 찾았으면 해당 인덱스를 반환하도록 했고 아닌 경우에는 -1을 반환하여 다음 세대로 넘어갈 수 있도록 했습니다.  
메인에서 이 메소드를 호출하여  -1인 경우에는 다음 세대로 넘어가고 적합한 개체를 찾은 경우에는 해당 개체를 출력하도록 했습니다.  

*  public int fitnessFunc(Position[] position)  
position 인자의 퀸의 위치에 대한 fitness값을 리턴하는 메소드 입니다.  
공격이 불가능한 경우에는 이 값을 하나씩 증가 시켰다.   
```{.c++}
          public int fitnessFunc(Position[] position) {
	      int fitnessNum=0;
		
	      for (int i = 0; i< nV; i++) {
                  for (int j=i+1; j<nV; j++ ) {
                 	//모든 쌍에 대해서 서로 공격이 불가능한 위치인 경우 하나 증가 시킨다. 
                      if (isAttack(position[i],position[j])==false) {
                          fitnessNum++;
                      }
                  }
              }
              return fitnessNum;
        }
```  
* public int[] crossOverFunc(int first, int second)  
1-point cross over를 시행하는 메소드 입니다.  
부모의 인덱스는 first, second이고,  
랜덤으로 1부터 nV-1값 중에 하나의 숫자를 받아와서  
해당 숫자부터 끝까지 값을 mother로 바꾸어 father에 저장하며 이는 곧 생성된 자식 개체가 됩니다.  
이를 반환하게 됩니다.  
```{.c++}
        //부모 중에서 first, second 인덱스를 갖는 부모를 이용하여 crossOver를 하는 메소드 
	public int[] crossOverFunc(int first, int second) {
		Random rand = new Random();
		int[] father=new int[nV];
		int[] mother=new int[nV];
		
		for(int i=0;i<nV;i++) {
			father[i]=nextGen[first][i];
			mother[i]=nextGen[second][i];
		}
		
		//father에 생성할 자식을 저장하여 반환 (point부터 끝까지를 mother로 바꿈) 
		int point=rand.nextInt(nV-1)+1;// 자리 바꿀 위치 선정 - (1..nV-1) 범위로 선정 
		for(int i=point;i<nV;i++) {
			father[i]=mother[i];
		}
		return father;//생성된 자식 반환 
	}
```

* public int[] mutationFunc(int[] child)  
mutation 실행 횟수는 (MUTATION_NUM에 저장된 값+1) 만큼 실행됩니다.  
숫자는 조절이 가능하며 해당 횟수만큼 랜덤으로 point값을 정하여 랜덤으로 생성된 s로 바꾸었습니다.  
다만, 이때 s와 child[point]의 값이 같으면 mutation의 의미가 없기 때문에 달라질 때까지 랜덤으로 값을 받아왔습니다.  
```{.c++}
        // first, second 값을 갖는 부모를 이용해서 뮤테이션 실행 
	public int[] mutationFunc(int[] child) {
		Random rand = new Random();
		int num=nV/3;//mutation 실행 횟수 설정 
		int arr[]=new int[nV];
		for(int i=0;i<nV;i++)
			arr[i]=child[i];
		
		//해당 횟수만큼 mutation 실행 
		while(num>=0) {
			int point=rand.nextInt(nV);
			int s=rand.nextInt(nV);
			//생성된 난수값이 현재 child의 값과 다르도록 선정(변화가 있도록) 
			while(s==child[point]) {
				s=rand.nextInt(nV);	
			}
			arr[point]=s;
			num--;
		}
		
		return arr;
	}
```  

  메인 메소드의 코드를 살펴보면 아래와 같습니다.  
```{.c++}  
          //답이 없는 경우 
	  if(n==2 || n==3) {
		try {
    			bs.write("No solution\nTotal Elapsed Time : 0.0\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }  
```  
답이 없는 경우에는 따로 처리를 하였고  

답이 있는 경우에는  
무한 루프가 도는 것을 막기 위해 최대 생성 세대수를  
```{.c++}
          int generationNum=10000;//무한 루프를 막기 위한 생성 세대수 제한 
```
설정해두고 실행했습니다. 
코드의 일부를 살펴보면 먼저 initFirstGen() 메소드를 호출하여 첫 세대를 생성 하였고  
```{.c++}
          else {// 답이 존재하는 경우 
			beforeTime = System.currentTimeMillis(); //실행 전 시간 측정 
			GA_nqueen ga=new GA_nqueen(n);
			ga.initFirstGen();
			int cnt=0;//답을 찾기 까지 생성된 세대수 
			int generationNum=10000;//무한 루프를 막기 위한 생성 세대수 제한 
			
			while(generationNum>0) {
				cnt++;
				//System.out.println(cnt);
				int success=ga.hasMostFit();
				//답이 존재하는 경우 
				if(success!=-1) {
					afterTime = System.currentTimeMillis(); //실행 후 시간 측정 
```

이후에 답이 존재하는 경우에는 종료를 하고  
답이 존재하지 않는 경우에는 아래와 같이  
next_Generation() 메소드를 호출하였습니다.  
```{.c++}
          ga.next_Generation();// 다음 세대 선정 
	  // 다음세대를 현재 세대로 변경 
	  for(int i=0;i<POPULATION;i++) {
		ga.currentGen[i]=ga.nextGen[i];
	  }
          generationNum--;

```
## 3. 실행 결과  

먼저, POPULATION=50000, TOURNAMENT_SELECTION=7, CRITERIA=0.5 로 두고 실행했습니다.  

* n=1  
![image](/uploads/cfa474c1f11b73425142ee9b3d973fc2/image.png)  

* n=2  
![image](/uploads/604513c1f2eda57936b3f8ad5127c58e/image.png)   

* n=8  
![image](/uploads/343f41b8085736db5730b732293e2f14/image.png)

* n=12  
![image](/uploads/9372f0b5227d98f9e314a18359bd331f/image.png)  

그다음에는 POPULATION=100000, TOURNAMENT_SELECTION=7, CRITERIA=0.5 로 두고 실행했습니다.  
한 세대당 개체수가 많을 수록 적은 세대 안에 빠르게 수렴하는 것을 볼 수 있습니다.  
* n=1  
![image](/uploads/86847768d46ac306e9b22c8c6464ef9b/image.png)

* n=2  
![image](/uploads/e27a3023438482d2a2433d445009e37e/image.png)


* n=8  
![image](/uploads/c6176c5cd88a4bcf3be54b7b5b6a40bb/image.png)

* n=12  
![image](/uploads/7a6b3db3683dc427f05aa69607cb2fad/image.png)


그다음에는 POPULATION=100000, TOURNAMENT_SELECTION=100, CRITERIA=0.5 로 두고 실행했습니다.  
더 많은 인원을 부모로 선정할수록 속도가 빨라지는 것을 알 수 있습니다.  (fitness function값이 높은 것이 다음 세대로 들어갈 확률이 높아진다. )  
* n=1  
![image](/uploads/441a47620101efd04f56be402f4111bd/image.png)


* n=2  
![image](/uploads/925a7954728f91341131c828b15f752c/image.png)  


* n=8  
![image](/uploads/344e60979e380221dffcd8fd493fc397/image.png)  


* n=12  
![image](/uploads/90192c12f19aed4be410ac675ba8776d/image.png)


## 4. 결과 분석  
POPULATION이 높을 수록, TOURNAMENT_SELECTION이 높을수록 수렴 속도가 빨라지는 것을 알 수 있습니다.  
다만, 적합한 개체의 수가 커질수록 수렴 속도가 빨라도 premature한 수렴이 될 위험있습니다.  

구현한 모델링의 장점:  
- 변수 값을 변화시켜 시도를 해보면서 정확도를 높이고 속도를 높일 수 있는 방향으로 진행할 수 있도록 했습니다.  
- 부모를 선정할때 이미 선정된 부모는 다시 선정하지 않아 다양한 유전자를 구성할 수 있었습니다.  
- cross over를 진행하기 위해 부모를 선정할 때 서로 다른 부모를(인덱스 다르게) 선정하도록 하였고, mutation을 진행할 때에 값의 변화가 있도록 바꾸는 값을 선정하여 다양한 세대를 구성하도록 했습니다.  
- 부모를 선정할 때 가장 좋은 fitness 값을 가진 부모만으로 선정하지 않아서 premature한 수렴이 되지 않도록 했습니다.   

구현한 모델링의 단점:  
- mutation 횟수를 높이거나 CRITERIA값이나 BestOrTournament 값의 크기에 따라서 다양성이 너무 커져서 속도가 느려질 수 있는 위험성이 있고  
반대로 fitness 값이 높은 개체들로만 이루어져서 premature한 수렴으로 이어져 답을 찾기 힘든 경우가 발생할 수도 있습니다.  
- crossover를 1-point로 구현하여 다양성이 낮아질 수 있다. 참고로 다양성이 높은 n-point 교차의 경우에는 수렴 속도가 느려질 수는 있지만 더 넓은 공간을 탐색할 수 있다는 장점이 있다. 
