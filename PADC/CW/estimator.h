#pragma once

#include <array>

#include "parameters.h"
#include "values.h"
#include "lattice.h"
#include "special_matrices.h"
#include "golden_section.h"

#define EV_ANGSTROM_TO_MBAR_COEF (1.602)

class Estimator{
private:
    double initial_a;
    Lattice lattice;
    Lattice surf_lattice;

    double min_a, max_a, a_precision;

    double e_coh_a;

public:
    Estimator(int nx, int ny, int nz, double min_a, double max_a, double a_precision, double e_coh_a)
    : lattice(0.0, nx, ny, nz), surf_lattice(0.0, nx, ny, 2 * nz), min_a{min_a}, max_a{max_a}, a_precision{a_precision}, e_coh_a{e_coh_a}
    {
        surf_lattice.drop(static_cast<double>(surf_lattice.nz) / 2.0);
        surf_lattice.is_uniform = false;
    }

    void set_a(double a){
        lattice.set_a(a);
    }

    double estimate_a(const Parameters& params){
        
        auto f = [&](double cur_a) {
            lattice.set_a(cur_a);
            return lattice.cohesive_energy(params);
        };
        
        return golden_section(f, min_a, max_a, a_precision);
    }

    double estimate_cohesive_energy(const Parameters& params){
        return lattice.cohesive_energy(params);
    }

    double deriv_h = 0.001;

    mat3 deform_mat_bulk_plus = SpecialMatrices::bulk_matrix(deriv_h);
    mat3 deform_mat_bulk_minus = SpecialMatrices::bulk_matrix(-deriv_h);
    mat3 deform_mat_c11_plus = SpecialMatrices::elasticity11_matrix(deriv_h);
    mat3 deform_mat_c11_minus = SpecialMatrices::elasticity11_matrix(-deriv_h);
    mat3 deform_mat_c12_plus = SpecialMatrices::elasticity12_matrix(deriv_h);
    mat3 deform_mat_c12_minus = SpecialMatrices::elasticity12_matrix(-deriv_h);
    mat3 deform_mat_c44_plus = SpecialMatrices::elasticity44_matrix(deriv_h);
    mat3 deform_mat_c44_minus = SpecialMatrices::elasticity44_matrix(-deriv_h);

