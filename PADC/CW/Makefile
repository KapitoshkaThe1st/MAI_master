all: parallel sequential

benchmark: parallel_bench sequential_bench

parallel: *.h main.cpp
	# g++ -O3 -march=native -std=c++17 -pedantic -Wall -Wextra -fopenmp -o parallel main.cpp 
	g++ -O3 -march=native -std=c++17 -pedantic -Wall -Wextra -fopenmp -o parallel main.cpp 

sequential: *.h main.cpp
	# g++ -O3 -march=native -std=c++17 -pedantic -Wall -Wextra -o sequential main.cpp 
	g++ -O0 -march=native -std=c++17 -pedantic -Wall -Wextra -o sequential main.cpp 

parallel_bench: *.h main.cpp
	# g++ -O3 -march=native -std=c++17 -pedantic -Wall -Wextra -fopenmp -o parallel main.cpp -DBENCHMARK=10
	g++ -O3 -march=native -std=c++17 -pedantic -Wall -Wextra -fopenmp -o parallel main.cpp -DBENCHMARK=10

sequential_bench: *.h main.cpp
	g++ -O3 -march=native -std=c++17 -pedantic -Wall -Wextra -o sequential main.cpp -DBENCHMARK=10
	# g++ -O0 -march=native -std=c++17 -pedantic -Wall -Wextra -o sequential main.cpp -DBENCHMARK=10

debug: *.h main.cpp
	g++ -O0 -g3 -std=c++17 -pedantic -Wall -Wextra -fsanitize=address -fsanitize=undefined -fopenmp -o parallel main.cpp

clean:
	rm parallel sequential