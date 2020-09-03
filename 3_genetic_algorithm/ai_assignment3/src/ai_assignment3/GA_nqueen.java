package ai_assignment3;

import java.util.*;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
}

public class GA_nqueen {
	private int nV;//정점의 개수 
	private int[][] currentGen;//현재 세대의 개체 모음 
	private int[][] nextGen;// 다음 세대의 개체 모음(이후 현재 세대로 복사) 
	private Boolean[] selected;// 선택되었던 부모는 다시 뽑지 않음 
	private static final int POPULATION=100000;// 한 세대당 개체 수 
	private static final int TOURNAMENT_SELECTION=50;// 부모 선택시 tournament selection에서 k값 
	private static int MOSTFIT;// 가장 적합한 경우 fitnessFunction 값 저장 
	private static int MUTATION_NUM;
	private static double CRITERIA=0.5;// tournament selection에서 사용하는 기준값 
	public static BufferedOutputStream bs = null;
	private static double beforeTime, afterTime; // 실행시간 측정(실행 전 : beforeTime, 실행 후 : afterTime)
	
	public GA_nqueen(int n) {
		nV=n;
		currentGen=new int[POPULATION][nV];
		nextGen=new int[POPULATION][nV];
		MOSTFIT=nV*(nV-1)/2;// 가장 적합한 경우 fitnessFunction 값 저장 
		selected=new Boolean[POPULATION];
		MUTATION_NUM=nV/3;// nV/3+1 만큼 실행  
	}
	
	// 처음 세대 생성 
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
	// int[]를 Position 으로 변환시키는 메소드 
	public Position[] intToPosition(int[] arr) {
		Position[] pos=new Position[nV];
		for(int i=0;i<nV;i++) {
			pos[i]= new Position(arr[i],i);
		}
//		for(int i=0;i<nV;i++)
//			System.out.println(" row column"+pos[i].getRow()+ " "+pos[i].getColumn());
		return pos;
	}
	//퀸 끼리 서로 공격이 가능한 상황에 있으면 True 반환하는 메소드 
	public static boolean isAttack(Position x, Position y) {  
    	if(x.getRow() == y.getRow() || x.getColumn() == y.getColumn() || 
    	(Math.abs(x.getColumn()-y.getColumn()) == Math.abs(x.getRow()-y.getRow())))
            return true;
        return false;
    }
	//fitness값 출력 함수 
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
	// first, second 값을 갖는 부모를 이용해서 뮤테이션 실행 
	public int[] mutationFunc(int[] child) {
		Random rand = new Random();
		int num=MUTATION_NUM;//mutation 실행 횟수 설정 
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
	
	// 난수를 생성해서 0.5보다 큰 경우에는 k개의(TOURNAMENT_SELECTION) 값 중에서 가장 큰 값을 부모로 선정하고 
	// 0.5보다 작은 경우에는 k개의(TOURNAMENT_SELECTION) 값을 선정할 때마다 난수를 생성하여 
	// 그 값이 CRITERIA보다 크면 두 개를 비교해서 좋은 값으로, 나쁘면 두 개를 비교해서 나쁜 값으로  부모를 선정
	// 다음 세대 전체 population의 10퍼센트를 이 방식으로 선정한다. 
	// k개(TOURNAMENT_SELECTION) 중에 이미 뽑힌 적이 한번이라도 있으면 다시 뽑지 않는다. (중복은 포함하지 않는다.)
	public void tourna_Selection() {
		int parents=POPULATION/10;
		
		for(int i=0;i<POPULATION;i++)
			selected[i]=false;
		for(int i=0;i<parents;i++) {
			Random rand = new Random();
			int max=-1;//선정할 부모의 fitness function 값 
			int max_indx=-1;// 선정할 부모의 currentGen에서의 인덱스 값 
			double BestOrTournament=Math.random();

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
			selected[max_indx]=true;
			//현재 세대 중에서 위의 선정방식으로 정해진 부모를 다음 세대로 추가 
			for(int j=0;j<nV;j++) {
				nextGen[i][j]=currentGen[max_indx][j];
			}
		}
	}
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
	
	public static void main(String[] args) {
		int n=Integer.parseInt(args[0]);
		try {
			bs = new BufferedOutputStream(new FileOutputStream(args[1]+"/result"+Integer.toString(n)+".txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			bs.write(">Genetic Algorithm\n".getBytes());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//답이 없는 경우 
		if(n==2 || n==3) {
			try {
    			bs.write("No solution\nTotal Elapsed Time : 0.0\n".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
					try {
						bs.write((Integer.toString(cnt)+" Generation(s) ").getBytes());
						for (int i=0; i<ga.nV; i++) {
				        	try {
				        		bs.write((Integer.toString(ga.currentGen[success][i])+" ").getBytes());
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
		    			bs.write(("Total Elapsed Time: "+Double.toString((afterTime - beforeTime)/1000)).getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				}
				ga.next_Generation();// 다음 세대 선정 
				// 다음세대를 현재 세대로 변경 
				for(int i=0;i<POPULATION;i++) {
					ga.currentGen[i]=ga.nextGen[i];
				}
				generationNum--;
			}
			if(generationNum==0)
				//만약 제시된 세대수 안에 답을 찾지 못한 경우 출력하게 했다. 
				try {
					bs.write("I couldn't find a solution in time\n".getBytes());
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