    Values estimate(const Parameters& params) {

        // рассчитываем приближенно параметр решетки a
        double a = estimate_a(params);

        // выставляем только что вычисленный параметр для обоих решеток
        lattice.set_a(a);
        surf_lattice.set_a(a);

        // рассчитываем когезионную энергию
        double e_coh = estimate_cohesive_energy(params);

        double v0 = lattice.v0(); // объем решетки
        double energy = lattice.total_energy(params); // полная энергия решетки

        // создаем вспомогательную лямбда-функцию для численного вычисления второй производной полной энергии для различных матриц деформации
        auto deriv2 = [&](const mat3& mat_minus_alpha, const mat3& mat_plus_alpha) {
            return (lattice.total_energy(params, &mat_minus_alpha) - 2.0 * energy + lattice.total_energy(params, &mat_plus_alpha)) / (deriv_h * deriv_h);
        };

        // рассчитываем вторые производные при различных деформациях
        double db = deriv2(deform_mat_bulk_minus, deform_mat_bulk_plus); // деформация для балк-модуля
        double dC11 = deriv2(deform_mat_c11_minus, deform_mat_c11_plus); // деформация для С_11
        double dC12 = deriv2(deform_mat_c12_minus, deform_mat_c12_plus); // деформация для С_12
        double dC44 = deriv2(deform_mat_c44_minus, deform_mat_c44_plus); // деформация для С_44

        // рассчитываем приближения табличных значений при текущем заданном параметре решетки
        double bulk_modulus = 1.0 / (9.0 * v0) * db * EV_ANGSTROM_TO_MBAR_COEF; // модуль всестороннего расширения (балк-модуль)
        double c11 = 0.25 / v0 * (dC11 + dC12) * EV_ANGSTROM_TO_MBAR_COEF;      // константа упругости C_11
        double c12 = 0.25 / v0 * (dC11 - dC12) * EV_ANGSTROM_TO_MBAR_COEF;      // константа упругости C_12
        double c44 = 0.25 / v0 * dC44 * EV_ANGSTROM_TO_MBAR_COEF;               // константа упругости C_44

        // заменяем атом в углу решетки примесным (тип A меняем на тип B)
        lattice.atoms[0].type = AtomType::A;
        lattice.is_uniform = false; // указываем что решетка больше не является обычной, т.к. в нее добавили примесь. Теперь расчет будет полный для всей решетки.

        double energy_ab = lattice.total_energy(params);    // полная энергия решетки с примесным атомом

        // восстанавливаем решетку до исходного состояния
        lattice.atoms[0].type = AtomType::B;
        lattice.is_uniform = true;

        // рассчитываем энергию растворимости
        double e_sol = energy_ab - energy - e_coh_a + e_coh;

        // рассчитываем поверхностную энергию решетки с поверхностью
        double surf_energy = surf_lattice.total_energy(params);

        // ищем индексы атомов в поверхностном слое
        // к сожалению, захардкожено для 3х3х3 (вот эти -1, +6), стоило бы написать процедуру для поиска соседних атомов в поверхностном слое
        int atom_on_surface_idx1 = surf_lattice.n_atoms() - ((surf_lattice.nx + 1) * (surf_lattice.ny + 1)) - 1;
        int atom_on_surface_idx2 = surf_lattice.n_atoms() - ((surf_lattice.nx + 1) * (surf_lattice.ny + 1)) + 6;

        // задаем первый примемной атом в поверхностном слое 
        surf_lattice.atoms[atom_on_surface_idx1].type = AtomType::A; 

        // рассчитываем поверхностную энергию решетки с адатомом в поверхностном слое
        double in_surf_adatom_energy = surf_lattice.total_energy(params);

        // задаем второй примемной атом в поверхностном слое 
        surf_lattice.atoms[atom_on_surface_idx2].type = AtomType::A;

        // рассчитываем поверхностную энергию решетки с димером в поверхностном слое
        double in_surf_dim_energy = surf_lattice.total_energy(params);

        // рассчитываем энергию связи димера в поверхностном слое
        double e_dim_in = (in_surf_dim_energy - surf_energy) - 2.0 * (in_surf_adatom_energy - surf_energy);

        // восстанавливаем решетку до исходного состояния
        surf_lattice.atoms[atom_on_surface_idx1].type = AtomType::B;
        surf_lattice.atoms[atom_on_surface_idx2].type = AtomType::B;

        // рассчитываем z-координату атомов на поверхности
        double on_surface_z = (static_cast<double>(surf_lattice.nz) / 2.0 + 0.5);

        // добавляем первый примесной атом на поверхность
        surf_lattice.atoms.push_back(Atom(vec3(1.0, 0.5, on_surface_z), AtomType::A));

        // рассчитываем поверхностную энергию решетки с адатомом на поверхности
        double on_surf_adatom_energy = surf_lattice.total_energy(params);

        // добавляем второй примесной атом на поверхность
        surf_lattice.atoms.push_back(Atom(vec3(0.5, 1.0, on_surface_z), AtomType::A));

        // рассчитываем поверхностную энергию решетки с димером на поверхности
        double on_surf_dim_energy = surf_lattice.total_energy(params);

        // рассчитываем энергию связи димера на поверхности
        double e_dim_on = (on_surf_dim_energy - surf_energy) - 2.0 * (on_surf_adatom_energy - surf_energy);

        // восстанавливаем решетку до исходного состояния
        surf_lattice.atoms.pop_back();
        surf_lattice.atoms.pop_back();

        return Values(a, e_coh, bulk_modulus, c11, c12, c44, e_sol, e_dim_in, e_dim_on);
    }
};
