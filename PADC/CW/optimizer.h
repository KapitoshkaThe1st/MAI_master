#include <unordered_map>
#include <iostream>
#include <valarray>
#include <cassert>
#include <random>
#include <algorithm>
#include <string>

template<class T, class F>
class NelderMeadOptimizer{
private:
	double alpha = 1.0;
	double beta = 0.5;
	double gamma = 2.0;
	double sigma = 0.5;

	void print_point(const T& point){
		if(!verbose)
			return;

		for(size_t j = 0; j < point.size(); ++j){
			std::cout << point[j] << " ";
		}
		std::cout << std::endl;
	}

	void print(const std::string& str){
		if(verbose)
			std::cout << str << std::endl; 
	}

    F& error_functional;
	T search_area_min, search_area_max;
    double eps;
	bool verbose;
public:
	
	NelderMeadOptimizer(F& error_functional, const T& search_area_min, const T& search_area_max, double eps = 0.01, bool verbose = false)
	: error_functional{error_functional}, search_area_min(search_area_min), search_area_max(search_area_max), eps{eps}, verbose{verbose} {
		assert(search_area_min.size() == search_area_max.size());
	}


    T optimize() {
		size_t n_parameters = search_area_max.size();
		size_t n_points = n_parameters + 1;
		std::vector<T> x(n_parameters+1);

		for(size_t i = 0; i < n_points; ++i){
			x[i] = T(n_parameters);
			for(size_t j = 0; j < n_parameters; ++j){
				x[i][j] = search_area_min[j] + (static_cast<double>(rand()) / RAND_MAX) * (search_area_max[j] - search_area_min[j]);
			}
		}

		for(auto& p : x)
			print_point(p);

		std::vector<double> err(n_points);

		bool compute_err = true;
			
		int k = 0;

        std::vector<size_t> indices(n_points);
		while(true){
			print("iteration: " + std::to_string(k));
			if(compute_err){
				for(size_t i = 0; i < n_points; ++i)
					err[i] = error_functional(x[i]);
				compute_err = false;	
			}

  			std::iota(indices.begin(), indices.end(), 0);
			std::sort(indices.begin(), indices.end(), [&](size_t i1, size_t i2) { return err[i1] < err[i2]; });

			size_t h_idx = indices[n_points-1];
			size_t g_idx = indices[n_points-2];
			size_t l_idx = indices[0];

			auto& x_h = x[h_idx];

			T x_c(0.0, n_parameters);

			for(size_t i = 0; i < n_points; ++i){
				if(i == h_idx)
					continue;
				
				x_c += x[i];
			}
			x_c /= n_points - 1;

			print("current point: ");
			print_point(x_c);

			double f_c = error_functional(x_c);
            print("error: " + std::to_string(f_c));

			double sum = 0.0;
			for(size_t i = 0; i < n_points; ++i){
				double de = err[i] - f_c;
				sum += de * de;
			}

			double conv = std::sqrt(sum / n_points);

            print("convergence: " + std::to_string(conv));

			if(conv < eps)
				return x_c;

			auto x_r = (1.0 + alpha) * x_c - alpha * x_h;
			double f_r = error_functional(x_r);

			double f_l = err[l_idx];
			double f_h = err[h_idx];
			double f_g = err[g_idx];

			bool compression_required = false;

			if(f_r < f_l){
				auto x_e = (1.0 - gamma) * x_c + gamma * x_r;
				double f_e = error_functional(x_e);

				if(f_e < f_r){
					x_h = x_e;
					err[h_idx] = f_e; 
				}
				else{
					x_h = x_r;
					err[h_idx] = f_r; 
				}
			}
			else if(f_l < f_r && f_r < f_g){
				x_h = x_r;
				err[h_idx] = f_r;
			}
			else if(f_g < f_r && f_r < f_h){
				x_h = x_r;
				err[h_idx] = f_r; 

				compression_required = true;
			}
			else if(f_h < f_r){
				compression_required = true;
			}
		
			if(compression_required){
				auto x_s = beta * x_h + (1.0 - beta) * x_c;
				double f_s = error_functional(x_s);

				if(f_s < err[h_idx]){
					x_h = x_s;
					err[h_idx] = f_s;
				}
				else{
					for(size_t i = 0; i < n_points; ++i){
						if(i == l_idx)
							continue;

						x[i] = (1.0 - sigma) * x[l_idx] + sigma * x[i];
					}
					compute_err = true;
				}
			}

			k++;
		}
    }
};
