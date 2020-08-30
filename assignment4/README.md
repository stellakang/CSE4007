Programming Assignment4 : SAT Solver  
=================================== 

## 1. 개요    

### 1.1 SAT-solver  
주어진 boolean formula 를 충족시키는 interpretation이 존재하는가를 결정하는 문제입니다.  
존재하는 경우 -> 해당 formula는 satisfiable 하다고 판단   
존재하지 않는 경우 -> 해당 formula는 unsatisfiable 하다고 판단  

직접 모델을 통해서 검증하는 것이 아니라,  
boolean expression의 특성을 이용하여 검증하게 됩니다.  

여기서는 SAT solver z3을 통해 해를 구하였으며 python3으로 작성했습니다.  

### 1.2 Symbol, formula 정의     

Symbol  
X[i] : i열에 queen의 위치를 저장하도록 했습니다.  
예를 들어 N이 4인 경우, X[0], X[1], X[2], X[3]이 각각 값을 갖게 되며 각 열에 해당하는 queen의 위치를 저장하게 됩니다.  

Formula  
1. domain = [And(X[col] >= 1, X[col] <= N) for col in range(N)]  
queen은 체스판 안에 위치해야 하므로,  
모든 열에 대해서 X[] 값이 1과 N사이의 값을 가져야 함을 명시했습니다.  

2. con1 = [Implies(i!=j, X[i] != X[j]) for i in range(N) for j in range(N)]  
i, j에 대해서 (0~N-1) i와 j가 다르면 (서로 다른 열) queen끼리 공격이 불가능하도록 
queen의 위치도 달라야 합니다. (X[i]!=X[j])  

3. con2 = [Implies(i!=j, X[i]-X[j] != i-j) for i in range(N) for j in range(N)]  
    con3 = [Implies(i!=j, X[i]-X[j] != j-i) for i in range(N) for j in range(N)]  
i, j에 대해서 (0~N-1) i와 j가 다르면 서로 대각선 상에 있으면 안된다는 constraint를 명시해주었습니다.  


## 2. 코드 구현  
```python

start = time.time()
# Variables to assign positions of queens each column
X = [Int("x_%s" % i) for i in range(N)]

# Constraints 1 <= X[i] <= N
domain = [And(X[col] >= 1, X[col] <= N) for col in range(N)]

# Constraint1 : each column has different value
con1 = [Implies(i!=j, X[i] != X[j]) for i in range(N) for j in range(N)]
# Constraint2,3 : queens cannot be assigned on a diagonal
con2 = [Implies(i!=j, X[i]-X[j] != i-j) for i in range(N) for j in range(N)]
con3 = [Implies(i!=j, X[i]-X[j] != j-i) for i in range(N) for j in range(N)]

eight_queens_c = domain + con1 + con2 + con3

```

위와 같이 constraint를 명시하고(자세한 설명은 symbol, formula부분에서 서술했습니다. )

```python
if s.check() == sat: # if it is satisfiable
    m = s.model()
    r = [m.evaluate(X[i]) for i in range(N)]
    print(r)
    print("elapsed time:",time.time() - start," sec")
```
답이 존재하는 경우에는 위와 같이 답을 출력해주고 소요된 시간을 출력 했으며  

```python
else: # if it is unsatisfiable
    print("No solution")
    print("elapsed time: 0.0")
```
답이 존재하지 않는 경우에는 위와 같이 답이 존재하지 않음을 명시하도록 했습니다.  

## 3. 실행 결과  

N=2
![image](/uploads/73db89bf9c449cf586d1365eccad994c/image.png)

N=3
![image](/uploads/1cabe52073af6b0f385974eb9b346053/image.png)

N=10
![image](/uploads/e5cc49a02f2128d7de9b43d62c366370/image.png)

N=15
![image](/uploads/eae369880b64fd669775d73d34a1f1c4/image.png)

N=20
![image](/uploads/8deeb8e91a57c5235f954bd0cb9f794e/image.png)

N=50
![image](/uploads/155e70e5fdbc9b23ada37ec1ae3d6c79/image.png)

## 4. 결과 분석  

Optimization 방법  
 Naive한 방법은 NxN 체스판에 대해서 X[i][j]에 0과 1의 수를 대응하는 방법으로 constraint를 적용하였으나,  
해당 방법은 모든 체스판의 위치를 탐색해야 한다는 단점이 있으므로  
한 개의 열에는 한 개의 queen만 위치해야 한다는 점을 이용하여 X[i]로 variable의 차원을 줄이고 constraint를 적용했습니다.  

Naive한 방법의 경우,  
![image](/uploads/ae2c2192fdb8e941852eaa00139dcbb2/image.png)

위와 같이 나타나고

Optimization의 경우, 
![image](/uploads/b82fa59093f60c233ba5435016ed1c53/image.png)
위와 같이 나타난다. 

N=20인 경우에는  
![image](/uploads/f6199a671ff1fef20198a75d8bae3141/image.png)

약 473배의 효과를 가져왔습니다.  

이를 grapher를 통해서 그려보면  
![image](/uploads/4c088fdba75b2e896675e6246b33fa3e/image.png)
위와 같이 N이 커질수록 optimization 효과가 크게 나타나는 것을 볼 수 있습니다. 
