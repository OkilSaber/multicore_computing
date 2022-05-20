#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

bool check_if_prime(int nb)
{
    if (nb == 0 || nb == 1)
        return false;
    for (int i = 2; i <= nb / 2; ++i)
        if (nb % i == 0)
            return false;
    return true;
}

int main(int argc, char **argv)
{
    if (argc != 3)
    {
        printf("Wrong number of arguments.\n");
        return -1;
    }
    int mode = atoi(argv[1]);
    int nb_threads = atoi(argv[2]);
    if (nb_threads <= 0)
        nb_threads = 1;
    int counter = 0;
    printf("Mode: %d\nNumber of threads: %d\n", mode, nb_threads);
    omp_set_num_threads(nb_threads);
    switch (mode)
    {
    case 1:
        omp_set_schedule(omp_sched_static, -1);
        break;
    case 2:
        omp_set_schedule(omp_sched_dynamic, -1);
        break;
    case 3:
        omp_set_schedule(omp_sched_static, 10);
        break;
    case 4:
        omp_set_schedule(omp_sched_dynamic, 10);
        break;
    default:
        printf("Invalid mode.\n");
        return (-1);
    }
    double start_time = omp_get_wtime();
    #pragma omp parallel for reduction(+:counter)
    for (int i = 1; i <= 200000; i++)
        if (check_if_prime(i))
            counter++;
    double end_time = omp_get_wtime();
    double total_time = end_time - start_time;
    printf("Total execution time: %.f\n", total_time * 1000);
    printf("Counter: %d\n", counter);
}