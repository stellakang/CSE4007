from z3 import *
import time

# Number of Queens
print("N: ")
N = int(input())

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

s = Solver()
s.add(eight_queens_c)

if s.check() == sat: # if it is satisfiable
    m = s.model()
    r = [m.evaluate(X[i]) for i in range(N)]
    print(r)
    print("elapsed time:",time.time() - start," sec")
else: # if it is unsatisfiable
    print("No solution")
    print("elapsed time: 0.0")



